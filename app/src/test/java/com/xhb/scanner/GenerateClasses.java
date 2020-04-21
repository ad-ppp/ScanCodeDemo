package com.xhb.scanner;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * 使用asm生成字节码示例
 * treeApi vs coreApi
 */
public class GenerateClasses {
    private final String classN = "bytecode/TreeMethodGenClass";
    private final String classN2 = "bytecode/TreeMethodGenClass1";
    private final File file = new File("src/test/java/bytecode/TreeMethodGenClass.class");
    private final File file2 = new File("src/test/java/bytecode/TreeMethodGenClass1.class");


    @Test
    public void test_generate() throws Exception {

        ClassNode classNode = new ClassNode();
        classNode.version = Opcodes.V1_8;
        classNode.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL;
        classNode.name = classN;
        classNode.superName = "java/lang/Object";
        classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, "espresso", "I", null, null));
        // public void addEspresso(int espresso) 方法生命
        MethodNode mn = new MethodNode(ACC_PUBLIC, "addEspresso", "(I)V", null, null);
        classNode.methods.add(mn);
        InsnList il = mn.instructions;
        il.add(new VarInsnNode(Opcodes.ILOAD, 1));
        il.add(new InsnNode(Opcodes.ICONST_3));
        LabelNode label = new LabelNode();
        // if (espresso > 0) 跳转通过LabelNode标记跳转地址
        il.add(new JumpInsnNode(Opcodes.IF_ICMPLE, label));
        il.add(new VarInsnNode(Opcodes.ALOAD, 0));
        il.add(new VarInsnNode(Opcodes.ILOAD, 1));
        // this.espresso = var1;
        il.add(new FieldInsnNode(Opcodes.PUTFIELD, "bytecode/TreeMethodGenClass", "espresso", "I"));
        LabelNode end = new LabelNode();
        il.add(new JumpInsnNode(Opcodes.GOTO, end));
        // label 后紧跟着下一个指令地址
        il.add(label);
        // java7之后对stack map frame 的处理
        il.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
        // throw new IllegalArgumentException();
        il.add(new TypeInsnNode(Opcodes.NEW, "java/lang/IllegalArgumentException"));
        il.add(new InsnNode(Opcodes.DUP));
        il.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "()V", false));
        il.add(new InsnNode(Opcodes.ATHROW));
        il.add(end);
        // stack map 的第二次偏移记录
        il.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
        il.add(new InsnNode(Opcodes.RETURN));
        // 局部变量表和操作数栈大小的处理
        mn.maxStack = 2;
        mn.maxLocals = 2;
        mn.visitEnd();

        // 打印查看class的生成结果
        ClassWriter cw = new ClassWriter(Opcodes.ASM5);
        classNode.accept(cw);
        FileOutputStream fout = new FileOutputStream(file);
        try {
            fout.write(cw.toByteArray());
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // test
        final ClassReader classReader = new ClassReader(new FileInputStream(file));
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
    }

    @Test
    public void test_generate2() throws Exception {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, classN2, null, "java/lang/Object", null);

        cw.visitSource("TreeMethodGenClass1.java", null);

        {
            fv = cw.visitField(ACC_PRIVATE, "espresso", "I", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(3, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "Lbytecode/TreeMethodGenClass1;", null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "addEspresso", "(I)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(7, l0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitIntInsn(BIPUSH, 30);
            Label l1 = new Label();
            mv.visitJumpInsn(IF_ICMPLE, l1);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLineNumber(8, l2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitFieldInsn(PUTFIELD, "bytecode/TreeMethodGenClass1", "espresso", "I");
            Label l3 = new Label();
            mv.visitJumpInsn(GOTO, l3);
            mv.visitLabel(l1);
            mv.visitLineNumber(10, l1);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "()V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l3);
            mv.visitLineNumber(12, l3);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitLocalVariable("this", "Lbytecode/TreeMethodGenClass1;", null, l0, l4, 0);
            mv.visitLocalVariable("var1", "I", null, l0, l4, 1);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();

        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(file2);
            fileOutputStream.write(cw.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
