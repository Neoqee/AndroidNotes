package com.neoqee.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LifecycleClassVisitor extends ClassVisitor {

    private String className;
    private String superName;

    public LifecycleClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    @Override
    public void visit(int i, int i1, String s, String s1, String s2, String[] strings) {
        super.visit(i, i1, s, s1, s2, strings);
        this.className = s;
        this.superName = s2;
    }

    @Override
    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
        System.out.println("ClassVisitor visitMethod name -----> " + s + ", supperName is " + superName);
        MethodVisitor mv = cv.visitMethod(i, s, s1, s2, strings);

        if (superName.equals("androidx/appcompat/app/AppCompatActivity")) {
            if (s.startsWith("onCreate")){
                return new LifecycleMethodVisitor(mv,className,s);
            }
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
