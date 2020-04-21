package com.xhb.scancode.plugin.classio.module;

import java.util.List;

public class JarNode {
    private String output;
    private String input;
    private List<String> entryNames;

    public JarNode(String input, String output) {
        this.input = input;
        this.output = output;
    }

    public List<String> getEntryNames() {
        return entryNames;
    }

    public void setEntryNames(List<String> entryNames) {
        this.entryNames = entryNames;
    }

    public String getOutput() {
        return output;
    }

    public String getInput() {
        return input;
    }
}
