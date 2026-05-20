package com.ssakura49.sakuratinker.mixin;

import com.ssakura49.sakuratinker.common.capability.entity.PlayerCapability;
import com.ssakura49.sakuratinker.library.tinkering.tools.STToolStats;
import com.ssakura49.sakuratinker.proxy.CommonProxy;
import com.ssakura49.tinkercuriolib.utils.TCToolUtil;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    public PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(at = @At("HEAD"), method = "hurt")
    private void triggerInvulOnHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!this.isInvulnerableTo(source)) {
            Player player = (Player) (Object) this;
            int statValue = TCToolUtil.getStacks(player).stream()
                    .mapToInt(stack -> ToolStack.from(stack).getStats().getInt(STToolStats.INVULNERABLE_TIME))
                    .max().orElse(0);
            if (statValue > 0) {
                CommonProxy.getPlayerCapOptional(player).ifPresent(cap ->
                        cap.setInvulnerableTick(statValue)
                );
            }
        }
    }
    @Inject(at = @At("RETURN"), method = "isInvulnerableTo", cancellable = true)
    private void checkInvulTick(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (!source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            if (this.sakuratinker$getInvulTick() > 0) {
                cir.setReturnValue(true);
            }
        }
    }
    @Inject(at = @At("HEAD"), method = "aiStep")
    private void tickDown(CallbackInfo ci) {
        if (!this.level().isClientSide) {
            int currentTick = this.sakuratinker$getInvulTick();
            if (currentTick > 0) {
                this.sakuratinker$setInvulTick(currentTick - 1);
            }
        }
    }
    @Unique
    private int sakuratinker$getInvulTick() {
        return CommonProxy.getPlayerCapOptional((Player)(Object)this)
                .map(PlayerCapability::getInvulnerableTick)
                .orElse(0);
    }

    @Unique
    private void sakuratinker$setInvulTick(int tick) {
        CommonProxy.getPlayerCapOptional((Player)(Object)this)
                .ifPresent(cap -> cap.setInvulnerableTick(tick));
    }
}
