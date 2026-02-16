package com.ssakura49.sakuratinker.library.hooks.armor;

import com.ssakura49.sakuratinker.library.logic.context.AttackedContent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface WearerDamageTakeHook {
//    WearerDamageTakeHook EMPTY = new WearerDamageTakeHook() {
//    };

    default void onTakeDamagePre(IToolStackView armor, ModifierEntry entry, LivingHurtEvent event, AttackedContent data) {
    }

    default void onTakeDamagePost(IToolStackView armor, ModifierEntry entry, LivingDamageEvent event, AttackedContent data) {
    }

    public static record AllMerger(Collection<WearerDamageTakeHook> modules) implements WearerDamageTakeHook {
        public AllMerger(Collection<WearerDamageTakeHook> modules) {
            this.modules = modules;
        }

        public void onTakeDamagePre(IToolStackView armor, ModifierEntry entry, LivingHurtEvent event, AttackedContent data) {
            for(WearerDamageTakeHook module : this.modules) {
                module.onTakeDamagePre(armor, entry, event, data);
            }

        }

        public void onTakeDamagePost(IToolStackView armor, ModifierEntry entry, LivingDamageEvent event, AttackedContent data) {
            for(WearerDamageTakeHook module : this.modules) {
                module.onTakeDamagePost(armor, entry, event, data);
            }

        }

        public Collection<WearerDamageTakeHook> modules() {
            return this.modules;
        }
    }
}
