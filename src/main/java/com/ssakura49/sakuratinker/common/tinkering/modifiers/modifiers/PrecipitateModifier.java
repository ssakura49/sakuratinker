package com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiers;

import com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiermodule;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class PrecipitateModifier extends modifiermodule {
    @Override
    public boolean isNoLevels() {
        return true;
    }

    @Override
    public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, PlayerEvent.BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
        Player player = event.getEntity();
            float bonusPercentage = getBonusPercentage(player);
            event.setNewSpeed(event.getNewSpeed() + (bonusPercentage * event.getOriginalSpeed()));
    }

    private float getBonusPercentage(LivingEntity entity) {
        float maxHealth = entity.getMaxHealth();
        return (maxHealth - entity.getHealth()) / maxHealth;
    }
}
