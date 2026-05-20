package com.ssakura49.sakuratinker.utils.helper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;

public class HealthModify {
    private static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("78364952-8455-5678-9abc-785463259abc");

    public static boolean entityDataModify(Entity target, float newValue) {
        if (target.isRemoved() && !target.isAddedToWorld) {
            return false;
        } else if (target instanceof LivingEntity) {
            LivingEntity le = (LivingEntity)target;
            if (le.getHealth() < 0.0F) {
                return false;
            } else {
                try {
                    SynchedEntityData entityData = target.getEntityData();
                    Map<Integer, SynchedEntityData.DataItem<?>> itemsById = entityData.itemsById;

                    for (SynchedEntityData.DataItem<?> item : itemsById.values()) {
                        Object value = item.value;

                        if (value instanceof Float) {
                            if ((Float) value == le.getHealth()) {
                                ((SynchedEntityData.DataItem<Float>) item).value = newValue;
                            }
                        } else if (value instanceof Double) {
                            if ((Double) value == (double) le.getHealth()) {
                                ((SynchedEntityData.DataItem<Double>) item).value = (double) newValue;
                            }
                        } else if (value instanceof Integer) {
                            if ((float) (Integer) value == le.getHealth()) {
                                ((SynchedEntityData.DataItem<Integer>) item).value = (int) newValue;
                            }
                        }
                    }
                    entityData.set(LivingEntity.DATA_HEALTH_ID, newValue);

                    return true;
                } catch (Throwable var10) {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public static boolean forceModify(Entity entity, float newValue, float newMaxValue) {
        if (entity instanceof LivingEntity le) {
            //HealthMethodHelper.forceSetHealth((Entity)le, newValue, newMaxValue);
            le.entityData.set(LivingEntity.DATA_HEALTH_ID, newValue);
        }

        return true;
    }

    public static boolean tagModify(Entity entity, float newValue) {
        if (entity instanceof LivingEntity le) {
            if (le.getHealth() < 0.0F) {
                return false;
            }

            CompoundTag ct = new CompoundTag();
            le.saveWithoutId(ct);
            ct.putFloat("Health", newValue);
            le.readAdditionalSaveData(ct);
            if (!le.level().isClientSide()) {
                if (entity.level().isClientSide() || !(entity instanceof ServerPlayer serverPlayer)) {
                    return false;
                }

                serverPlayer.connection.send(new ClientboundSetHealthPacket(newValue, serverPlayer.getFoodData().getFoodLevel(), serverPlayer.getFoodData().getSaturationLevel()));
            }
        }

        return true;
    }

    public static boolean attributeModifier(Entity entity, float newMaxValue) {
        if (entity instanceof LivingEntity le) {
            if (le.getHealth() < 0.0F) {
                return false;
            } else {
                AttributeInstance maxHealthAttribute = le.getAttribute(Attributes.MAX_HEALTH);
                if (maxHealthAttribute == null) {
                    return false;
                } else {
                    maxHealthAttribute.removeModifier(HEALTH_MODIFIER_UUID);
                    double currentMax = maxHealthAttribute.getBaseValue();
                    double difference = (double)newMaxValue - currentMax;
                    if (Math.abs(difference) > 0.001) {
                        AttributeModifier modifier = new AttributeModifier(HEALTH_MODIFIER_UUID, "health_modifier", difference, AttributeModifier.Operation.ADDITION);
                        maxHealthAttribute.addPermanentModifier(modifier);
                    }

                    return true;
                }
            }
        } else {
            return false;
        }
    }
}
