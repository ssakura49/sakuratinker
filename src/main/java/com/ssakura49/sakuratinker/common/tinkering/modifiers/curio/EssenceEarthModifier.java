package com.ssakura49.sakuratinker.common.tinkering.modifiers.curio;

import com.ssakura49.sakuratinker.STConfig;
import com.ssakura49.sakuratinker.generic.CurioModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.function.Supplier;

public class EssenceEarthModifier extends CurioModifier {
    private static final Supplier<Double> bonus = STConfig.Common.ESSENCE_EARTH_MODIFIER_BONUS;

    @Override
    public boolean isNoLevels() {
        return true;
    }

    @Override
    public void onDamageTargetPre(IToolStackView curio, ModifierEntry entry, LivingHurtEvent event, LivingEntity attacker, LivingEntity target) {
        if (target != null) {
            if (!(attacker instanceof Player)) {
                return;
            }
            float playerMaxHealth = attacker.getMaxHealth();
            float targetMaxHealth = target.getMaxHealth();
            float healthRatio = targetMaxHealth / playerMaxHealth;
            healthRatio = (float) Math.max(1.0f, Math.min(healthRatio, bonus.get()));
            float damage = event.getAmount();
            float newDamage = damage * healthRatio;
            event.setAmount(newDamage);
        }
    }
}
