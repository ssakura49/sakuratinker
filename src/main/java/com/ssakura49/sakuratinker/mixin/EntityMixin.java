package com.ssakura49.sakuratinker.mixin;

import com.ssakura49.sakuratinker.api.entity.OmnipotenceUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class,priority = 0)
public abstract class EntityMixin {
    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void sakuratinker$onIsInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (OmnipotenceUtil.isOmnipotenceSource(source)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void sakuratinker$onEntityHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof PartEntity<?> part) {
            if (OmnipotenceUtil.isOmnipotenceSource(source)) {
                part.getParent().hurt(source, amount);
                cir.setReturnValue(true);
            }
        }
    }
}
