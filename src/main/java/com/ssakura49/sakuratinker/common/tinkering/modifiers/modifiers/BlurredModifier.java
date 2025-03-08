package com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiers;

import com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiermodule;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class BlurredModifier extends modifiermodule {
    private static final int COOLDOWN = 600;
    public static MobEffect getUnconsciousEffect() {
        ResourceLocation effectId = new ResourceLocation("youkaishomecoming", "unconscious");
        return ForgeRegistries.MOB_EFFECTS.getValue(effectId);
    }
    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level level, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!isSelected || !(holder instanceof Player player)) {
            return;
        }
        MobEffect unconscious = getUnconsciousEffect();
        if (unconscious == null) {
            return;
        }
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return;
        }
        if (player.hasEffect(unconscious)) {
            player.removeEffect(unconscious);
        }
        player.addEffect(new MobEffectInstance(unconscious, 200, 0, false, false));
        player.getCooldowns().addCooldown(stack.getItem(), COOLDOWN);
    }

}
