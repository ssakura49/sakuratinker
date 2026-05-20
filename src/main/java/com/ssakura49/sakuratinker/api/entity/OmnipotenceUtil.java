package com.ssakura49.sakuratinker.api.entity;

import net.minecraft.world.damagesource.DamageSource;

public class OmnipotenceUtil {
    public static boolean isOmnipotenceSource(DamageSource source) {
        return source instanceof IOmnipotenceSource omni && omni.sakuratinker$isOmnipotence();
    }

    public static DamageSource markOmnipotent(DamageSource source) {
        if (source instanceof IOmnipotenceSource omni) {
            omni.sakuratinker$setOmnipotence(true);
        }
        return source;
    }
}
