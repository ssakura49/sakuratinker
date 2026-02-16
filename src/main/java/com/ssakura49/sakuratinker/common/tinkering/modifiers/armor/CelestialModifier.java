package com.ssakura49.sakuratinker.common.tinkering.modifiers.armor;

import com.ssakura49.sakuratinker.generic.BaseModifier;
import com.ssakura49.sakuratinker.library.logic.helper.FlyingHelper;
import com.ssakura49.sakuratinker.register.STEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class CelestialModifier extends BaseModifier {
    @Override
    public boolean isNoLevels() {
        return true;
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level level, LivingEntity entity, int slot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!level.isClientSide && entity instanceof Player player) {
            if (isCorrectSlot && tool.getModifierLevel(this)>0) {
                FlyingHelper.tickFlying(player);
            }

        }
    }

//    @Override
//    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
//        if (isArmorSlot(context.getChangedSlot()) && context.getEntity() instanceof Player player) {
//            player.getAbilities().mayfly = true;
//            player.getAbilities().flying = true;
//            player.onUpdateAbilities();
//        }
//    }
//    @Override
//    public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
//        if (isArmorSlot(context.getChangedSlot())) {
//                if (context.getEntity() instanceof Player player) {
//                    if (!hasCelestialArmor(player))  {
//                        if (!player.isCreative() && !player.isSpectator()) {
//                            player.getAbilities().mayfly = false;
//                            player.getAbilities().flying = false;
//                            player.onUpdateAbilities();
//                        }
//                    }
//            }
//        }
//    }
//    private boolean isArmorSlot(EquipmentSlot slot) {
//        return slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
//    }
//    private boolean hasCelestialArmor(Player player) {
//        for (ItemStack stack : player.getInventory().armor) {
//            if (!stack.isEmpty()) {
//                IToolStackView armorTool = ToolStack.from(stack);
//                for (ModifierEntry entry : armorTool.getModifiers().getModifiers()) {
//                    if (entry.getModifier() == this) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
}
