package com.ssakura49.sakuratinker.compat.Photon;

import com.lowdragmc.photon.client.fx.EntityEffect;
import com.lowdragmc.photon.client.fx.FXHelper;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.utils.render.vec.Vector3;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class TrailRenderer {
    public static void attachTrail(Entity entity, Level level, Vector3f offset) {
        var fx = FXHelper.getFX(ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID, "trail"));
        if (fx == null) {
            return;
        }

        var effect = new EntityEffect(fx, level, entity, EntityEffect.AutoRotate.FORWARD);
        effect.setForcedDeath(true);

        effect.setOffset(offset);

        effect.start();
    }
}
