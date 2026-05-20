package com.ssakura49.sakuratinker.common.entity;

import com.mojang.logging.LogUtils;
import com.ssakura49.sakuratinker.api.entity.IOmnipotenceSource;
import com.ssakura49.sakuratinker.common.entity.item.CelestialBladePart;
import com.ssakura49.sakuratinker.library.damagesource.LegacyDamageSource;
import com.ssakura49.sakuratinker.register.STEntities;
import com.ssakura49.sakuratinker.register.STEntityDataSerializers;
import com.ssakura49.sakuratinker.register.STModifiers;
import com.ssakura49.sakuratinker.utils.math.MathUtils;
import com.ssakura49.sakuratinker.utils.tinker.AttackUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CelestialBladeEntity extends Entity implements TraceableEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String TAG_ZENITH_PART = "zenith_part";
    private static final String TAG_OWNER = "owner";
    private static final String TAG_AGE = "age";
    private static final String TAG_PITCH = "pitch";
    private static final String TAG_YAW = "yaw";
    private static final String TAG_ROLL = "roll";

    public static final int LIFESPAN = 20;
    public static final int LENGTH = 15;
    public static final float RADIUS_RATIO = 0.3f;

    protected static final EntityDataAccessor<CelestialBladePart> ZENITH_PART = SynchedEntityData.defineId(CelestialBladeEntity.class, STEntityDataSerializers.CELESTIAL_BLADE_PART.get());

    public void setZenithPart(CelestialBladePart part) {
        this.getEntityData().set(ZENITH_PART, part);
    }

    public CelestialBladePart getZenithPart() {
        return this.getEntityData().get(ZENITH_PART);
    }

    protected static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(CelestialBladeEntity.class, EntityDataSerializers.INT);

    public void setOwnerId(int ownerId) {
        this.getEntityData().set(OWNER_ID, ownerId);
    }

    public int getOwnerId() {
        return this.getEntityData().get(OWNER_ID);
    }

    protected static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(CelestialBladeEntity.class, EntityDataSerializers.INT);

    public void setAge(int age) {
        this.getEntityData().set(AGE, age);
    }

    public int getAge() {
        return this.getEntityData().get(AGE);
    }

    protected static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(CelestialBladeEntity.class, EntityDataSerializers.FLOAT);

    public float getPitch() {
        return this.getEntityData().get(PITCH);
    }

    public void setPitch(float pitch) {
        this.getEntityData().set(PITCH, pitch);
    }

    protected static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(CelestialBladeEntity.class, EntityDataSerializers.FLOAT);

    public float getYaw() {
        return this.getEntityData().get(YAW);
    }

    public void setYaw(float yaw) {
        this.getEntityData().set(YAW, yaw);
    }

    protected static final EntityDataAccessor<Float> ROLL = SynchedEntityData.defineId(CelestialBladeEntity.class, EntityDataSerializers.FLOAT);

    public float getRoll() {
        return this.getEntityData().get(ROLL);
    }

    public void setRoll(float roll) {
        this.getEntityData().set(ROLL, roll);
    }

    public static final EntityDataAccessor<Float> RANGE = SynchedEntityData.defineId(CelestialBladeEntity.class, EntityDataSerializers.FLOAT);

    public float getRange() {
        return this.getEntityData().get(RANGE);
    }

    public void setRange(float range) {
        this.entityData.set(RANGE,range);
    }

    public static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(CelestialBladeEntity.class, EntityDataSerializers.ITEM_STACK);

    public ItemStack getItemStack() {
        return getEntityData().get(DATA_ITEM_STACK);
    }

    public void setItemStack(ItemStack stack) {
        entityData.set(DATA_ITEM_STACK,stack);
    }

    private int clientAge = -1;

    public int getClientAge() {
        return clientAge;
    }

    public float renderPitch, renderYaw, renderRoll, prevPitch, prevYaw, prevRoll;

    private Entity owner;
    private UUID ownerId;

    private ItemStack itemStack;
    private ToolStack toolStack;
    private StatsNBT stats;
    private InteractionHand hand;
    private EquipmentSlot slot;
    private float baseDamage = 5.0f;

    private final List<Entity> damagedEntities = new ArrayList<>();

    @Override
    @Nullable
    public Entity getOwner() {
        return owner;
    }

    public void setOwner(Entity owner) {
        this.ownerId = owner.getUUID();
        this.owner = owner;
        if (!level().isClientSide) {
            setOwnerId(owner.getId());
        }
    }

    public CelestialBladeEntity(EntityType<? extends CelestialBladeEntity> type, Level level) {
        super(type, level);
        noCulling = true;
    }

    public CelestialBladeEntity(Level level, LivingEntity owner, ToolStack tool, InteractionHand hand, float baseDamage) {
        this(STEntities.CELESTIAL_BLADE_ENTITY.get(), level);
        this.setOwner(owner);
        this.setPitch(-owner.getXRot());
        this.setYaw(owner.yHeadRot + 90);
        this.setRoll(random.nextFloat() * 360);
        this.setPos(getIdealPos(owner, owner.position()));
        this.hand = hand;
        this.toolStack = tool;
        this.slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        this.baseDamage = baseDamage;
        this.stats = tool.getStats();
    }

    @Override
    protected void defineSynchedData() {
        getEntityData().define(ZENITH_PART, new CelestialBladePart(Items.WOODEN_SWORD.builtInRegistryHolder(), 0x594319, 0.125, Mth.HALF_PI * 0.5f, 1.75, 1));
        getEntityData().define(OWNER_ID, -1);
        getEntityData().define(AGE, 0);
        getEntityData().define(PITCH, 0f);
        getEntityData().define(YAW, 0f);
        getEntityData().define(ROLL, 0f);
        getEntityData().define(RANGE, 10.0f);
        getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    @Override
    public @NotNull PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
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

    public ToolStack getTool() {
        return this.toolStack;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (owner == null && ownerId != null && level() instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(ownerId);
                if (entity != null) {
                    owner = entity;
                }
                if (owner == null) {
                    ownerId = null;
                }
            }
            Entity owner = getOwner();
            if (owner != null) {
                setPos(getIdealPos(owner, owner.position()));
            }
            if (owner instanceof LivingEntity livingOwner) {
                checkHitEntities(livingOwner);
            }
            if (getAge() > LIFESPAN + 3) {
                discard();
            }
            setAge(getAge() + 1);
        } else {
            prevPitch = clientAge == -1 ? getPitch() : renderPitch;
            prevYaw = clientAge == -1 ? getYaw() : renderYaw;
            prevRoll = clientAge == -1 ? getRoll() : renderRoll;
            renderPitch = getPitch();
            renderYaw = getYaw();
            renderRoll = getRoll();
            if (clientAge == -1) {
                clientAge = getAge();
            }
            clientAge++;
            xo = getX();
            yo = getY();
            zo = getZ();
        }
    }




    private void checkHitEntities(LivingEntity owner) {
        boolean isPvpServerWide;
        if (level() instanceof ServerLevel serverLevel) {
            isPvpServerWide = serverLevel.getServer().isPvpAllowed();
        } else {
            isPvpServerWide = true;
        }
        Vec3 startPos = this.position();
        float currentRange = getRange();
        Vec3 endPos = MathUtils.rotationToPosition(startPos, currentRange, getPitch(), getYaw());
        AABB aabb = new AABB(startPos, endPos).inflate(1.5);
        level().getEntities(this, aabb, e -> e instanceof LivingEntity && e != owner && e.isAlive())
                .forEach(e -> {
                    LivingEntity target = (LivingEntity) e;

                    //剑刃是否切到了目标
                    AABB targetBox = target.getBoundingBox().inflate(target.getPickRadius() + 0.5);
                    if (!targetBox.contains(startPos) && targetBox.clip(startPos, endPos).isEmpty()) {
                        return;
                    }
                    //可能过强
                    //if (damagedEntities.contains(target)) return;
                    if (target instanceof Player targetPlayer) {
                        if (owner instanceof Player attackerPlayer) {
                            if (!attackerPlayer.canHarmPlayer(targetPlayer)) {
                                return;
                            }
                        } else {
                            if (!isPvpServerWide) return;
                        }
                    }
                    target.invulnerableTime = 0;
                    if (toolStack != null && !toolStack.isBroken() && hand != null) {
                        DamageSource source = owner instanceof Player p ?
                                damageSources().playerAttack(p) :
                                damageSources().mobAttack(owner);
                        if (toolStack.getModifierLevel(STModifiers.Omnipotence.get()) > 0&&owner instanceof Player p) {
                            if (source instanceof IOmnipotenceSource omniSource) {
                                omniSource.sakuratinker$setOmnipotence(true);
                            }
                            target.hurt(source, baseDamage);
                            AttackUtil.attackEntity(toolStack, owner, hand, target, () -> 1.0f, false, slot, false, baseDamage, false);
                        } else {
                            AttackUtil.attackEntity(toolStack, owner, hand, target, () -> 1.0f, false, slot, false, baseDamage, false);
                        }

                    } else {
                        target.hurt(owner instanceof Player p ? damageSources().playerAttack(p) : damageSources().mobAttack(owner), baseDamage);
                    }
                    //damagedEntities.add(target);
                });
    }

    public Vec3 getIdealPos(Entity owner, Vec3 ownerPos) {
        return ownerPos.add(0, owner.getBbHeight() * 0.6f, 0);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 1024;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.contains(TAG_ZENITH_PART)) {
            CelestialBladePart.CODEC.parse(NbtOps.INSTANCE, compoundTag.get(TAG_ZENITH_PART)).resultOrPartial(s -> LOGGER.warn("Failed to parse Zenith Slash: {}", s)).ifPresent(this::setZenithPart);
        }
        if (compoundTag.hasUUID(TAG_OWNER)) {
            ownerId = compoundTag.getUUID(TAG_OWNER);
        }
        setAge(compoundTag.getInt(TAG_AGE));
        setPitch(compoundTag.getFloat(TAG_PITCH));
        setYaw(compoundTag.getFloat(TAG_YAW));
        setRoll(compoundTag.getFloat(TAG_ROLL));
        if (compoundTag.contains("ToolStack")) {
            this.itemStack = ItemStack.of(compoundTag.getCompound("ToolStack"));
            this.toolStack = ToolStack.from(this.itemStack);
        }
        this.baseDamage = compoundTag.getFloat("BaseDamage");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.put(TAG_ZENITH_PART, CelestialBladePart.CODEC.encodeStart(NbtOps.INSTANCE, getZenithPart()).getOrThrow(false, LOGGER::warn));
        if (owner != null) {
            compoundTag.putUUID(TAG_OWNER, owner.getUUID());
        }
        compoundTag.putInt(TAG_AGE, getAge());
        compoundTag.putFloat(TAG_PITCH, getPitch());
        compoundTag.putFloat(TAG_YAW, getYaw());
        compoundTag.putFloat(TAG_ROLL, getRoll());
        if (this.itemStack != null) {
            compoundTag.put("ToolStack", this.itemStack.serializeNBT());
        }
        compoundTag.putFloat("BaseDamage", this.baseDamage);
    }
}
