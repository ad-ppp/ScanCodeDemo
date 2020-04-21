package com.xhb.scancode.plugin.classio;

import com.xhb.scancode.plugin.MethodTracer;
import com.xhb.scancode.plugin.classio.module.JarNode;
import com.xhb.scancode.plugin.classio.module.MeNode;
import com.xhb.scancode.plugin.util.Log;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IoLogic {
    private final static String TAG = "IoLogic";

    /**
     * writeClassOutput scan information to file
     */
    public static void writeClassOutput(File file, List<String> clazzes, List<String> iPath, List<String> oPath) {
        try {
            if (file.exists()) {
                file.delete();
            }


            final PrintWriter printWriter = new PrintWriter(file);
            printWriter.write(String.format("generate by scanner plugin [date:%s]\n\n", new Date(System.currentTimeMillis())));
            for (int i = 0; i < clazzes.size(); i++) {
                printWriter.write("class:" + clazzes.get(i) + "\n");
                printWriter.write("i:" + iPath.get(i) + "\n");
                printWriter.write("o:" + oPath.get(i) + "\n");
                printWriter.append("\n");
            }
            printWriter.flush();
        } catch (Exception e) {
            Log.printErrStackTrace(TAG, e, "writeClassOutput file[%s] error", file.getAbsolutePath());
        }
    }

    public static void writeMethodOutput(final File file,final List<MeNode> meNodes) {
        try {
            if (file.exists()) {
                file.delete();
            }

            final PrintWriter printWriter = new PrintWriter(file);
            printWriter.write(String.format("generate by scanner plugin [date:%s]\nexclude empty set and get method\n\n", new Date(System.currentTimeMillis())));
            for (MeNode meNode : meNodes) {
                printWriter.write("class:" + meNode.getClassName() + "\n");
                printWriter.write("method:" + meNode.getMethodName() + "\n");
                final List<String> insnNodes = meNode.getInsnNodes();
                if (insnNodes != null) {
                    for (String insnNode : insnNodes) {
                        printWriter.write("insn:" + insnNode + "\n");
                    }
                }
                printWriter.append("\n");
            }
            printWriter.append("\n");
            printWriter.flush();
        } catch (Exception e) {
            Log.printErrStackTrace(TAG, e, "writeMethodOutput file[%s] error", file.getAbsolutePath());
        }
    }

    public static void writeJarEntryOutput(final File file, List<JarNode> jarNodes) {
        try {
            if (file.exists()) {
                file.delete();
            }

            final PrintWriter printWriter = new PrintWriter(file);
            printWriter.write(String.format("generate by scanner plugin [date:%s]\njar file log:\n\n", new Date(System.currentTimeMillis())));

            for (JarNode jarNode : jarNodes) {
                int entryId = 1;

                printWriter.write(String.format("jar\n[i:%s]\n[o:%s]\n", jarNode.getInput(), jarNode.getOutput()));
                for (String entryName : jarNode.getEntryNames()) {
                    printWriter.write(String.format(Locale.ENGLISH, "entryName[%d][%s]\n", entryId++, entryName));
                }
                printWriter.append("\n");
            }
            printWriter.append("\n");
            printWriter.flush();
        } catch (Exception e) {
            Log.printErrStackTrace(TAG, e, "writeMethodOutput file[%s] error", file.getAbsolutePath());
        }
    }
}
