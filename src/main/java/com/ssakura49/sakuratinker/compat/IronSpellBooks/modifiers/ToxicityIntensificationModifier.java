package com.ssakura49.sakuratinker.compat.IronSpellBooks.modifiers;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSHooks;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.context.SpellAttackContext;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.hook.SpellHitModifierHook;
import com.ssakura49.sakuratinker.generic.BaseModifier;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.stats.ToolType;

public class ToxicityIntensificationModifier extends BaseModifier implements SpellHitModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this,ISSHooks.SPELL_HIT);
    }


    @Override
    public void afterSpellHit(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        if (context.getSchoolType()== SchoolRegistry.ICE.get()&&target!=null) {
            if (target.hasEffect(MobEffects.POISON)) {
                MobEffectInstance currentPoison = target.getEffect(MobEffects.POISON);

                if (currentPoison != null) {
                    int currentDuration = currentPoison.getDuration();
                    int currentAmplifier = currentPoison.getAmplifier();
                    int newAmplifier = Math.min(9, (currentAmplifier + 1) * 2 - 1);
                    int newDuration = currentDuration * 2;
                    MobEffectInstance newPoison = new MobEffectInstance(
                            MobEffects.POISON,
                            currentDuration,
                            newAmplifier,
                            currentPoison.isAmbient(),
                            currentPoison.isVisible(),
                            currentPoison.showIcon()
                    );
                    target.addEffect(newPoison);
                }
            }

        }
    }
}
