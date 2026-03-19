package com.ssakura49.sakuratinker.common.tinkering.modifiers.curio;

import com.ssakura49.sakuratinker.library.damagesource.LegacyDamageSource;
import com.ssakura49.sakuratinker.library.events.LivingCalculateAbsEvent;
import com.ssakura49.tinkercuriolib.content.ProjectileImpactContent;
import com.ssakura49.tinkercuriolib.event.LivingDamageCalculationEvent;
import com.ssakura49.tinkercuriolib.hook.TCLibHooks;
import com.ssakura49.tinkercuriolib.hook.combat.CurioDamageCalculateModifierHook;
import com.ssakura49.tinkercuriolib.hook.ranged.CurioProjectileHitModifierHook;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class WorldBottomModifier extends NoLevelsModifier implements CurioDamageCalculateModifierHook, CurioProjectileHitModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this, TCLibHooks.CURIO_CALCULATE_DAMAGE,TCLibHooks.CURIO_PROJECTILE_HIT);
    }

    private static final int BASE_Y_LEVEL = -64;
    private static final int BLOCKS_PER_BONUS = 5;
    private static final float BONUS_PER_LEVEL = 0.01f;

    private float calculateDamageBonus(LivingEntity entity, int level) {
        double yPos = entity.getY();
        if (yPos <= BASE_Y_LEVEL) {
            return 1.0f;
        }
        double blocksAbove = yPos - BASE_Y_LEVEL;
        int bonusTiers = (int)(blocksAbove / BLOCKS_PER_BONUS);
        return 1.0f + (bonusTiers * level * BONUS_PER_LEVEL);
    }

    @Override
    public void onCurioCalculateDamage(IToolStackView curio, ModifierEntry entry, LivingDamageCalculationEvent event, LivingEntity attacker, LivingEntity target) {
        float damageBonus = calculateDamageBonus(attacker, entry.getLevel());
        if (attacker instanceof Player player && target != null) {
            target.hurt(LegacyDamageSource.playerAttack(player), damageBonus);
        }
    }

    @Override
    public void onCurioProjectileHit(IToolStackView curio, ModifierEntry entry, LivingEntity shooter, ProjectileImpactContent content, HitResult hit) {
        float damageBonus = calculateDamageBonus(shooter, entry.getLevel());
        Entity entity = content.getTarget();
        if (entity != null && shooter instanceof Player player) {
            if (entity.isAlive()) {
                entity.hurt(LegacyDamageSource.playerAttack(player), damageBonus);
            }
        }
    }
}
