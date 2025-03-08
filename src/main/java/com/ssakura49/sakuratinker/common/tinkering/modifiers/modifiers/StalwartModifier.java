package com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiers;

import com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiermodule;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class StalwartModifier extends modifiermodule {
    @Override
    public boolean isNoLevels() {
        return true;
    }
    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if(context.isCritical() && RANDOM.nextInt(10) == 0) {
            context.getAttacker().addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200));
        }
    }
}
