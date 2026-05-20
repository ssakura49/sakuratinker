package com.ssakura49.sakuratinker.agent.HiddenClass;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HiddenClassManager {
    private static final Set<String> PACKAGES_TO_PROTECT = new HashSet();
    private static final Set<String> CLASSES_TO_PROTECT = new HashSet();

    public static boolean shouldProtect(String className) {
        if (className == null) {
            return false;
        } else if (CLASSES_TO_PROTECT.contains(className)) {
            return true;
        } else {
            for(String protectedPackage : PACKAGES_TO_PROTECT) {
                if (className.equals(protectedPackage) || className.startsWith(protectedPackage + ".")) {
                    return true;
                }
            }

            return false;
        }
    }

    public static Set<String> getProtectedPackages() {
        return Collections.unmodifiableSet(PACKAGES_TO_PROTECT);
    }

    public static Set<String> getProtectedClasses() {
        return Collections.unmodifiableSet(CLASSES_TO_PROTECT);
    }

    public static void addProtectedPackage(String packageName) {
        PACKAGES_TO_PROTECT.add(packageName);
    }

    public static void addProtectedClass(String className) {
        CLASSES_TO_PROTECT.add(className);
    }

    static {
        PACKAGES_TO_PROTECT.add("com.ssakura49.sakuratinker");
    }
}