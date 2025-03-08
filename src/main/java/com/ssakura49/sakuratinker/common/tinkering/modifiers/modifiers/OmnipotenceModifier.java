package com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiers;

import com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiermodule;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class OmnipotenceModifier extends modifiermodule {
    @Override
    public boolean isNoLevels() {
        return true;
    }

    public OmnipotenceModifier() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        LivingEntity target = event.getEntity();
        if (source.getEntity() instanceof Player player) {
            ItemStack item = player.getMainHandItem();
            if (this.getLevel(item) > 0) {
                float halfHealth = target.getMaxHealth() * 0.5f;
                if (target.getHealth() <= halfHealth) {
                    target.setHealth(0);
                    target.die(source);
                } else {
                    target.setHealth(halfHealth);
                }
                event.setCanceled(true);
            }
        }
    }
    private int getLevel(ItemStack stack) {
        if (stack.getItem() instanceof IModifiable) {
            IToolStackView tool = ToolStack.from(stack);
            return tool.getModifierLevel(this);
        }
        return 0;
    }
}

