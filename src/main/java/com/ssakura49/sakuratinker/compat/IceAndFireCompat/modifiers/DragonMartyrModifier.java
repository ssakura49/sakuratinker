package com.ssakura49.sakuratinker.compat.IceAndFireCompat.modifiers;

import com.ssakura49.sakuratinker.generic.BaseModifier;
import com.ssakura49.sakuratinker.utils.SafeClassUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public class DragonMartyrModifier extends BaseModifier {
    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float actualDamage) {
        LivingEntity attacker = context.getAttacker();
        LivingEntity target = context.getLivingTarget();
        if (target != null && SafeClassUtil.IceAndFireLoaded && isDragonEntity(target)) {
            return actualDamage * (1 + 0.2f * modifier.getLevel());
        }
        return baseDamage;
    }

    @Override
    public void onProjectileHitTarget(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry entry, Projectile projectile, AbstractArrow arrow, EntityHitResult hit, LivingEntity attacker, LivingEntity target) {
        if (SafeClassUtil.IceAndFireLoaded && isDragonEntity(target)) {
            float bonusDamage = (float) (arrow.getBaseDamage() * 0.2f * entry.getLevel());
            arrow.setBaseDamage(arrow.getBaseDamage() + bonusDamage);
        }
    }

    private boolean isDragonEntity(LivingEntity target) {
        ResourceLocation id = EntityType.getKey(target.getType());
        String namespace = id.getNamespace();
        String path = id.getPath();
        return (namespace.equals("iceandfire")) && (path.contains("dragon") || path.contains("fire_dragon") || path.contains("ice_dragon") || path.contains("lightning_dragon"));
    }
}
