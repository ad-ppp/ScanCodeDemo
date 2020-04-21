package com.xhb.scancode.plugin.classio.module;

import com.xhb.scancode.plugin.base.Checkable;

import java.util.List;

public class MeNode implements Checkable {
    private String className;
    private String methodName;
    private List<String> insnNodes;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getInsnNodes() {
        return insnNodes;
    }

    public void setInsnNodes(List<String> insnNodes) {
        this.insnNodes = insnNodes;
    }

    @Override
    public boolean check() {
        return className != null && !className.equals("") &&
                methodName != null && !methodName.equals("");
    }
}
