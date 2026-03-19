package com.ssakura49.sakuratinker.common.tinkering.modifiers.curio;

import com.ssakura49.sakuratinker.utils.entity.EntityUtil;
import com.ssakura49.tinkercuriolib.hook.TCLibHooks;
import com.ssakura49.tinkercuriolib.hook.combat.CurioDamageTargetPostModifierHook;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.shared.TinkerEffects;

public class LaceratingCurioModifier extends Modifier implements CurioDamageTargetPostModifierHook {
    public LaceratingCurioModifier(){}
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this, TCLibHooks.CURIO_DAMAGE_TARGET_POST);
    }
    @Override
    public void onCurioDamageTargetPost(IToolStackView curio, ModifierEntry entry, LivingDamageEvent event, LivingEntity attacker, LivingEntity target) {
        if (attacker instanceof Player player) {
            if (EntityUtil.isFullChance(player) && RANDOM.nextBoolean()) {
                applyEffect(target, entry.getLevel());
            }
        }
    }

    private static void applyEffect(LivingEntity target, int level) {
        TinkerEffects.bleeding.get().apply(target, 1 + 20 * (2 + (RANDOM.nextInt(level + 3))), level - 1, true);
    }
}
