package com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiers.armor;

import com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiermodule;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.UUID;

public class MagicianModifier extends modifiermodule {
    private static final UUID MAX_MANA_UUID = UUID.fromString("16fd3709-40ba-4456-8da6-9f1eedd48af4");
    private static final UUID MANA_REGEN_UUID = UUID.fromString("e9d33d91-03d0-42b6-9d3c-37edb07095a1");
    private static final UUID CAST_TIME_UUID = UUID.fromString("cad2c9a0-98e5-4131-9fd1-c5bed139bf8d");

    @Override
    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        Player player = context.getEntity() instanceof Player ? (Player) context.getEntity() : null;
        if (player != null) {
            int level = modifier.getLevel();
            double multiplierMana = 0.8 * level;
            double multiplierRegen = 0.2 * level;
            double multiplierCastTimeReduction = 0.1 * level;
            addAttributeModifier(player, AttributeRegistry.MAX_MANA.get(), MAX_MANA_UUID, "ISS Max Mana", multiplierMana, AttributeModifier.Operation.MULTIPLY_TOTAL);
            addAttributeModifier(player, AttributeRegistry.MANA_REGEN.get(), MANA_REGEN_UUID, "ISS Mana Regen", multiplierRegen, AttributeModifier.Operation.MULTIPLY_TOTAL);
            addAttributeModifier(player, AttributeRegistry.CAST_TIME_REDUCTION.get(), CAST_TIME_UUID, "ISS Cast Time Reduction", multiplierCastTimeReduction, AttributeModifier.Operation.MULTIPLY_TOTAL);
        }
    }

    @Override
    public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        Player player = context.getEntity() instanceof Player ? (Player) context.getEntity() : null;
        if (player != null) {
            removeAttributeModifier(player, AttributeRegistry.MAX_MANA.get(), MAX_MANA_UUID);
            removeAttributeModifier(player, AttributeRegistry.MANA_REGEN.get(), MANA_REGEN_UUID);
            removeAttributeModifier(player, AttributeRegistry.CAST_TIME_REDUCTION.get(), CAST_TIME_UUID);
        }
    }

    private void addAttributeModifier(Player player, Attribute attribute, UUID uuid, String name, double value, AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null && !instance.hasModifier(new AttributeModifier(uuid, name, value, operation))) {
            instance.addTransientModifier(new AttributeModifier(uuid, name, value, operation));
        }
    }
    private void removeAttributeModifier(Player player, Attribute attribute, UUID uuid) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(uuid);
        }
    }
}
