package com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiers;

import com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiermodule;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFItems;

public class SynergyModifier extends modifiermodule {
    private static final int MAX_REPAIR_PER_SECOND = 5;
    private static final int TICK_INTERVAL = 100;

    private static final Item STEEL_LEAF = TFItems.STEELEAF_INGOT.get();
    private static final Item STEEL_LEAF_BLOCK = TFBlocks.STEELEAF_BLOCK.get().asItem();

    @Override
    public boolean isNoLevels() {
        return true;
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level level, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack itemStack) {
        if (!level.isClientSide && holder instanceof Player player) {

            if (level.getGameTime() % TICK_INTERVAL == 0 && modifier.getLevel() > 0) {
                int steelLeafCount = countSteelLeafItems(player, STEEL_LEAF);
                int steelLeafBlockCount = countSteelLeafItems(player, STEEL_LEAF_BLOCK);

                float repairAmount = calculateRepairAmount(steelLeafCount, steelLeafBlockCount);

                if (repairAmount > 0) {
                    ItemStack mainHandItem = player.getMainHandItem();
                    if (!mainHandItem.isEmpty() && mainHandItem.isDamageableItem()) {
                        repairItem(mainHandItem, repairAmount);
                    } else {
                        ItemStack offHandItem = player.getOffhandItem();
                        if (!offHandItem.isEmpty() && offHandItem.isDamageableItem()) {
                            repairItem(offHandItem, repairAmount);
                        }
                    }
                }
            }
        }
    }

    private int countSteelLeafItems(Player player, Item targetItem) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == targetItem) {
                count += stack.getCount();
            }
        }
        return count;
    }
    private float calculateRepairAmount(int steelLeafCount, int steelLeafBlockCount) {
        float repairAmount = steelLeafCount * 0.01f + steelLeafBlockCount * 0.09f;
        return Math.min(MAX_REPAIR_PER_SECOND, repairAmount);
    }
    private void repairItem(ItemStack itemStack, float amount) {
        int damage = itemStack.getDamageValue();
        int newDamage = (int) Math.max(0, damage - amount);
        itemStack.setDamageValue(newDamage);
    }
}