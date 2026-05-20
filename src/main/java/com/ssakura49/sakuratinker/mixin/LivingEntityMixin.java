package com.ssakura49.sakuratinker.mixin;

import com.ssakura49.sakuratinker.STConfig;
import com.ssakura49.sakuratinker.api.entity.IOmnipotenceEntity;
import com.ssakura49.sakuratinker.api.entity.OmnipotenceUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 0)
public abstract class LivingEntityMixin implements IOmnipotenceEntity {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (OmnipotenceUtil.isOmnipotenceSource(source)) {
            boolean handled = this.sakuratinker$executeOmnipotenceHurt(source, amount);
            if (handled) {
                cir.setReturnValue(true);
            }
        }
    }

    @Override
    public boolean sakuratinker$executeOmnipotenceHurt(DamageSource source, float amount) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide || !self.isAlive()) return false;

        float finalDamage = STConfig.Common.INFINITY_DAMAGE.get() ? Float.MAX_VALUE : amount;
        self.invulnerableTime = 0;
        self.setHealth(Math.max(0, self.getHealth() - finalDamage));
        self.level().broadcastDamageEvent(self, source);
        self.playSound(SoundEvents.GENERIC_HURT, 1.0F, self.getVoicePitch());
        if (self.getHealth() <= 0) {
            self.die(source);
        }
        return true;
    }
}
