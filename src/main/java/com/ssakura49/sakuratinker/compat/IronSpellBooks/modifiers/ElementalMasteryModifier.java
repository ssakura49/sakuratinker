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

public class ElementalMasteryModifier extends BaseModifier {

    private static final UUID FIRE_SPELL_POWER = UUID.fromString("2fc05d10-9abf-4076-9143-043362c62351");
    private static final UUID ICE_SPELL_POWER = UUID.fromString("d09d3411-1b5e-4880-836e-a55f2a988f95");
    private static final UUID LIGHTNING_SPELL_POWER = UUID.fromString("cbca1965-6b50-474a-a888-8a671add1371");
    private static final UUID HOLY_SPELL_POWER = UUID.fromString("5cc332ef-9c10-466a-ad6c-ffc9c9507187");
    private static final UUID NATURE_SPELL_POWER = UUID.fromString("d8ebc2ff-1d85-478f-bf71-f73cdd2d3900");

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        int level = modifier.getLevel();
        double value = 0.1 * level;

        consumer.accept(AttributeRegistry.FIRE_SPELL_POWER.get(),
                new AttributeModifier(FIRE_SPELL_POWER, "Elemental Fire Power", value, AttributeModifier.Operation.MULTIPLY_TOTAL));
        consumer.accept(AttributeRegistry.ICE_SPELL_POWER.get(),
                new AttributeModifier(ICE_SPELL_POWER, "Elemental Ice Power", value, AttributeModifier.Operation.MULTIPLY_TOTAL));
        consumer.accept(AttributeRegistry.LIGHTNING_SPELL_POWER.get(),
                new AttributeModifier(LIGHTNING_SPELL_POWER, "Elemental Lightning Power", value, AttributeModifier.Operation.MULTIPLY_TOTAL));
        consumer.accept(AttributeRegistry.HOLY_SPELL_POWER.get(),
                new AttributeModifier(HOLY_SPELL_POWER, "Elemental Holy Power", value, AttributeModifier.Operation.MULTIPLY_TOTAL));
        consumer.accept(AttributeRegistry.NATURE_SPELL_POWER.get(),
                new AttributeModifier(NATURE_SPELL_POWER, "Elemental Nature Power", value, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }
}
