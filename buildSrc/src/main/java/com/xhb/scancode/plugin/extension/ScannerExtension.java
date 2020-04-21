package com.xhb.scancode.plugin.extension;

public class ScannerExtension {
    private String pluginName = "";
    private String scanCodeConfigPath = "";
    private String scanClasspathOutput = "";
    private String scanMethodOutput = "";
    private String jarEntryNameOutput = "";

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getScanCodeConfigPath() {
        return scanCodeConfigPath;
    }

    public void setScanCodeConfigPath(String scanCodeConfigPath) {
        this.scanCodeConfigPath = scanCodeConfigPath;
    }

    public String getScanClasspathOutput() {
        return scanClasspathOutput;
    }

    public void setScanClasspathOutput(String scanClasspathOutput) {
        this.scanClasspathOutput = scanClasspathOutput;
    }

    public String getScanMethodOutput() {
        return scanMethodOutput;
    }

    public void setScanMethodOutput(String scanMethodOutput) {
        this.scanMethodOutput = scanMethodOutput;
    }

    public String getJarEntryNameOutput() {
        return jarEntryNameOutput;
    }

    public void setJarEntryNameOutput(String jarEntryNameOutput) {
        this.jarEntryNameOutput = jarEntryNameOutput;
    }

    @Override
    public String toString() {
        return "ScannerExtension{" +
                "pluginName='" + pluginName + '\'' +
                ", scanCodeConfigPath='" + scanCodeConfigPath + '\'' +
                ", scanClasspathOutput='" + scanClasspathOutput + '\'' +
                ", scanMethodOutput='" + scanMethodOutput + '\'' +
                ", jarEntryNameOutput='" + jarEntryNameOutput + '\'' +
                '}';
    }
}
