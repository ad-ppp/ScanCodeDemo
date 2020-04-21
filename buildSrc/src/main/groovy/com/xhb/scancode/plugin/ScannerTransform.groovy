package com.xhb.scancode.plugin

import com.android.annotations.NonNull
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.xhb.scancode.plugin.util.Log
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project

public class ScannerTransform extends Transform {
    private final String TAG = "ScannerTransform"
    private Project project
    private long startTime

    ScannerTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return project.scannerConfig.pluginName
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    public void transform(@NonNull TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        Log.i(TAG, "start %s transform", getName())
        startTime = System.currentTimeMillis()

        Collection<TransformInput> inputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider

        Map<File, File> jarInputMap = new HashMap<>()
        Map<File, File> srcInputMap = new HashMap<>()
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                srcInputMap.put(directoryInput.file, dest)
            }
            input.jarInputs.each { JarInput jarInput ->
                String destName = jarInput.file.name
                /** 重名名输出文件,因为可能同名,会覆盖*/
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8)
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4)
                }
                File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                jarInputMap.put(jarInput.file, dest)

                dest.getParentFile().deleteDir()
            }
        }

        final ScanCodeReader scanCodeReader = new ScanCodeReader(new File(project.scannerConfig.scanCodeConfigPath))
        final MethodTracer methodTracer = new MethodTracer(project, scanCodeReader)
        methodTracer.trace(srcInputMap, jarInputMap)

        Log.i(TAG, "transform %s cost %d ms", getName(), (System.currentTimeMillis() - startTime))
    }
}