package com.ssakura49.sakuratinker.common.tinkering.modifiers.curio;

import com.ssakura49.tinkercuriolib.hook.TCLibHooks;
import com.ssakura49.tinkercuriolib.hook.behavior.CurioTakeHealModifierHook;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class SteadyCurioModifier extends NoLevelsModifier implements CurioTakeHealModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this, TCLibHooks.CURIO_TAKE_HEAL);
    }

    @Override
    public void onCurioTakeHeal(IToolStackView curio, ModifierEntry entry, LivingHealEvent event, LivingEntity entity) {
        if (entity != null) {
            if (entity instanceof Player player) {
                float bonus = event.getAmount() * 2.0f;
                event.setAmount(bonus);
            }
        }
    }
}
