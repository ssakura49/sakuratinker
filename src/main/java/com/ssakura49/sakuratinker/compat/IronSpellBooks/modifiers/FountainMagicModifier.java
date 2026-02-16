package com.ssakura49.sakuratinker.compat.IronSpellBooks.modifiers;

import com.ssakura49.sakuratinker.generic.BaseModifier;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.UUID;
import java.util.function.BiConsumer;

public class FountainMagicModifier extends BaseModifier {

    private static final UUID MAX_MANA_UUID = UUID.fromString("16fd3709-40ba-4456-8da6-9f1eedd48af4");
    private static final UUID MANA_REGEN_UUID = UUID.fromString("e9d33d91-03d0-42b6-9d3c-37edb07095a1");

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        int level = modifier.getLevel();
        double addMana = 800 * level;
        double regenMultiplier = 0.1 * level;
        consumer.accept(AttributeRegistry.MAX_MANA.get(), new AttributeModifier(MAX_MANA_UUID, "Fountain Max Mana", addMana, AttributeModifier.Operation.ADDITION));
        consumer.accept(AttributeRegistry.MANA_REGEN.get(), new AttributeModifier(MANA_REGEN_UUID, "Fountain Mana Regen", regenMultiplier, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }
}
