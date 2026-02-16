package com.ssakura49.sakuratinker.library.hooks.curio.behavior;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.Collection;
import java.util.UUID;
import java.util.function.BiConsumer;

public interface CurioDropRuleModifierHook {
    default ICurio.DropRule getCurioDropRule(IToolStackView curio, ModifierEntry entry, DamageSource source, int looting, boolean recentlyHit, ItemStack stack) {
        return ICurio.DropRule.DEFAULT;
    }

    public static record AllMerger(Collection<CurioDropRuleModifierHook> modules) implements CurioDropRuleModifierHook {
        public AllMerger(Collection<CurioDropRuleModifierHook> modules) {
            this.modules = modules;
        }

        @Override
        public ICurio.DropRule getCurioDropRule(IToolStackView curio, ModifierEntry entry,DamageSource source, int looting, boolean recent, ItemStack stack) {
            for (CurioDropRuleModifierHook module : modules) {
                return module.getCurioDropRule(curio, entry, source, looting, recent, stack);
            }
            return ICurio.DropRule.DEFAULT;
        }

        public Collection<CurioDropRuleModifierHook> modules() {
            return this.modules;
        }
    }
}
