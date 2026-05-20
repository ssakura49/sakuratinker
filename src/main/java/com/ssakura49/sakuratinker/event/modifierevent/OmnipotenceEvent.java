package com.ssakura49.sakuratinker.event.modifierevent;

import com.ssakura49.sakuratinker.STConfig;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.library.damagesource.LegacyDamageSource;
import com.ssakura49.sakuratinker.register.STModifiers;
import com.ssakura49.sakuratinker.utils.helper.DropLootHelper;
import com.ssakura49.sakuratinker.utils.tinker.ToolUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = SakuraTinker.MODID)
public class OmnipotenceEvent {
    public static Supplier<Boolean> infinity = STConfig.Common.INFINITY_DAMAGE;
    private static final ThreadLocal<Boolean> IS_PROCESSING = ThreadLocal.withInitial(() -> false);
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onOmnipotenceLivingDamage(AttackEntityEvent event) {
        if (IS_PROCESSING.get()) return;
        Entity rawTarget = event.getTarget();
        Player player = event.getEntity();
        if (!(rawTarget instanceof LivingEntity target) || player.level().isClientSide) return;
        ToolStack tool = getOmnipotenceTool(player);
        if (tool != null) {
            event.setCanceled(true);
            try {
                IS_PROCESSING.set(true);
                LegacyDamageSource omniSource = LegacyDamageSource.playerAttack(player)
                        .setBypassArmor()
                        .setBypassInvulnerability()
                        .setBypassShield()
                        .setBypassInvulnerableTime()
                        .setNoImpact();
                float damage = tool.getStats().get(ToolStats.ATTACK_DAMAGE);
                float finalDamage = infinity.get() ? Float.MAX_VALUE : damage;
                hurt(target, omniSource, finalDamage);
            } finally {
                IS_PROCESSING.set(false);
            }
        }

    }

    public static boolean hasOmnipotence(Player player) {
        for (ToolStack tool : ToolUtil.getAllEquippedToolStacks(player)) {
            if (tool.getModifierLevel(STModifiers.Omnipotence.get()) > 0) {
                return true;
            }
        }
        return false;
    }

    private static ToolStack getOmnipotenceTool(Player player) {
        for (ToolStack tool : ToolUtil.getAllEquippedToolStacks(player)) {
            if (tool.getModifierLevel(STModifiers.Omnipotence.get()) > 0) {
                return tool;
            }
        }
        return null;
    }

    public static boolean hurt(LivingEntity victim, DamageSource pSource, float pAmount) {
        if (victim.level().isClientSide || victim.isDeadOrDying()) {
            return false;
        } else {
            if (victim.isSleeping() && !victim.level().isClientSide) {
                victim.stopSleeping();
            }

            boolean flag = false;

            victim.setNoActionTime(0);
            victim.walkAnimation.setSpeed(1.5F);
            victim.lastHurt = pAmount;
            victim.invulnerableTime = 20;
            victim.getCombatTracker().recordDamage(pSource, pAmount);
            victim.setHealth(victim.getHealth() - pAmount);
            victim.gameEvent(GameEvent.ENTITY_DAMAGE);
            victim.hurtDuration = 10;
            victim.hurtTime = victim.hurtDuration;


            Entity entity1 = pSource.getEntity();
            if (entity1 != null) {
                if (entity1 instanceof LivingEntity livingentity1) {
                    if (!pSource.is(DamageTypeTags.NO_ANGER)) {
                        victim.setLastHurtByMob(livingentity1);
                    }
                }

                if (entity1 instanceof Player player1) {
                    victim.lastHurtByPlayerTime = 100;
                    victim.setLastHurtByPlayer(player1);
                } else if (entity1 instanceof net.minecraft.world.entity.TamableAnimal tamableEntity) {
                    if (tamableEntity.isTame()) {
                        victim.lastHurtByPlayerTime = 100;
                        LivingEntity livingentity2 = tamableEntity.getOwner();
                        if (livingentity2 instanceof Player player2) {
                            victim.setLastHurtByPlayer(player2);
                        } else {
                            victim.setLastHurtByPlayer(null);
                        }
                    }
                }
            }

            victim.level().broadcastDamageEvent(victim, pSource);

            if (!pSource.is(DamageTypeTags.NO_IMPACT)) {
                victim.hurtMarked = true;
            }

            if (entity1 != null && !pSource.is(DamageTypeTags.IS_EXPLOSION)) {
                double d0 = entity1.getX() - victim.getX();

                double d1;
                for (d1 = entity1.getZ() - victim.getZ(); d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                    d0 = (Math.random() - Math.random()) * 0.01D;
                }

                victim.knockback(0.4F, d0, d1);
                if (!flag) {
                    victim.indicateDamage(d0, d1);
                }
            }

            if (victim.isDeadOrDying()) {
                die(victim, pSource);
            } else {
                SoundEvent soundevent = SoundEvents.GENERIC_HURT;
                victim.playSound(soundevent, 2F, victim.getVoicePitch());
            }

            boolean flag2 = true;
            victim.lastDamageSource = pSource;
            victim.lastDamageStamp = victim.level().getGameTime();

            if (victim instanceof ServerPlayer) {
                CriteriaTriggers.ENTITY_HURT_PLAYER.trigger((ServerPlayer) victim, pSource, pAmount, pAmount, flag);
            }

            if (entity1 instanceof ServerPlayer) {
                CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer) entity1, victim, pSource, pAmount, pAmount, flag);
            }

            return flag2;
        }
    }

    public static void die(LivingEntity victim, DamageSource pDamageSource) {
        if (!victim.isRemoved() && !victim.dead) {
            Entity entity = pDamageSource.getEntity();
            LivingEntity livingentity = victim.getKillCredit();
            if (victim.deathScore >= 0 && livingentity != null) {
                livingentity.awardKillScore(victim, victim.deathScore, pDamageSource);
            }

            if (victim.isSleeping()) {
                victim.stopSleeping();
            }

//            if (!victim.level().isClientSide && victim.hasCustomName()) {
//                SakuraTinker.LOGGER.info("Named entity {} died: {}", this, victim.getCombatTracker().getDeathMessage().getString());
//            }

            victim.dead = true;
            victim.getCombatTracker().recheckStatus();
            Level level = victim.level();
            if (level instanceof ServerLevel serverlevel) {
                victim.level().broadcastEntityEvent(victim, (byte) 3);
            }

            victim.setPose(Pose.DYING);
            if (entity instanceof Player player) {
                DropLootHelper.dropLoot(victim,player,true);
            }
        }
    }
}
