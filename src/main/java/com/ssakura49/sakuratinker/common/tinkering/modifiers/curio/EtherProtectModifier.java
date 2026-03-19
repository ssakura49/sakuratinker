package com.ssakura49.sakuratinker.common.tinkering.modifiers.curio;

import com.ssakura49.tinkercuriolib.hook.TCLibHooks;
import com.ssakura49.tinkercuriolib.hook.armor.CurioTakeDamagePreModifierHook;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EtherProtectModifier extends NoLevelsModifier implements CurioTakeDamagePreModifierHook {
    public EtherProtectModifier(){}

    private static final Map<UUID, Boolean> shieldActiveMap = new HashMap<>();
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
        super.registerHooks(builder);
        builder.addHook(this, TCLibHooks.CURIO_TAKE_DAMAGE_PRE);
    }

    @Override
    public void onCurioTakeDamagePre(IToolStackView curio, ModifierEntry entry, LivingHurtEvent event, LivingEntity entity, DamageSource source) {
        UUID entityId = entity.getUUID();
        if (entity.getHealth() / entity.getMaxHealth() <= 0.4f) {
            shieldActiveMap.put(entityId, true);
        } else {
            shieldActiveMap.put(entityId, false);
        }
        if (shieldActiveMap.getOrDefault(entityId, false)) {
            event.setAmount(event.getAmount() * 0.5f);
            if (source.is(DamageTypeTags.IS_PROJECTILE) && source.getDirectEntity() instanceof Projectile projectile) {
                Vec3 motion = projectile.getDeltaMovement().scale(-1);
                projectile.setDeltaMovement(motion);
                event.setCanceled(true);
            }
            if (source.getEntity() instanceof LivingEntity attacker) {
                Vec3 ka = entity.position().subtract(attacker.position()).normalize().scale(2.0);
                attacker.setDeltaMovement(ka);
            }
        }
    }
}
