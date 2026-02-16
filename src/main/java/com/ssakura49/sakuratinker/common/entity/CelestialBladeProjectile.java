package com.ssakura49.sakuratinker.common.entity;

import com.ssakura49.sakuratinker.compat.Photon.TrailRenderer;
import com.ssakura49.sakuratinker.library.interfaces.projectile.IProjectileCritical;
import com.ssakura49.sakuratinker.library.interfaces.projectile.IProjectileDamage;
import com.ssakura49.sakuratinker.library.interfaces.projectile.IProjectileRange;
import com.ssakura49.sakuratinker.library.tinkering.tools.STToolStats;
import com.ssakura49.sakuratinker.register.STEntities;
import com.ssakura49.sakuratinker.register.STItems;
import com.ssakura49.sakuratinker.utils.SafeClassUtil;
import com.ssakura49.sakuratinker.utils.entity.EntityLookUtil;
import com.ssakura49.sakuratinker.utils.render.vec.Cuboid6;
import com.ssakura49.sakuratinker.utils.tinker.AttackUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Queue;

public class CelestialBladeProjectile extends Projectile implements IEntityAdditionalSpawnData, IProjectileCritical, IProjectileDamage, ItemSupplier, IProjectileRange {

    public static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(CelestialBladeProjectile.class, EntityDataSerializers.ITEM_STACK);
    protected static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(CelestialBladeProjectile.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> DATA_BACK = SynchedEntityData.defineId(CelestialBladeProjectile.class, EntityDataSerializers.BOOLEAN);

    private float acceleration = 1.0F;
    private Vec3 originalDirection = Vec3.ZERO;
    private int maxTicks;
    private float curvature;
    private float yVelocity;
    private float baseDamage = 5.0f;
    private boolean critical = false;
    private boolean returning = false;
    private ItemStack itemStack;
    private ToolStack toolStack;
    private StatsNBT stats;
    private InteractionHand hand;
    private EquipmentSlot slot;
    protected int rgb;
    public int backTicks;

    public CelestialBladeTrail trail;
    public Queue<Vec3> trailsQueue = new ArrayDeque<>();

    public Vec3 ellipseCenter;//椭圆中心
    private Vec3 ellipseLongAxis;//长轴方向向量
    private Vec3 ellipseShortAxis;//短轴方向向量
    private float longAxisLength;//长轴长度
    private float shortAxisLength;//短轴长度
    private float startAngle;//椭圆角度起点

    private float range = 10;

    float colorProgress = 0;
    float sliderProgress = 0;
    int colorFrom = new Color(0x1FE6C0).getRGB();
    int colorTo = new Color(0xC67C28).getRGB();

    public int getTicks() {
        return this.maxTicks;
    }

    public int getRgb() {
        return rgb;
    }

    public RandomSource getRandom() {
        return this.random;
    }

    protected int getRandomColor() {
        this.colorProgress = getRandom().nextFloat();
        int color = lerpColor(colorFrom, colorTo,this.colorProgress );

        return color;
    }

    public Vec3 getEllipsePoint(float progress) {
        if (ellipseCenter == null) return this.position();

        float angle = startAngle + progress * (float)Math.PI * 2.0f;

        return ellipseCenter
                .add(ellipseLongAxis.scale(Math.cos(angle) * longAxisLength * 0.5))
                .add(ellipseShortAxis.scale(Math.sin(angle) * shortAxisLength));
    }

    protected int lerpColor(int from, int to, float t) {
        int r = (int) ((from >> 16 & 255) + ((to >> 16 & 255) - (from >> 16 & 255)) * t);
        int g = (int) ((from >> 8 & 255) + ((to >> 8 & 255) - (from >> 8 & 255)) * t);
        int b = (int) ((from & 255) + ((to & 255) - (from & 255)) * t);
        return (r << 16) + (g << 8) + b;
    }

    public CelestialBladeProjectile(EntityType<? extends CelestialBladeProjectile> type, Level level) {
        super(type, level);
        this.curvature = 0.5F + this.random.nextFloat() * 2.0F;
        //updateRadius();
        this.noPhysics = true;
        this.rgb = getRandomColor();
        this.entityData.set(DATA_COLOR, this.rgb);
        this.trail = new CelestialBladeTrail(1, 0.35f, getRandomColor());
    }

    public CelestialBladeProjectile(Level level, Player player, float baseDamage, ToolStack tool, InteractionHand hand) {
        this(STEntities.CELESTIAL_BLADE.get(), level);
        this.baseDamage = baseDamage;
        this.itemStack = ItemStack.EMPTY;
        this.toolStack = tool;
        this.hand = hand;
        this.slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;

        this.setOwner(player);
        Vec3 behind = player.position().subtract(player.getLookAngle().scale(0.5));
        this.setPos(behind.x, behind.y + 0.8D, behind.z);

        Vec3 eyePos = player.getEyePosition(1.0f);
        Vec3 lookVec = player.getLookAngle();

        Entity target = EntityLookUtil.getEntityLookedAt(player, getRange());
        Vec3 endPos;

        if (target != null) {
            endPos = target.getBoundingBox().getCenter();
        } else {
            Vec3 reachVec = eyePos.add(lookVec.scale(getRange()));
            HitResult hit = player.level().clip(new ClipContext(eyePos, reachVec,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            if (hit.getType() != HitResult.Type.MISS) {
                endPos = hit.getLocation();
            } else {
                endPos = reachVec;
            }
        }

        //长轴向量=目标点-玩家位置
        this.ellipseLongAxis = endPos.subtract(this.position());
        this.longAxisLength = (float) ellipseLongAxis.length();
        this.ellipseLongAxis = ellipseLongAxis.normalize();

        //随机短轴
        Vec3 randomVec = new Vec3(random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5).normalize();
        this.ellipseShortAxis = ellipseLongAxis.cross(randomVec).normalize();//垂直向量
        this.shortAxisLength = Math.max(3.0f, 1.5f + random.nextFloat() * 2.0f);//随机长度

        //椭圆中心（玩家位置+长轴一半）
        this.ellipseCenter = this.position().add(ellipseLongAxis.scale(longAxisLength * 0.5));

        //起始角度(随机0~360°)
        this.startAngle = random.nextFloat() * (float)Math.PI * 2f;

        this.maxTicks = (int) (getRange() * 2);

        this.noPhysics = true;

        this.rgb = getRandomColor();
        this.entityData.set(DATA_COLOR, this.rgb);
    }

    public void setTool(ItemStack tool) {
        if (!tool.isEmpty()) {
            this.itemStack = tool.copy();
            this.toolStack = ToolStack.from(tool);
            this.stats = this.toolStack.getStats();
        } else {
            this.itemStack = ItemStack.EMPTY;
            this.toolStack = null;
            this.stats = StatsNBT.EMPTY;
        }
    }

    public Vec3 getTangent(float partialTicks) {
        float progress = ((float)tickCount + partialTicks) / (float)maxTicks;
        if (progress > 1) progress = 1;
        float angle = startAngle + progress * (float)Math.PI * 2.0f;
        Vec3 tangent = ellipseLongAxis.scale(-Math.sin(angle) * longAxisLength * 0.5)
                .add(ellipseShortAxis.scale(Math.cos(angle) * shortAxisLength));
        return tangent.normalize();
    }

    public ToolStack getTool() {
        return this.toolStack;
    }

    private Vec3 calculateDirectionVector(float yaw) {
        return new Vec3(
                -Mth.sin(yaw * Mth.DEG_TO_RAD),
                0.0D,
                Mth.cos(yaw * Mth.DEG_TO_RAD)
        ).normalize();
    }

    public void setItem(ItemStack pStack) {
        if (!pStack.is(this.getDefaultItem()) || pStack.hasTag()) {
            this.getEntityData().set(DATA_ITEM_STACK, pStack.copyWithCount(1));
        }

    }

    public Item getDefaultItem() {
        return STItems.blade_convergence.get();
    };

    public ItemStack getItemRaw() {
        return (ItemStack)this.getEntityData().get(DATA_ITEM_STACK);
    }

    @Override
    public @NotNull ItemStack getItem() {
        ItemStack stack = this.getItemRaw();
        return stack.isEmpty() ? new ItemStack(this.getDefaultItem()) : stack;
    }


    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(key == DATA_BACK){
            boolean back = this.entityData.get(DATA_BACK);
            if(!back) {
                this.backTicks = 20;
            }else{
                this.backTicks = 0;
            }

        }

    }


    @Override
    public void tick() {
        super.tick();
        if (SafeClassUtil.PhotonLoaded && this.tickCount == 1 && this.level().isClientSide) {
            TrailRenderer.attachTrail(this, this.level(),new Vector3f(2f, 0f, 0f));
        }

        if (!level().isClientSide) {
            Entity owner = this.getOwner();
            if (owner instanceof Player player) {
                checkHitEntities(player);
            }

            if (tickCount < maxTicks) {
                Vec3 movement = calculateMovement();
                this.setDeltaMovement(movement);
                this.move(MoverType.SELF, movement);
                if (tickCount >= maxTicks / 2 && !returning) {
                    returning = true;
                    entityData.set(DATA_BACK, true);
                }
            } else {
                discard();
            }
        }
        tickCount++;
    }

    private Vec3 calculateMovement() {
        float progress = (float)tickCount / (float)maxTicks; // 0 ~ 1
        if (progress > 1) progress = 1;
        float angle = startAngle + progress * (float)Math.PI * 2.0f;
        Vec3 pos = ellipseCenter
                .add(ellipseLongAxis.scale(Math.cos(angle) * longAxisLength * 0.5))
                .add(ellipseShortAxis.scale(Math.sin(angle) * shortAxisLength));
        return pos.subtract(this.position());
    }

    private void checkHitEntities(Player owner) {
        AABB aabb = new AABB(this.position(), this.position()).inflate(2.5);
        level().getEntities(this, aabb, e ->
                e instanceof LivingEntity && e != owner && e.isAlive()
        ).forEach(e -> {
            LivingEntity target = (LivingEntity) e;
            target.invulnerableTime = 0;
            if (toolStack != null && !toolStack.isBroken() && hand != null) {
                AttackUtil.attackEntity(
                        toolStack,
                        owner,
                        hand,
                        target,
                        () -> 1.0f,
                        false,
                        slot,
                        false,
                        baseDamage,
                        false
                );
            } else {
                target.hurt(
                        this.damageSources().playerAttack(owner),
                        baseDamage
                );
            }
            if (!returning) {
                returning = true;
                entityData.set(DATA_BACK, true);
            }
        });
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ItemStack $$1 = this.getItemRaw();
        if (!$$1.isEmpty()) {
            tag.put("Item", $$1.save(new CompoundTag()));
        }
        tag.put("Tool", itemStack.serializeNBT());
        tag.putBoolean("Critical", critical);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ItemStack item = ItemStack.of(tag.getCompound("Item"));
        this.setItem(item);
        this.setTool(ItemStack.of(tag.getCompound("Tool")));
        this.critical = tag.getBoolean("Critical");
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeItem(this.itemStack);
        buffer.writeFloat(acceleration);
        buffer.writeInt(maxTicks);
        buffer.writeFloat(curvature);
        buffer.writeFloat(yVelocity);
        buffer.writeDouble(originalDirection.x());
        buffer.writeDouble(originalDirection.y());
        buffer.writeDouble(originalDirection.z());
        buffer.writeBoolean(hand == InteractionHand.MAIN_HAND);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        this.setTool(buffer.readItem());
        this.acceleration = buffer.readFloat();
        this.maxTicks = buffer.readInt();
        this.curvature = buffer.readFloat();
        this.yVelocity = buffer.readFloat();
        this.originalDirection = new Vec3(
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble()
        );
        this.hand = buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        this.slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ITEM_STACK, ItemStack.EMPTY);
        this.entityData.define(DATA_COLOR, 0);
        this.entityData.define(DATA_BACK, false);
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }
    public boolean getCritical() {
        return this.critical;
    }
    public void setDamage(float damage) {
        this.baseDamage = damage;
    }
    public float getDamage() {
        return this.stats.get(ToolStats.ATTACK_DAMAGE) + this.baseDamage + 0.5F;
    }

    @Override
    public void setRange(float range) {
        this.range = range;
    }

    @Override
    public float getRange() {
        return this.range;
    }
}
