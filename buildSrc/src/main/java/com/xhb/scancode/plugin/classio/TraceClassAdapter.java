package com.xhb.scancode.plugin.classio;

import com.xhb.scancode.plugin.TraceMethod;
import com.xhb.scancode.plugin.base.IScanCodeNodeProvider;
import com.xhb.scancode.plugin.classio.module.MeNode;
import com.xhb.scancode.plugin.classio.module.ScanCodeNode;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class TraceClassAdapter extends ClassVisitor {
    private final String TAG = "SingleTraceClassAdapter";
    private String className;
    private boolean isABSClass = false;
    private boolean isSingle;
    private final List<MeNode> meNodes = new ArrayList<>();
    private final IScanCodeNodeProvider scanCodeNodeProvider;

    public TraceClassAdapter(boolean isSingle, IScanCodeNodeProvider scanCodeNodeProvider, int i, ClassVisitor classVisitor) {
        super(i, classVisitor);
        this.scanCodeNodeProvider = scanCodeNodeProvider;
        this.isSingle = isSingle;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

        this.className = name;
        if ((access & Opcodes.ACC_ABSTRACT) > 0 || (access & Opcodes.ACC_INTERFACE) > 0) {
            this.isABSClass = true;
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (isSingle) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        final CollectMethodNode collectMethodNode = new CollectMethodNode(className, access, name, desc, signature, exceptions);
        final MeNode meNode = collectMethodNode.getMeNode();
        meNodes.add(meNode);
        return collectMethodNode;
    }

    @NotNull
    public List<MeNode> getMeNodes() {
        return meNodes;
    }


    private class CollectMethodNode extends MethodNode {
        private String className;
        private boolean isConstructor;
        private AtomicInteger id = new AtomicInteger();
        private final ScanCodeNode scanCodeNode;
        private MeNode meNode = new MeNode();

        CollectMethodNode(String className, int access, String name, String desc, String signature, String[] exceptions) {
            super(Opcodes.ASM5, access, name, desc, signature, exceptions);
            this.className = className;
            scanCodeNode = scanCodeNodeProvider != null ? scanCodeNodeProvider.provideScanCoeNode() : null;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            TraceMethod traceMethod = TraceMethod.create(0, access, className, name, desc);
            final String methodName = traceMethod.getMethodName();

            if ("<init>".equals(name) /*|| "<clinit>".equals(name)*/) {
                isConstructor = true;
            }

            // filter simple methods
            if ((isEmptyMethod() || isGetSetMethod() || isSingleMethod())) {
                return;
            }

            meNode.setClassName(className);
            meNode.setMethodName(methodName);

            final List<String> insnNodes = new ArrayList<>();

            final ListIterator<AbstractInsnNode> iterator = instructions.iterator();
            while (iterator.hasNext()) {
                final AbstractInsnNode next = iterator.next();
                insnNodes.add(analyzeInsnNode(next));

                if (scanCodeNode == null) {
                    continue;
                }

//                if (next instanceof FieldInsnNode) {
//                    final FieldInsnNode fieldInsnNode = (FieldInsnNode) next;
//
//                    final ScanCodeNode.FieldNode field = scanCodeNode.getField();
//                    if (field!=null) {
//                        if (field.getOwner().equals(fieldInsnNode.owner))
//                    }
//                } else if (next instanceof LineNumberNode) {
//                    final LineNumberNode lineNumberNode = (LineNumberNode) next;
//                    lineNumber = lineNumberNode.line;
//                }
            }
            meNode.setInsnNodes(insnNodes);
        }

        private String analyzeInsnNode(AbstractInsnNode next) {
            final String base = String.format(Locale.ENGLISH, "[%d][%s][opcode:%d,0x:%x][type:%d]",
                    id.incrementAndGet(), next.getClass().getSimpleName(), next.getOpcode(), next.getOpcode(), next.getType()
            );

            if (next instanceof FieldInsnNode) {
                final FieldInsnNode fieldInsnNode = (FieldInsnNode) next;
                return base + String.format(Locale.ENGLISH, "\t[owner:%s][name:%s][desc:%s]",
                        fieldInsnNode.owner, fieldInsnNode.name, fieldInsnNode.desc
                );
            } else if (next instanceof LineNumberNode) {
                final LineNumberNode lineNumberNode = (LineNumberNode) next;
                return base + String.format(Locale.ENGLISH, "\t[line:%d]",
                        lineNumberNode.line
                );
            } else if (next instanceof MethodInsnNode) {
                final MethodInsnNode methodInsnNode = (MethodInsnNode) next;
                return base + String.format(Locale.ENGLISH, "\t[owner:%s][name:%s][desc:%s][itf:%b]",
                        methodInsnNode.owner, methodInsnNode.name, methodInsnNode.desc, methodInsnNode.itf
                );
            } else if (next instanceof LdcInsnNode) {
                final LdcInsnNode ldcInsnNode = (LdcInsnNode) next;
                return base + String.format(Locale.ENGLISH, "\t[cst:%s]",
                        ldcInsnNode.cst.toString()
                );
            } else if (next instanceof VarInsnNode) {
                final VarInsnNode varInsnNode = (VarInsnNode) next;
                return base + String.format(Locale.ENGLISH, "\t[var:%d]",
                        varInsnNode.var
                );
            } else if (next instanceof TypeInsnNode) {
                final TypeInsnNode typeInsnNode = (TypeInsnNode) next;
                return base + String.format(Locale.ENGLISH, "\t[desc:%s]",
                        typeInsnNode.desc
                );
            } else if (next instanceof IntInsnNode) {
                final IntInsnNode intInsnNode = (IntInsnNode) next;
                return base + String.format(Locale.ENGLISH, "\t[operand:%d]",
                        intInsnNode.operand
                );
            }


            return base;
        }

        private boolean isGetSetMethod() {
            int ignoreCount = 0;
            ListIterator<AbstractInsnNode> iterator = instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode insnNode = iterator.next();
                int opcode = insnNode.getOpcode();
                if (-1 == opcode) {
                    continue;
                }
                if (opcode != Opcodes.GETFIELD
                        && opcode != Opcodes.GETSTATIC
                        && opcode != Opcodes.H_GETFIELD
                        && opcode != Opcodes.H_GETSTATIC

                        && opcode != Opcodes.RETURN
                        && opcode != Opcodes.ARETURN
                        && opcode != Opcodes.DRETURN
                        && opcode != Opcodes.FRETURN
                        && opcode != Opcodes.LRETURN
                        && opcode != Opcodes.IRETURN

                        && opcode != Opcodes.PUTFIELD
                        && opcode != Opcodes.PUTSTATIC
                        && opcode != Opcodes.H_PUTFIELD
                        && opcode != Opcodes.H_PUTSTATIC
                        && opcode > Opcodes.SALOAD) {
                    if (isConstructor && opcode == Opcodes.INVOKESPECIAL) {
                        ignoreCount++;
                        if (ignoreCount > 1) {
                            return false;
                        }
                        continue;
                    }
                    return false;
                }
            }
            return true;
        }

        private boolean isSingleMethod() {
            ListIterator<AbstractInsnNode> iterator = instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode insnNode = iterator.next();
                int opcode = insnNode.getOpcode();
                if (-1 != opcode) {
                    if (Opcodes.INVOKEVIRTUAL <= opcode && opcode <= Opcodes.INVOKEDYNAMIC) {
                        return false;
                    }
                }
            }
            return true;
        }

        private boolean isEmptyMethod() {
            ListIterator<AbstractInsnNode> iterator = instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode insnNode = iterator.next();
                int opcode = insnNode.getOpcode();
                if (-1 != opcode) {
                    return false;
                }
            }
            return true;
        }

        public MeNode getMeNode() {
            return meNode;
        }
    }
}
