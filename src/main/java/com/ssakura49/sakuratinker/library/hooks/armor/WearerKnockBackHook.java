package com.ssakura49.sakuratinker.library.hooks.armor;

import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface WearerKnockBackHook {
//    WearerKnockBackHook EMPTY = new WearerKnockBackHook() {
//    };

    default void onKnockBack(IToolStackView armor, ModifierEntry entry, LivingKnockBackEvent event, EquipmentContext context) {
    }

    public static record AllMerger(Collection<WearerKnockBackHook> modules) implements WearerKnockBackHook {
        public AllMerger(Collection<WearerKnockBackHook> modules) {
            this.modules = modules;
        }

        public void onKnockBack(IToolStackView armor, ModifierEntry entry, LivingKnockBackEvent event, EquipmentContext context) {
            for(WearerKnockBackHook module : this.modules) {
                module.onKnockBack(armor, entry, event, context);
            }

        }

        public Collection<WearerKnockBackHook> modules() {
            return this.modules;
        }
    }
}
