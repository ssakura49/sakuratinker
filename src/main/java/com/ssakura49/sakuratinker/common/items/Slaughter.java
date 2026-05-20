package com.ssakura49.sakuratinker.common.items;

import com.ssakura49.sakuratinker.agent.HealthMethod.AgentHealthMethodHelper;
import com.ssakura49.sakuratinker.utils.helper.HealthModify;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

public class Slaughter extends SwordItem {
    public Slaughter(Properties pProperties) {
        super(Tiers.NETHERITE, 100, 1.5f, pProperties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity.removalReason == null && !(entity instanceof LightningBolt)) {
            if (!(entity instanceof Player)) {
                //entity.hurt(player.damageSources().playerAttack(player), (float)stack.getMaxDamage());
                //HealthModify.entityDataModify(entity, 0.0F);
                //HealthModify.tagModify(entity, 0.0F);
                //HealthModify.attributeModifier(entity, 0.0F);
                AgentHealthMethodHelper.forceSetHealth(entity, 0, 0);
            }

            return false;
        } else {
            return false;
        }
    }
}
