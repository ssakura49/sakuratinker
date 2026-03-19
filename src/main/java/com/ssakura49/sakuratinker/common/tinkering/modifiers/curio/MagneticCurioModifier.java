package com.ssakura49.sakuratinker.common.tinkering.modifiers.curio;

import com.ssakura49.tinkercuriolib.hook.TCLibHooks;
import com.ssakura49.tinkercuriolib.hook.interation.CurioInventoryTickModifierHook;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.MagneticModifier;
import top.theillusivec4.curios.api.SlotContext;

public class MagneticCurioModifier extends NoLevelsModifier implements CurioInventoryTickModifierHook {
    public MagneticCurioModifier(){
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this, TCLibHooks.CURIO_TICK);
    }
    @Override
    public void onCurioTick(IToolStackView curio, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
        if (entity.tickCount % 20 == 0) {
            MagneticModifier.applyMagnet(entity, entry.getLevel());
        }
    }
}
