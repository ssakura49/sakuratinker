package com.ssakura49.sakuratinker.agent.HiddenClass;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class HiddenClassTransformer implements ClassFileTransformer {
    private static final String[] WHITELIST_PACKAGES = new String[]{"com.ssakura49.sakuratinker", "net.minecraftforge.fml", "cpw.mods.modlauncher", "net.minecraftforge.registries", "org.spongepowered.asm.mixin"};

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className == null) {
            return classfileBuffer;
        } else {
            String normalizedName = className.replace('/', '.');
            if (HiddenClassManager.shouldProtect(normalizedName)) {
                if (this.isInternalAccess()) {
                    return classfileBuffer;
                } else {
                    System.out.println("[SakuraAgent] 拦截外部访问: " + normalizedName);
                    return this.createDecoyClass(normalizedName);
                }
            } else {
                return classfileBuffer;
            }
        }
    }

    private boolean isInternalAccess() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        for(StackTraceElement element : stack) {
            String caller = element.getClassName();

            for(String whitelistPackage : WHITELIST_PACKAGES) {
                if (caller.startsWith(whitelistPackage)) {
                    return true;
                }
            }
        }

        return false;
    }

    private byte[] createDecoyClass(String className) {
        ClassWriter cw = new ClassWriter(2);
        String internalName = className.replace('.', '/');
        cw.visit(61, 1, internalName, (String)null, "java/lang/Object", (String[])null);
        MethodVisitor mv = cw.visitMethod(1, "<init>", "()V", (String)null, (String[])null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, "java/lang/Object", "<init>", "()V", false);
        mv.visitTypeInsn(187, "java/lang/SecurityException");
        mv.visitInsn(89);
        mv.visitLdcInsn("[SakuraAgent] Unauthorized access to protected class: " + className);
        mv.visitMethodInsn(183, "java/lang/SecurityException", "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(191);
        mv.visitMaxs(3, 1);
        mv.visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }
}

