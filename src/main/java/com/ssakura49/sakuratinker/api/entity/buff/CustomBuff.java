package com.ssakura49.sakuratinker.api.entity.buff;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.HashMap;
import java.util.Map;

public abstract class CustomBuff {
    private final OverwriteStrategy strategy;
    private final PersistenceLevel persistence;
    private final Map<Attribute, AttributeModifierTemplate> attributeModifiers = new HashMap<>();

    protected CustomBuff(OverwriteStrategy strategy, PersistenceLevel persistence) {
        this.strategy = strategy;
        this.persistence = persistence;
    }

    public OverwriteStrategy getStrategy() { return strategy; }
    public PersistenceLevel getPersistence() { return persistence; }

    public void onTick(LivingEntity entity, int level) {}

    public void onAdded(LivingEntity entity, int level) {}

    public void onRemoved(LivingEntity entity, int level) {}

    protected void addAttributeModifier(Attribute attribute, String name, double amount, AttributeModifier.Operation operation) {
        attributeModifiers.put(attribute, new AttributeModifierTemplate(name, amount, operation));
    }

    public Map<Attribute, AttributeModifierTemplate> getAttributeModifiers() {
        return attributeModifiers;
    }

    record AttributeModifierTemplate(String name, double amount, AttributeModifier.Operation operation) {}


    public enum OverwriteStrategy {
        HIGHEST_LEVEL,//等级高者优先
        ADDITIVE_TIME,//时间叠加
        REPLACE,      //总是替换
        COEXIST       //共存模式
    }

    // 持久化策略
    public enum PersistenceLevel {
        NONE,            //死亡/下线消失
        SAVE_ON_EXIT,    //下线保存，死亡消失
        PERSIST_DEATH    //死亡不消失
    }
}