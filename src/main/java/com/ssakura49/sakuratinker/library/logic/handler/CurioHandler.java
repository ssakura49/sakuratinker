package com.ssakura49.sakuratinker.library.logic.handler;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.library.events.ItemStackDamageEvent;
import com.ssakura49.sakuratinker.library.events.LivingCalculateAbsEvent;
import com.ssakura49.sakuratinker.library.hooks.curio.armor.CurioTakeDamagePreModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.combat.CurioDamageTargetPostModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.combat.CurioKillTargetModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.mining.CurioBreakSpeedModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.behavior.CurioTakeHealModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.combat.CurioCalculateDamageModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.combat.CurioDamageTargetPreModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.ranged.CurioArrowHitHook;
import com.ssakura49.sakuratinker.library.hooks.curio.ranged.CurioArrowShootHook;
import com.ssakura49.sakuratinker.library.hooks.curio.behavior.CurioGetToolDamageModifierHook;
import com.ssakura49.sakuratinker.library.hooks.curio.armor.CurioTakeDamagePostModifierHook;
import com.ssakura49.sakuratinker.library.logic.context.ProjectileImpactContent;
import com.ssakura49.sakuratinker.library.tinkering.tools.STHooks;
import com.ssakura49.sakuratinker.library.tinkering.tools.STToolStats;
import com.ssakura49.sakuratinker.utils.tinker.ToolUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mod.EventBusSubscriber(modid = SakuraTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CurioHandler {
    public CurioHandler (){}

    @SubscribeEvent
    public static void onDamageStack(ItemStackDamageEvent event) {
        LivingEntity entity = event.getEntity();
        ToolUtil.Curios.getStacks(entity).forEach((stack) -> {
            ToolStack curio = ToolStack.from(stack);
            curio.getModifierList().forEach((entry) -> {
                CurioGetToolDamageModifierHook hook = (CurioGetToolDamageModifierHook)entry.getHook(STHooks.CURIO_TOOL_DAMAGE);
                hook.onCurioGetToolDamage(curio,entry, entity, event);
            });
        });
    }

    private static void onHurtEntity(LivingHurtEvent event) {
        Entity var2 = event.getSource().getEntity();
        if (var2 instanceof LivingEntity attacker) {
            ToolUtil.Curios.getStacks(attacker).forEach((stack) -> {
                ToolStack curio = ToolStack.from(stack);
                curio.getModifierList().forEach((e) -> {
                    CurioDamageTargetPreModifierHook hook = (CurioDamageTargetPreModifierHook) e.getHook(STHooks.CURIO_DAMAGE_TARGET_PRE);
                    hook.onDamageTargetPre(curio,e, event, attacker, event.getEntity());
                });
            });
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        onHurtEntity(event);
        ToolUtil.Curios.getStacks(entity).forEach((stack) -> {
            ToolStack curio = ToolStack.from(stack);
            curio.getModifierList().forEach((e) -> {
                CurioTakeDamagePreModifierHook hook = (CurioTakeDamagePreModifierHook)e.getHook(STHooks.CURIO_TAKE_DAMAGE_PRE);
                hook.onCurioTakeDamagePre(curio,e, event, entity, event.getSource());
            });
        });
    }

    private static void onDamageEntity(LivingDamageEvent event) {
        Entity var2 = event.getSource().getEntity();
        if (var2 instanceof LivingEntity attacker) {
            ToolUtil.Curios.getStacks(attacker).forEach((stack) -> {
                ToolStack curio = ToolStack.from(stack);
                curio.getModifierList().forEach((e) -> {
                    CurioDamageTargetPostModifierHook hook = (CurioDamageTargetPostModifierHook)e.getHook(STHooks.CURIO_DAMAGE_TARGET_POST);
                    hook.onCurioDamageTargetPost(curio,e, event, attacker, event.getEntity());
                });
            });
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        onDamageEntity(event);
        ToolUtil.Curios.getStacks(entity).forEach((stack) -> {
            ToolStack curio = ToolStack.from(stack);
            curio.getModifierList().forEach((e) -> {
                CurioTakeDamagePostModifierHook hook = (CurioTakeDamagePostModifierHook)e.getHook(STHooks.CURIO_TAKE_DAMAGE_POST);
                hook.onCurioTakeDamagePost(curio,e, event, entity, event.getSource());
            });
        });
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity var2 = event.getSource().getEntity();
        if (var2 instanceof LivingEntity attacker) {
            ToolUtil.Curios.getStacks(attacker).forEach((stack) -> {
                ToolStack curio = ToolStack.from(stack);
                curio.getModifierList().forEach((e) -> {
                    CurioKillTargetModifierHook hook = (CurioKillTargetModifierHook)e.getHook(STHooks.CURIO_KILL_TARGET);
                    hook.onCurioToKillTarget(curio,e, event, attacker, event.getEntity());
                });
            });
        }
    }

    @SubscribeEvent
    public static void onLivingCalculate(LivingCalculateAbsEvent event) {
        LivingEntity attacker = event.getLivingAttacker();
        if (attacker != null) {
            ToolUtil.Curios.getStacks(attacker).forEach((stack) -> {
                ToolStack curio = ToolStack.from(stack);
                curio.getModifierList().forEach((e) -> {
                    CurioCalculateDamageModifierHook hook = (CurioCalculateDamageModifierHook)e.getHook(STHooks.CURIO_CALCULATE_DAMAGE);
                    hook.onCurioCalculateDamage(curio,e, event, attacker, event.getEntity());
                });
            });
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        ToolUtil.Curios.getStacks(entity).forEach((stack) -> {
            ToolStack curio = ToolStack.from(stack);
            curio.getModifierList().forEach((e) -> {
                CurioTakeHealModifierHook hook = (CurioTakeHealModifierHook)e.getHook(STHooks.CURIO_TAKE_HEAL);
                hook.onCurioTakeHeal(curio,e, event, entity);
            });
        });
    }

    public static void arrowStatAdd(ToolStack curio, LivingEntity entity, AbstractArrow arrow) {
        float arrow_damage = ConditionalStatModifierHook.getModifiedStat(curio, entity, STToolStats.ARROW_DAMAGE);
        arrow.setBaseDamage(arrow.getBaseDamage() * (double)(1.0F + arrow_damage));
    }

    @SubscribeEvent
    public static void onShootArrow(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof AbstractArrow arrow) {
            Entity var3 = arrow.getOwner();
            if (var3 instanceof LivingEntity livingEntity) {
                ToolUtil.Curios.getStacks(livingEntity).forEach((stack) -> {
                    ToolStack curio = ToolStack.from(stack);
                    curio.getModifierList().forEach((e) -> {
                        arrowStatAdd(curio, livingEntity, arrow);
                        CurioArrowShootHook hook = (CurioArrowShootHook)e.getHook(STHooks.CURIO_ARROW_SHOOT);
                        hook.onCurioShootArrow(curio,e, livingEntity, arrow, PersistentDataCapability.getOrWarn(arrow));
                    });
                });
            }
        }
    }

    @SubscribeEvent
    public static void onArrowHit(ProjectileImpactEvent event) {
        Projectile shooter = event.getProjectile();
        if (shooter instanceof AbstractArrow arrow) {
            Entity var3 = arrow.getOwner();
            if (var3 instanceof LivingEntity shooter1) {
                ToolUtil.Curios.getStacks(shooter1).forEach((stack) -> {
                    ToolStack curio = ToolStack.from(stack);
                    curio.getModifierList().forEach((entry) -> {
                        CurioArrowHitHook hook = entry.getHook(STHooks.CURIO_ARROW_HIT);
                        hook.onCurioArrowHit(curio,entry, shooter1, new ProjectileImpactContent(event, arrow));
                    });
                });
            }
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        ToolUtil.Curios.getStacks(event.getEntity()).forEach((stack) -> {
            ToolStack curio = ToolStack.from(stack);
            curio.getModifierList().forEach((e) -> {
                CurioBreakSpeedModifierHook hook = (CurioBreakSpeedModifierHook)e.getHook(STHooks.CURIO_BREAK_SPEED);
                hook.onCurioBreakSpeed(curio,e, event, event.getEntity());
            });
        });
    }
}
