package com.ssakura49.sakuratinker.library.hooks.armor;

import com.ssakura49.sakuratinker.library.logic.context.AttackedContent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface WearerDamagePreHook {
    default void onDamagePre(IToolStackView armor, ModifierEntry entry, LivingHurtEvent event, AttackedContent data) {
    }

    public static record AllMerger(Collection<WearerDamagePreHook> modules) implements WearerDamagePreHook {
        public AllMerger(Collection<WearerDamagePreHook> modules) {
            this.modules = modules;
        }

        public void onDamagePre(IToolStackView armor, ModifierEntry entry, LivingHurtEvent event, AttackedContent data) {
            for(WearerDamagePreHook module : this.modules) {
                module.onDamagePre(armor, entry, event, data);
            }

        }

        public Collection<WearerDamagePreHook> modules() {
            return this.modules;
        }
    }
}