package com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiers;

import com.ssakura49.sakuratinker.common.register.EffectsRegister;
import com.ssakura49.sakuratinker.common.tinkering.modifiers.modifiermodule;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

@Mod.EventBusSubscriber
public class MortalWoundModifier extends modifiermodule {
    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (context.getLivingTarget() != null) {
            LivingEntity target = context.getLivingTarget();
            MobEffectInstance effect = new MobEffectInstance(EffectsRegister.MORTAL_WOUND.get(), 200, 0, false, true, true);
            context.getAttacker();
            target.addEffect(effect, context.getAttacker());
        }
    }
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(EffectsRegister.MORTAL_WOUND.get())) {
            float reduced = event.getAmount() * 0.25f;
            event.setAmount(reduced);
        }
    }
}
