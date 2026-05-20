package com.ssakura49.sakuratinker.compat.Botania.modifier;

import com.google.common.collect.Iterables;
import com.ssakura49.sakuratinker.generic.BaseModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;

public class TerraMagicalModifier extends BaseModifier {
    private static final int MANA_PER_TICK = 2;
    private static final int HEAL_INTERVAL = 40;

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level level, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (level.isClientSide || !(holder instanceof Player player)) return;
        List<ItemStack> items = ManaItemHandler.INSTANCE.getManaItems(player);
        List<ItemStack> acc = ManaItemHandler.INSTANCE.getManaAccesories(player);
        for (ItemStack stackInSlot : Iterables.concat(items, acc)) {
            if (stackInSlot == stack) {
                continue;
            }
            var manaItem = XplatAbstractions.INSTANCE.findManaItem(stackInSlot);
            if (manaItem != null) {
                var requestor = XplatAbstractions.INSTANCE.findManaItem(stack);
                if (requestor != null && !requestor.canReceiveManaFromItem(stackInSlot)) {
                    continue;
                }
                int maxMana = manaItem.getMaxMana();
                int currentMana = manaItem.getMana();
                if (currentMana < maxMana) {
                    manaItem.addMana(modifier.getLevel());
                }
            }
        }
        if (level.getGameTime() % HEAL_INTERVAL == 0 && player.getHealth() < player.getMaxHealth()) {
            player.heal(1.0F * modifier.getLevel());
        }
    }

}
