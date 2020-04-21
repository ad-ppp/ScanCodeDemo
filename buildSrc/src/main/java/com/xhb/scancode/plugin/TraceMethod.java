package com.xhb.scancode.plugin;

import org.objectweb.asm.Opcodes;

public class TraceMethod {
    private static final String TAG = "Matrix.TraceMethod";
    public int id;
    public int accessFlag;
    public String className;
    public String methodName;
    public String desc;

    public static TraceMethod create(int id, int accessFlag, String className, String methodName, String desc) {
        TraceMethod traceMethod = new TraceMethod();
        traceMethod.id = id;
        traceMethod.accessFlag = accessFlag;
        traceMethod.className = className.replace("/", ".");
        traceMethod.methodName = methodName;
        traceMethod.desc = desc.replace("/", ".");
        return traceMethod;
    }

    public String getMethodName() {
        if (desc == null || isNativeMethod()) {
            return this.className + "." + this.methodName;
        } else {
            return this.className + "." + this.methodName + "." + desc;
        }
    }

    @Override
    public String toString() {
        if (desc == null || isNativeMethod()) {
            return id + "," + accessFlag + "," + className + " " + methodName;
        } else {
            return id + "," + accessFlag + "," + className + " " + methodName + " " + desc;
        }
    }

    public boolean isNativeMethod() {
        return (accessFlag & Opcodes.ACC_NATIVE) != 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TraceMethod) {
            TraceMethod tm = (TraceMethod) obj;
            return tm.getMethodName().equals(getMethodName());
        } else {
            return false;
        }
    }
}
