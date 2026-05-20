package com.ssakura49.sakuratinker.compat.IronSpellBooks;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.context.SpellAttackContext;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.hook.SpellDamageModifierHook;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.hook.SpellHitModifierHook;
import com.ssakura49.sakuratinker.generic.BaseModifier;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class UmbralISSModifier extends BaseModifier implements SpellHitModifierHook, SpellDamageModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this,ISSHooks.SPELL_HIT,ISSHooks.SPELL_DAMAGE);
    }

    @Override
    public boolean isNoLevels() {
        return true;
    }

    @Override
    public void afterSpellHit(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        if (target == null || target.level().isClientSide()) return;
        if (context.getSchoolType() == SchoolRegistry.ELDRITCH.get()) {
            if (target instanceof net.minecraft.world.entity.player.Player) {
                target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 10 * 20, 0));
            }
        }
    }

    @Override
    public float getSpellDamage(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float baseDamage, float damage) {
        LivingEntity target = context.getLivingTarget();
        if (target != null && context.getSchoolType() == SchoolRegistry.BLOOD.get()) {
            if (!(target instanceof net.minecraft.world.entity.player.Player)) {
                damage += baseDamage * 0.20f;
            }
        }
        return damage;
    }
}
