package com.ssakura49.sakuratinker.common.entity;

import com.ssakura49.sakuratinker.utils.entity.PositionPoseProperties;
import com.ssakura49.sakuratinker.utils.entity.PositionPoseTrail;
import com.ssakura49.sakuratinker.utils.entity.TrailProperties;

public class CelestialBladeTrail extends PositionPoseTrail<CelestialBladeProjectile> {
    public CelestialBladeTrail(int size, float widthScale, int color) {
        super(size, widthScale, color);
    }

    @Override
    public int getRgb(CelestialBladeProjectile holder, TrailProperties properties) {
        return holder.getRgb();
    }

    @Override
    public void generateTrail(CelestialBladeProjectile holder, int ticks) {
        if(trailsQueue.size() >= 8){
            trailsQueue.poll();
        }

        if(holder.getOwner() != null) {
            trailsQueue.add(new PositionPoseProperties(holder.position(), holder.getXRot(), holder.getYRot()));
        }
    }

}
