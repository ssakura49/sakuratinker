package com.ssakura49.sakuratinker.mixin.damagesource;

import com.ssakura49.sakuratinker.api.entity.IOmnipotenceSource;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements IOmnipotenceSource {
    @Unique
    private boolean sakuratinker$isOmnipotence = false;

    @Override
    public void sakuratinker$setOmnipotence(boolean value) {
        this.sakuratinker$isOmnipotence = value;
    }

    @Override
    public boolean sakuratinker$isOmnipotence() {
        return this.sakuratinker$isOmnipotence;
    }
}
