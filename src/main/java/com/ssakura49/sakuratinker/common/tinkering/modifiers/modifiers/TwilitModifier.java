package com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiers;

import com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiermodule;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import twilightforest.world.registration.TFGenerationSettings;

public class TwilitModifier extends modifiermodule{
    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        Player player = context.getPlayerAttacker();
        if (player != null && isInTwilightForest(player.level())) {
            return damage + (baseDamage + 2 * modifier.getLevel());
        }
        return damage;
    }

    @Override
    public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, PlayerEvent.BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
        Player player = event.getEntity();
        if (isInTwilightForest(player.level())) {
            float newSpeed = event.getNewSpeed() * (1.0f + (0.03f * modifier.getLevel()));
            event.setNewSpeed(newSpeed);
        }
    }

    private boolean isInTwilightForest(Level level) {
        return level.dimension().equals(TFGenerationSettings.DIMENSION_KEY);
    }
}
