package com.ssakura49.sakuratinker.compat.IceAndFireCompat.modifiers;

import com.ssakura49.sakuratinker.generic.BaseModifier;
import com.ssakura49.sakuratinker.utils.SafeClassUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class BreathResistanceModifier extends BaseModifier {
    @Override
    public float onModifyTakeDamage(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        if (SafeClassUtil.IceAndFireLoaded) {
            LivingEntity entity = context.getEntity();
            if (isDragonEntity(entity)) {
                return (amount * (1.0F - 0.1F * (float)modifier.getLevel()));
            }
            return amount;
        }
        return amount;
    }

    private boolean isDragonEntity(LivingEntity target) {
        ResourceLocation id = EntityType.getKey(target.getType());
        if (id == null) return false;
        String namespace = id.getNamespace();
        String path = id.getPath();
        return (namespace.equals("iceandfire")) && (path.contains("dragon") || path.contains("fire_dragon") || path.contains("ice_dragon") || path.contains("lightning_dragon"));
    }
}
