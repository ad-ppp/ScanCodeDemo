package com.xhb.scancode.plugin.classio;

import com.xhb.scancode.plugin.TraceMethod;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

public class TraceMethodAdapter extends AdviceAdapter {
    private final String methodName;
    private final String name;
    private final String className;

    public TraceMethodAdapter(int api, MethodVisitor mv, int access, String name, String desc, String className) {
        super(api, mv, access, name, desc);
        TraceMethod traceMethod = TraceMethod.create(0, access, className, name, desc);

        this.methodName = traceMethod.getMethodName();
        this.className = className;
        this.name = name;
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
    }
}
