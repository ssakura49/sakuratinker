package com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiers;

import com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiermodule;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class SuperHeatModifier extends modifiermodule {
    @Override
    public boolean isNoLevels() {
        return true;
    }
    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        LivingEntity target = context.getLivingTarget();
        if (target != null && target.isOnFire()) {
            return damage + (damage * 0.25F);
        }
        return damage;
    }
}
