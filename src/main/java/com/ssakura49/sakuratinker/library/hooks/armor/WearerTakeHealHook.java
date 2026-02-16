package com.ssakura49.sakuratinker.library.hooks.armor;

import net.minecraftforge.event.entity.living.LivingHealEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface WearerTakeHealHook {
//    WearerTakeHealHook EMPTY = new WearerTakeHealHook() {
//    };

    default void onTakeHeal(IToolStackView armor, ModifierEntry entry, LivingHealEvent event, EquipmentContext context) {
    }

    public static record AllMerger(Collection<WearerTakeHealHook> modules) implements WearerTakeHealHook {
        public AllMerger(Collection<WearerTakeHealHook> modules) {
            this.modules = modules;
        }

        public void onTakeHeal(IToolStackView armor, ModifierEntry entry, LivingHealEvent event, EquipmentContext context) {
            for(WearerTakeHealHook module : this.modules) {
                module.onTakeHeal(armor, entry, event, context);
            }

        }

        public Collection<WearerTakeHealHook> modules() {
            return this.modules;
        }
    }
}
