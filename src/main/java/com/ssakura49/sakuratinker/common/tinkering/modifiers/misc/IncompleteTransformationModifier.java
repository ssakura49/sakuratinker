package com.ssakura49.sakuratinker.common.tinkering.modifiers.misc;

import com.ssakura49.sakuratinker.generic.BaseModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import static slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule.OVERSLIME_STAT;

public class IncompleteTransformationModifier extends BaseModifier {
    @Override
    public void modifierAddToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        OVERSLIME_STAT.add(builder, 1000 * modifier.getLevel());
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!world.isClientSide && world.getGameTime() % 100 == 0) {
            int current = OverslimeModule.INSTANCE.getAmount(tool);
            int max = OverslimeModule.getCapacity(tool);
            if (current < max) {
                int recovery = (int)(0.1f * modifier.getLevel() * max);
                int toAdd = Math.min(recovery, max - current);
                if (toAdd > 0) {
                    OverslimeModule.INSTANCE.addAmount(tool, toAdd);
                }
            }
        }
    }
}
