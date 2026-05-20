package com.ssakura49.sakuratinker.compat.IronSpellBooks.modifiers;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSHooks;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.context.SpellAttackContext;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.hook.SpellDamageModifierHook;
import com.ssakura49.sakuratinker.generic.BaseModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class HolyHitModifier extends BaseModifier implements SpellDamageModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this, ISSHooks.SPELL_DAMAGE);
    }

    @Override
    public float getSpellDamage(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float baseDamage, float damage) {
        if (context.getAttacker().level().random.nextFloat() < 0.15){
            return damage*2;
        }
        return damage;
    }
}
