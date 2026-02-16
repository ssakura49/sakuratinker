package com.ssakura49.sakuratinker.common.tinkering.modifiers.special;

import com.ssakura49.sakuratinker.generic.BaseModifier;
import com.ssakura49.sakuratinker.library.damagesource.LegacyDamageSource;
import com.ssakura49.sakuratinker.library.damagesource.PercentageBypassInvulnerableTime;
import com.ssakura49.sakuratinker.library.logic.helper.PlayerAdvDataHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class BladeConvergenceModifier extends BaseModifier {
    @Override
    public boolean isNoLevels() {
        return true;
    }

    @Override
    public LegacyDamageSource modifyDamageSource(IToolStackView tool, ModifierEntry entry, LivingEntity attacker, InteractionHand hand, Entity target,
                                                 EquipmentSlot sourceSlot, boolean isFullyCharged, boolean isExtraAttack, boolean isCritical, LegacyDamageSource source) {
        if (!(attacker instanceof ServerPlayer player)) {
            return source;
        }

        int total = PlayerAdvDataHelper.AdvancementCache.getTotal(player);
        int completed = PlayerAdvDataHelper.get(player);

        float percent = (float) completed / (float) total;
        if (percent <= 0f) {
            return source;
        }
        return PercentageBypassInvulnerableTime.any(source.typeHolder(), attacker, attacker, percent);
    }
}
