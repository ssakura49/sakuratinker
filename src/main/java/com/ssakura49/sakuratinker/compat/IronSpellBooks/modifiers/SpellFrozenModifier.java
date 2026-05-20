package com.ssakura49.sakuratinker.compat.IronSpellBooks.modifiers;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSHooks;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.context.SpellAttackContext;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.hook.SpellHitModifierHook;
import com.ssakura49.sakuratinker.generic.BaseModifier;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class SpellFrozenModifier extends BaseModifier implements SpellHitModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this, ISSHooks.SPELL_HIT);
    }

    @Override
    public void afterSpellHit(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        if (context.getSchoolType()==SchoolRegistry.ICE.get()&&target!=null) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,200,modifier.getLevel(),true,true,true));
        }
    }
}
