package com.ssakura49.sakuratinker.compat.Goety.modifiers;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.ssakura49.sakuratinker.generic.BaseModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public class SoulErosionModifier extends BaseModifier {

    @Override
    public void onProjectileHitTarget(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry entry, Projectile projectile, AbstractArrow arrow, EntityHitResult hit, LivingEntity attacker, LivingEntity target) {
        int level = entry.getLevel();
        if (target != null && level > 0) {
            target.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), 100 * level, level - 1));
        }
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damageDealt) {
        LivingEntity attacker = context.getAttacker();
        LivingEntity target = context.getLivingTarget();
        int level = entry.getLevel();
        if (target != null && level > 0) {
            target.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), 100 * level, level - 1));
        }
    }
}
