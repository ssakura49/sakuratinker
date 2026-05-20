package com.ssakura49.sakuratinker.compat.IronSpellBooks.modifiers;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSHooks;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.context.SpellAttackContext;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.hook.SpellDamageModifierHook;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.hook.SpellHitModifierHook;
import com.ssakura49.sakuratinker.generic.BaseModifier;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.stats.ToolType;

public class GreedyBookModifier extends BaseModifier implements SpellHitModifierHook, SpellDamageModifierHook {
    private final ResourceLocation ME = SakuraTinker.getResource("magic_enhancement");

    @Override
    protected void registerHooks(ModuleHookMap.Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this, ISSHooks.SPELL_DAMAGE,ISSHooks.SPELL_HIT);
    }

    @Override
    public float getSpellDamage(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float baseDamage, float damage) {
        LivingEntity attacker = context.getAttacker();
        TinkerEffect effect = TinkerModifiers.insatiableEffect.get(ToolType.ARMOR);
        int level = TinkerEffect.getAmplifier(attacker, effect);
        if (level >= 0) {
            damage += (level + 1) * 2f;
        }

        return damage;
    }

    @Override
    public void afterSpellHit(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float damageDealt) {
        LivingEntity attacker = context.getAttacker();
        if (attacker.level().isClientSide()) return;
        int maxLevel = modifier.getLevel() - 1;
        int duration = 3 * 20;
        applyGreedyEffect(attacker, ToolType.ARMOR, duration, 1, maxLevel);
    }

    /**
     * 辅助方法：安全应用工匠的效果叠加逻辑
     */
    private void applyGreedyEffect(LivingEntity living, ToolType type, int duration, int add, int maxLevel) {
        TinkerEffect effect = (TinkerEffect) TinkerModifiers.insatiableEffect.get(type);
        int currentLevel = TinkerEffect.getAmplifier(living, effect);
        int nextLevel = currentLevel < 0 ? 0 : Math.min(maxLevel, currentLevel + add);
        effect.apply(living, duration, nextLevel, true);
    }
}
