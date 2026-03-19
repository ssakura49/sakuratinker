package com.ssakura49.sakuratinker.common.tinkering.modifiers.curio;

import com.ssakura49.tinkercuriolib.hook.TCLibHooks;
import com.ssakura49.tinkercuriolib.hook.armor.CurioTakeDamagePreModifierHook;
import com.ssakura49.tinkercuriolib.tools.modifier.base.TCLibBaseModifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Random;

public class ColorizationModifier extends NoLevelsModifier implements CurioTakeDamagePreModifierHook {
    private static final float MAX_DODGE_CHANCE = 0.80f;

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this, TCLibHooks.CURIO_TAKE_DAMAGE_PRE);
    }

    @Override
    public void onCurioTakeDamagePre(IToolStackView curio, ModifierEntry entry, LivingHurtEvent event, LivingEntity entity, DamageSource source) {
        float armorValue = entity.getArmorValue();
        float dodgeChance = Math.min(armorValue * 0.01f, MAX_DODGE_CHANCE);
        Random random = new Random();
        if (random.nextFloat() < dodgeChance) {
            event.setCanceled(true);
        }
    }
}
