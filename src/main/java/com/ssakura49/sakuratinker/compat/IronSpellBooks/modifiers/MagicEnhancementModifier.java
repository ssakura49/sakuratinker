package com.ssakura49.sakuratinker.compat.IronSpellBooks.modifiers;

import com.ssakura49.sakuratinker.compat.IronSpellBooks.ISSHooks;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.context.SpellAttackContext;
import com.ssakura49.sakuratinker.compat.IronSpellBooks.hook.SpellDamageModifierHook;
import com.ssakura49.sakuratinker.generic.BaseModifier;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class MagicEnhancementModifier extends BaseModifier implements  SpellDamageModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this,ISSHooks.SPELL_DAMAGE);
    }


    @Override
    public float getSpellDamage(IToolStackView tool, ModifierEntry modifier, SpellAttackContext context, float baseDamage, float damage) {
        LivingEntity attacker = context.getAttacker();
        LivingEntity target = context.getLivingTarget();
        if (target == null || target.level().isClientSide() || !(attacker instanceof ServerPlayer serverPlayer)) {
            return damage;
        }
        MagicData magicData = MagicData.getPlayerMagicData(serverPlayer);
        float serverMana = magicData.getMana();
        float manaLimit = modifier.getLevel() * 20f;
        float manaToUse = Math.min(serverMana, manaLimit);
        if (manaToUse < 10f) {
            return damage;
        }
        int boostCycles = (int) Math.floor(manaToUse / 10f);

        if (boostCycles > 0) {
            float consumedMana = boostCycles * 10f;
            float boostPercent = boostCycles * 0.10f;
            int newMana = (int) (serverMana - consumedMana);
            magicData.setMana(newMana);
            PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(magicData));
            damage += (baseDamage * boostPercent);
        }

        return damage;
    }
}
