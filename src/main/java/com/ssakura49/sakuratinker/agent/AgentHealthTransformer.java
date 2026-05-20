package com.ssakura49.sakuratinker.agent;

import com.ssakura49.sakuratinker.agent.helper.AgentHelper;
import com.ssakura49.sakuratinker.agent.helper.SafeClassWriter;
import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class AgentHealthTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (AgentHelper.blackClass(className)) {
            return null;
        } else {
            try {
                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new SafeClassWriter(cr, 2, loader);
                ClassVisitor cv = new AgentHealthClassVisitor(589824, cw);
                cr.accept(cv, 4);
                return cw.toByteArray();
            } catch (Exception e) {
                System.err.println("[SakuraTinker] 转换类失败: " + className);
                e.printStackTrace();
                return null;
            }
        }
    }

    static class AgentHealthClassVisitor extends ClassVisitor {
        public AgentHealthClassVisitor(int api, ClassVisitor cv) {
            super(api, cv);
        }

        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if ((name.equals("getHealth") || name.equals("ssGetHealth") || name.equals("m_21223_")) && descriptor.equals("()F")) {
                return new HealthMethodVisitor(this.api, mv, true);
            } else {
                return (MethodVisitor)((name.equals("getMaxHealth") || name.equals("m_21233_")) && descriptor.equals("()F") ? new HealthMethodVisitor(this.api, mv, false) : mv);
            }
        }

        static class HealthMethodVisitor extends MethodVisitor {
            private final boolean isGetHealth;

            public HealthMethodVisitor(int api, MethodVisitor mv, boolean isGetHealth) {
                super(api, mv);
                this.isGetHealth = isGetHealth;
            }
            @Override
            public void visitCode() {
                String helperClass = "com/ssakura49/sakuratinker/agent/HealthMethod/AgentHealthMethodHelper";
                String helperMethod = this.isGetHealth ? "agentGetHealth" : "agentGetMaxHealth";
                Label executeOriginal = new Label();
                this.mv.visitVarInsn(Opcodes.ALOAD, 0);
                this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, helperClass, helperMethod, "(Ljava/lang/Object;)F", false);
                this.mv.visitLdcInsn(-1.0F);
                this.mv.visitInsn(Opcodes.FCMPL);
                this.mv.visitJumpInsn(Opcodes.IFEQ, executeOriginal);
                this.mv.visitVarInsn(Opcodes.ALOAD, 0);
                this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, helperClass, helperMethod, "(Ljava/lang/Object;)F", false);
                this.mv.visitInsn(Opcodes.FRETURN);
                this.mv.visitLabel(executeOriginal);
                super.visitCode();
            }
        }
    }
}
