package com.ssakura49.sakuratinker.compat.IronSpellBooks.modifiers;

import com.ssakura49.sakuratinker.generic.BaseModifier;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.UUID;
import java.util.function.BiConsumer;

public class MagicianModifier extends BaseModifier {
    private static final UUID CAST_TIME_UUID = UUID.fromString("cad2c9a0-98e5-4131-9fd1-c5bed139bf8d");
    private static final UUID COOLDOWN_REDUCTION = UUID.fromString("19bfe93c-9f39-447f-a12e-9b3f810008db");

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        int level = modifier.getLevel();
        double castTimeReduction = 0.1 * level;
        double cooldownReduction = 0.1 * level;
        consumer.accept(AttributeRegistry.CAST_TIME_REDUCTION.get(), new AttributeModifier(CAST_TIME_UUID, "Magician Cast Time Reduction", castTimeReduction, AttributeModifier.Operation.MULTIPLY_TOTAL));
        consumer.accept(AttributeRegistry.COOLDOWN_REDUCTION.get(), new AttributeModifier(COOLDOWN_REDUCTION, "Magician Cooldown Reduction", cooldownReduction, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }
}
