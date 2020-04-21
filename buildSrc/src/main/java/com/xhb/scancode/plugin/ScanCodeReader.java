package com.xhb.scancode.plugin;

import com.google.gson.Gson;
import com.xhb.scancode.plugin.base.IScanCodeNodeProvider;
import com.xhb.scancode.plugin.classio.module.ScanCodeNode;
import com.xhb.scancode.plugin.util.Log;
import com.xhb.scancode.plugin.util.Util;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.concurrent.CountDownLatch;

public class ScanCodeReader implements IScanCodeNodeProvider {
    private File scanCodeConfigFile;
    private ScanCodeNode scanCodeNode;
    private final String SCAN_INSN_NODE = "scanInsnNode";
    private final String TAG = "ScanCodeReader";
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    public ScanCodeReader(File scanCodeConfigFile) {
        this.scanCodeConfigFile = scanCodeConfigFile;
        this.read();
    }

    private void read() {
        LineNumberReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(scanCodeConfigFile));
            reader = new LineNumberReader(bufferedReader);

            String line;
            int muteCount = 0;
            StringBuilder sb = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("#")) {
                    boolean startToken = false;
                    if (line.startsWith(SCAN_INSN_NODE) && line.endsWith("{")) {
                        muteCount++;
                        startToken = true;
                        sb = new StringBuilder("{");
                    }

                    if (muteCount > 0 && !startToken) {
                        if (line.endsWith("{")) {
                            muteCount++;
                        }

                        sb.append(line);
                    }

                    // "}" or ",}"
                    if (muteCount > 0 && (line.endsWith("}") || line.startsWith("}"))) {
                        muteCount--;

                        if (muteCount == 0) {
                            scanCodeNode = new Gson().fromJson(sb.toString(), ScanCodeNode.class);
                            Log.i(TAG, "scan code node :%s", scanCodeNode.toString());
                        }
                    }
                } else {
                    Log.i("ScanCodeReader", "comment: %s", line);
                }
            }
        } catch (FileNotFoundException e) {
            Log.printErrStackTrace(TAG, e, "file[%s] not found", this.scanCodeConfigFile.getAbsolutePath());
        } catch (IOException e) {
            Log.printErrStackTrace(TAG, e, "IOException[%s]", this.scanCodeConfigFile.getAbsolutePath());
        } finally {
            countDownLatch.countDown();
            Util.closeQuietly(bufferedReader);
            Util.closeQuietly(reader);
        }
    }

    @Nullable
    @Override
    public ScanCodeNode provideScanCoeNode() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return scanCodeNode;
    }
}
