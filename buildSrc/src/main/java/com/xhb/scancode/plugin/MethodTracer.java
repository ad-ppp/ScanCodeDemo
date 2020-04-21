package com.xhb.scancode.plugin;

import com.google.common.base.Strings;
import com.xhb.scancode.plugin.base.IScanCodeNodeProvider;
import com.xhb.scancode.plugin.classio.IoLogic;
import com.xhb.scancode.plugin.classio.TraceClassAdapter;
import com.xhb.scancode.plugin.classio.module.JarNode;
import com.xhb.scancode.plugin.classio.module.MeNode;
import com.xhb.scancode.plugin.extension.ScannerExtension;
import com.xhb.scancode.plugin.util.Log;
import com.xhb.scancode.plugin.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

public class MethodTracer {
    private final static String TAG = "MethodTracer";
    private final Project project;
    private final ScannerExtension scannerExtension;
    private final IScanCodeNodeProvider scanCodeNodeProvider;

    private final List<String> clazzes = new ArrayList<>();
    private final List<String> iPath = new ArrayList<>();
    private final List<String> oPath = new ArrayList<>();
    private final List<MeNode> meNodes = new ArrayList<>();
    private final List<JarNode> jarNodes = new ArrayList<>();


    public MethodTracer(Project project, IScanCodeNodeProvider scanCodeNodeProvider) {
        this.project = project;
        this.scanCodeNodeProvider = scanCodeNodeProvider;
        this.scannerExtension = project.getExtensions().getByType(ScannerExtension.class);
    }

    public void trace(Map<File, File> srcFolderList, Map<File, File> dependencyJarList) {
        traceMethodFromSrc(srcFolderList);
        traceMethodFromJar(dependencyJarList);

        if (!Strings.isNullOrEmpty(scannerExtension.getScanClasspathOutput())) {
            IoLogic.writeClassOutput(new File(scannerExtension.getScanClasspathOutput())
                    , clazzes, iPath, oPath);
        }
        if (!Strings.isNullOrEmpty(scannerExtension.getScanMethodOutput())) {
            IoLogic.writeMethodOutput(new File(scannerExtension.getScanMethodOutput()), meNodes);
        }
        if (!Strings.isNullOrEmpty(scannerExtension.getJarEntryNameOutput())) {
            IoLogic.writeJarEntryOutput(new File(scannerExtension.getJarEntryNameOutput()), jarNodes);
        }
    }

    private void traceMethodFromSrc(Map<File, File> srcMap) {
        if (null != srcMap) {
            for (Map.Entry<File, File> entry : srcMap.entrySet()) {
                innerTraceMethodFromSrc(entry.getKey(), entry.getValue());
            }
        }
    }

    private void traceMethodFromJar(Map<File, File> dependencyMap) {
        if (null != dependencyMap) {
            for (Map.Entry<File, File> entry : dependencyMap.entrySet()) {
                innerTraceMethodFromJar(entry.getKey(), entry.getValue());
            }
        }
    }

    private void innerTraceMethodFromSrc(File input, File output) {
        List<File> allFiles = new ArrayList<>();
        if (input.isDirectory()) {
            listClassFiles(allFiles, input);
        } else {
            allFiles.add(input);
        }

        for (File file : allFiles) {
            InputStream is = null;
            FileOutputStream os = null;
            final String classFullPath = file.getAbsolutePath();
            final File changedFileOutput = new File(classFullPath.replace(input.getAbsolutePath(), output.getAbsolutePath()));
            if (!changedFileOutput.exists()) {
                changedFileOutput.getParentFile().mkdirs();
            } else {
                changedFileOutput.delete();
            }

            try {
                boolean isNeedCopy = true;
                if (changedFileOutput.getAbsolutePath().endsWith(".class")) {
                    String classPkg = classFullPath.replaceAll(input.getAbsolutePath() + File.separator, "").replaceAll(File.separator, ".");
                    if (Util.isNeedTraceClass(file.getName())) {
                        changedFileOutput.createNewFile();
                        addPath(classPkg, classFullPath, changedFileOutput);

                        is = new FileInputStream(file);
                        final byte[] bytes = IOUtils.toByteArray(is);
                        final byte[] scan = scan(bytes);

                        if (output.isDirectory()) {
                            os = new FileOutputStream(changedFileOutput);
                        } else {
                            os = new FileOutputStream(output);
                        }

                        isNeedCopy = false;
                        os.write(scan);
                    }
                }

                if (isNeedCopy) {
                    Util.copyFileUsingStream(file, changedFileOutput);
                }
            } catch (Exception e) {
                Log.e(TAG, "innerTraceMethodFromSrc exception: [e:%s]", String.valueOf(e.getMessage()));
            } finally {
                Util.closeQuietly(os);
                Util.closeQuietly(is);
            }
        }
    }

    private void innerTraceMethodFromJar(File input, File output) {
        JarOutputStream jarOutputStream = null;
        JarFile jarFile = null;
        final JarNode jarNode = new JarNode(input.getAbsolutePath(), output.getAbsolutePath());
        try {
            if (output.exists()) {
                FileUtils.deleteQuietly(output);
            }
            final List<String> entryNames = new ArrayList<>();
            jarOutputStream = new JarOutputStream(new FileOutputStream(output));
            jarFile = new JarFile(input);
            Enumeration enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement();
                InputStream inputStream = jarFile.getInputStream(jarEntry);

                // entryName => com/alibaba/android/arouter/routes/ARouter$$Group$$module_material.class
                final String entryName = jarEntry.getName();
                final String classPkg = entryName.replaceAll(File.separator, ".");
                byte[] bytes = IOUtils.toByteArray(inputStream);
                if (Util.isNeedTraceClass(classPkg)) {
                    addPath(classPkg, input.getAbsolutePath(), output.getAbsoluteFile());
                    bytes = scan(bytes);
                }
                entryNames.add(entryName);
                inputStream = new ByteArrayInputStream(bytes);
                Util.addZipEntry(jarOutputStream, new ZipEntry(entryName), inputStream);
            }
            jarNode.setEntryNames(entryNames);
            jarNodes.add(jarNode);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Util.closeQuietly(jarFile);
            Util.closeQuietly(jarOutputStream);
        }
    }

    private void addPath(String classPkg, String classFullPath, File changedFileOutput) {
        clazzes.add(classPkg);
        iPath.add(classFullPath);
        oPath.add(changedFileOutput.getAbsolutePath());
    }

    private void listClassFiles(List<File> classFiles, File folder) {
        File[] files = folder.listFiles();
        if (null == files) {
            return;
        }
        for (File file : files) {
            if (file == null) {
                continue;
            }
            if (file.isDirectory()) {
                listClassFiles(classFiles, file);
            } else {
                if (file.isFile()) {
                    classFiles.add(file);
                }
            }
        }
    }

    @NotNull
    private byte[] scan(byte[] bytes) {
        ClassWriter classWriter;
        try {
            ClassReader classReader = new ClassReader(bytes);
            classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            TraceClassAdapter classVisitor;
            // method node
            classVisitor = new TraceClassAdapter(false, scanCodeNodeProvider, Opcodes.ASM5, null);
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
            meNodes.addAll(classVisitor.getMeNodes());

            // write class
            classVisitor = new TraceClassAdapter(true, scanCodeNodeProvider, Opcodes.ASM5, classWriter);
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        } catch (Exception e) {
            Log.printErrStackTrace(TAG, e, "scan exception");
        }

        return bytes;
    }
}
