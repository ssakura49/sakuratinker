package com.ssakura49.sakuratinker.utils.entity;

import com.ssakura49.sakuratinker.SakuraTinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class EntityUtil {
    public EntityUtil() {
    }

    /**
     * 计算一条射线(start -> end)与所有符合条件实体的碰撞结果。
     * 返回按照距离升序排序的所有实体碰撞信息列表。
     */
    public static List<EntityHitResult> getEntityHitResults(Level level, Entity shooter, Vec3 start, Vec3 end, Predicate<Entity> filter, double expand) {
        Vec3 direction = end.subtract(start);
        double length = direction.length();
        if (length == 0) return List.of();
        Vec3 dirNormalized = direction.normalize();
        AABB rayAABB = new AABB(start, end).inflate(expand);
        List<Entity> entities = level.getEntities(shooter, rayAABB, filter);
        List<EntityHitResult> hitResults = new ArrayList<>();
        for (Entity entity : entities) {
            AABB entityBox = entity.getBoundingBox().inflate(0.3);
            Optional<Vec3> interceptOpt = entityBox.clip(start, end);
            if (interceptOpt.isPresent()) {
                Vec3 hitVec = interceptOpt.get();
                double dist = start.distanceToSqr(hitVec);
                double projection = hitVec.subtract(start).dot(dirNormalized);
                if (projection >= -0.001 && projection <= length + 0.001) {
                    EntityHitResult entityHitResult = new EntityHitResult(entity, hitVec);
                    hitResults.add(entityHitResult);
                }
            }
        }
        hitResults.sort(Comparator.comparingDouble(hit -> start.distanceToSqr(hit.getLocation())));
        return hitResults;
    }

    public static List<LivingEntity> getMonsters(LivingEntity center, int range) {
        return center.level().getEntitiesOfClass(LivingEntity.class, center.getBoundingBox().inflate((double)range), (entity) -> entity instanceof Monster);
    }

    public static boolean isHostile(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;
        return living.getType().getCategory() == MobCategory.MONSTER;
    }

    public static List<LivingEntity> getExceptForCenterMonsters(LivingEntity center, int range) {
        List<LivingEntity> entities = center.level().getEntitiesOfClass(LivingEntity.class, center.getBoundingBox().inflate((double)range), (entity) -> entity instanceof Monster);
        entities.remove(center);
        return entities;
    }

    @Nullable
    public static LivingEntity getLivingTarget(Entity target){
        LivingEntity livingEntity = null;
        if (target instanceof PartEntity<?> partEntity && partEntity.getParent() instanceof LivingEntity living){
            livingEntity = living;
        } else if (target instanceof LivingEntity living){
            livingEntity = living;
        }
        return livingEntity;
    }

    public static void spawnThunder(BlockPos pos, Level level) {
        LightningBolt lightningbolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(level);
        if (lightningbolt != null) {
            lightningbolt.moveTo(Vec3.atBottomCenterOf(pos));
            level.addFreshEntity(lightningbolt);
        }

    }

    public static void spawnThunder(Entity target) {
        spawnThunder(target.getOnPos(), target.level());
    }

    public static boolean isEnderEntity(LivingEntity entity) {
        return entity instanceof EnderDragon || entity instanceof Endermite || entity instanceof Shulker || entity instanceof EnderMan;
    }

    public static boolean isInTheSun(LivingEntity entity) {
        return entity.level().canSeeSky(entity.getOnPos()) && entity.level().isDay();
    }

    public static DamageSource getMobOrPlayerSource(LivingEntity entity) {
        if (entity instanceof Player player) {
            return entity.damageSources().playerAttack(player);
        } else {
            return entity.damageSources().mobAttack(entity);
        }
    }

    public static void setFullAttackCooldown(Player player) {
        player.resetAttackStrengthTicker();
    }

    public static LocalPlayer getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static boolean isEmptyInHand(LivingEntity entity) {
        return entity.getMainHandItem().isEmpty() && entity.getOffhandItem().isEmpty();
    }

    public static void modifyArrowSpeed(AbstractArrow arrow, double value) {
        arrow.setDeltaMovement(arrow.getDeltaMovement().scale(value));
    }

    public static boolean isCanDeath(ToolStack tool, LivingDamageEvent event) {
        return tool != null && !event.isCanceled();
    }

    public static boolean isFullChance(Player player) {
        return player.getAttackStrengthScale(0.5F) > 0.9F;
    }

    public static int getEffectLevel(LivingEntity entity, MobEffect effect) {
        MobEffectInstance instance = entity.getEffect(effect);
        return instance == null ? -1 : instance.getAmplifier();
    }

    public static float getMissHp(LivingEntity entity) {
        return entity.getMaxHealth() - entity.getHealth();
    }

    public static float getPerMissHp(LivingEntity entity) {
        return 1.0F - entity.getHealth() / entity.getMaxHealth();
    }

    public static void addEffect(LivingEntity entity, MobEffect effect, int time, int level) {
        entity.addEffect(new MobEffectInstance(effect, time, level));
    }

    public static void addEffect(LivingEntity entity, MobEffect effect, int time) {
        addEffect(entity, effect, time, 0);
    }

    public static int getHarmfulEffectCount(LivingEntity entity) {
        return (int)entity.getActiveEffects().stream().filter((e) -> e.getEffect().getCategory() == MobEffectCategory.HARMFUL).count();
    }

    public static int getBeneficialEffectCount(LivingEntity entity) {
        return (int)entity.getActiveEffects().stream().filter((e) -> e.getEffect().getCategory() == MobEffectCategory.BENEFICIAL).count();
    }

    public static boolean isJumping() {
        return Minecraft.getInstance().options.keyJump.isDown();
    }

    public static boolean isLookingBehindTarget(LivingEntity target, Vec3 attackerLocation) {
        if (attackerLocation != null) {
            Vec3 lookingVector = target.getViewVector(1.0F);
            Vec3 attackAngleVector = attackerLocation.subtract(target.position()).normalize();
            attackAngleVector = new Vec3(attackAngleVector.x, (double)0.0F, attackAngleVector.z);
            return attackAngleVector.dot(lookingVector) < (double)-0.5F;
        } else {
            return false;
        }
    }

    public static void spawnItem(Level level, BlockPos pos, ItemStack stack) {
        ItemEntity entity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack, 0, 0.5, 0);
        entity.setPickUpDelay(10);
        level.addFreshEntity(entity);
    }

    public static void die(LivingEntity victim, DamageSource pDamageSource) {
        if (net.minecraftforge.common.ForgeHooks.onLivingDeath(victim, pDamageSource)) return;
        if (!victim.isRemoved() && !victim.dead) {
            Entity entity = pDamageSource.getEntity();
            LivingEntity livingentity = victim.getKillCredit();
            if (victim.deathScore >= 0 && livingentity != null) {
                livingentity.awardKillScore(victim, victim.deathScore, pDamageSource);
            }
            if (victim.isSleeping()) {
                victim.stopSleeping();
            }
            if (!victim.level().isClientSide && victim.hasCustomName()) {
                SakuraTinker.LOGGER.info("Named entity {} died: {}", entity, victim.getCombatTracker().getDeathMessage().getString());
            }
            victim.dead = true;
            victim.getCombatTracker().recheckStatus();
            Level level = victim.level();
            if (level instanceof ServerLevel serverlevel) {
                if (entity == null || entity.killedEntity(serverlevel, victim)) {
                    victim.gameEvent(GameEvent.ENTITY_DIE);
                    victim.dropAllDeathLoot(pDamageSource);
                } else if (livingentity instanceof Player player){
                    EntityHealthDataHelper.reflectionPenetratingDamage(victim, player, victim.getMaxHealth());
                }
                victim.level().broadcastEntityEvent(victim, (byte)3);
            }
            victim.setPose(Pose.DYING);
        }
    }
}
