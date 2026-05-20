package com.ssakura49.sakuratinker.api.entity.buff;

import com.ssakura49.sakuratinker.network.PacketHandler;
import com.ssakura49.sakuratinker.network.s2c.ClientboundSyncBuffPacket;
import com.ssakura49.sakuratinker.register.ModBuffs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;

public class CustomBuffHolder {
    private final List<BuffInstance> activeBuffs = new ArrayList<>();

    public void addBuff(LivingEntity entity,BuffInstance incoming) {
        CustomBuff type = incoming.getType();
        //处理非共存
        if (type.getStrategy() != CustomBuff.OverwriteStrategy.COEXIST) {
            Optional<BuffInstance> existing = activeBuffs.stream()
                    .filter(i -> i.getType() == type).findFirst();

            if (existing.isPresent()) {
                resolveConflict(entity,existing.get(), incoming);
                return;
            }
        }
        activeBuffs.add(incoming);
        applyAttributes(entity, incoming);
        incoming.getType().onAdded(null, incoming.getLevel());
    }

    private void resolveConflict(LivingEntity entity,BuffInstance current, BuffInstance incoming) {
        switch (current.getType().getStrategy()) {
            case HIGHEST_LEVEL -> {
                if (incoming.getLevel() > current.getLevel()) {
                    removeAttributes(entity, current);
                    current.setDuration(incoming.getDuration());
                    applyAttributes(entity, current);
                }
            }
            case ADDITIVE_TIME -> current.setDuration(current.getDuration() + incoming.getDuration());
            case REPLACE -> {
                removeAttributes(entity, current);
                activeBuffs.remove(current);
                current.getType().onRemoved(entity, current.getLevel());
                addBuffInternal(entity, incoming);
            }
        }
    }
    private void addBuffInternal(LivingEntity entity, BuffInstance incoming) {
        applyAttributes(entity, incoming);
        incoming.setAttributesApplied(true);
        activeBuffs.add(incoming);
        incoming.getType().onAdded(entity, incoming.getLevel());
        if (!entity.level().isClientSide) {
            ResourceLocation id = ModBuffs.REGISTRY.get().getKey(incoming.getType());
            if (id != null) {
                PacketHandler.sentToTrackingEntityAndPlayer(entity, new ClientboundSyncBuffPacket(
                        entity.getId(), id, incoming.getDuration(), incoming.getLevel(), false));
            }
        }
    }

    public void tickAll(LivingEntity entity) {
        Iterator<BuffInstance> it = activeBuffs.iterator();
        List<BuffInstance> toRemove = new ArrayList<>();
        while (it.hasNext()) {
            BuffInstance instance = it.next();
            if (!instance.isAttributesApplied()) {
                applyAttributes(entity, instance);
                instance.setAttributesApplied(true);
            }

            instance.tick();
            instance.getType().onTick(entity, instance.getLevel());
            if (instance.isExpired()) {
                toRemove.add(instance);
                it.remove();
            }
        }
        for (BuffInstance deadInstance : toRemove) {
            removeAttributes(entity, deadInstance);
            deadInstance.getType().onRemoved(entity, deadInstance.getLevel());
        }
    }

    public List<BuffInstance> getActiveBuffs() { return activeBuffs; }

    public void saveNBT(CompoundTag nbt) {
        ListTag list = new ListTag();
        IForgeRegistry<CustomBuff> registry = ModBuffs.REGISTRY.get(); // 获取注册表
        for (BuffInstance instance : activeBuffs) {
            if (instance.getType().getPersistence() != CustomBuff.PersistenceLevel.NONE) {
                CompoundTag tag = new CompoundTag();
                ResourceLocation id = registry.getKey(instance.getType());
                if (id != null) {
                    tag.putString("id", id.toString());
                    tag.putInt("duration", instance.getDuration());
                    tag.putInt("level", instance.getLevel());
                    tag.putUUID("modifierId", instance.getModifierId());
                    list.add(tag);
                }
            }
        }
        nbt.put("ActiveBuffs", list);
    }

    public void loadNBT(CompoundTag nbt) {
        activeBuffs.clear();
        ListTag list = nbt.getList("ActiveBuffs", Tag.TAG_COMPOUND);
        IForgeRegistry<CustomBuff> registry = ModBuffs.REGISTRY.get();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("id"));
            CustomBuff type = (id != null) ? registry.getValue(id) : null;
            if (type != null) {
                UUID savedUuid = tag.hasUUID("modifierId") ? tag.getUUID("modifierId") : UUID.randomUUID();
                BuffInstance instance = new BuffInstance(type, tag.getInt("duration"), tag.getInt("level"), savedUuid);
                activeBuffs.add(instance);
            }
        }
    }

    public void applyAttributes(LivingEntity entity, BuffInstance instance) {
        CustomBuff type = instance.getType();
        type.getAttributeModifiers().forEach((attribute, template) -> {
            AttributeInstance attributeInstance = entity.getAttribute(attribute);
            if (attributeInstance != null) {
                AttributeModifier modifier = new AttributeModifier(
                        instance.getModifierId(),
                        template.name(),
                        template.amount() * (instance.getLevel() + 1),
                        template.operation()
                );

                if (!attributeInstance.hasModifier(modifier)) {
                    attributeInstance.addPermanentModifier(modifier);
                }
            }
        });
    }

    public void removeAttributes(LivingEntity entity, BuffInstance instance) {
        CustomBuff type = instance.getType();
        type.getAttributeModifiers().forEach((attribute, template) -> {
            AttributeInstance attributeInstance = entity.getAttribute(attribute);
            if (attributeInstance != null) {
                attributeInstance.removeModifier(instance.getModifierId());
            }
        });
    }
}