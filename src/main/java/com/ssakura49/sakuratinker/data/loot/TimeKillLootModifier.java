package com.ssakura49.sakuratinker.data.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class TimeKillLootModifier extends LootModifier {
    private final long minTime;
    private final long maxTime;
    private final Item targetItem;
    private final EntityType<?> targetEntity;
    private final Item weapon;

    public static final Codec<TimeKillLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst)
            .and(Codec.LONG.fieldOf("min_time").forGetter(m -> m.minTime))
            .and(Codec.LONG.fieldOf("max_time").forGetter(m -> m.maxTime))
            .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("target_item").forGetter(m -> m.targetItem))
            .and(BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("target_entity").forGetter(m -> m.targetEntity))
            .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("weapon").forGetter(m -> m.weapon))
            .apply(inst, TimeKillLootModifier::new)
    );

    public TimeKillLootModifier(LootItemCondition[] conditions,long minTime, long maxTime, Item targetItem, EntityType<?> targetEntity, Item weapon) {
        super(conditions);
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.targetItem = targetItem;
        this.targetEntity = targetEntity;
        this.weapon = weapon;
    }


    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        long time = context.getLevel().getDayTime() % 24000L;
        if (time < minTime || time > maxTime) return generatedLoot;

        Entity killed = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        Entity killer = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
        if (!(killer instanceof LivingEntity living)) return generatedLoot;
        if (!(killed != null && killed.getType() == targetEntity)) return generatedLoot;

        ItemStack mainhand = living.getMainHandItem();
        if (mainhand.getItem() == weapon) {
            generatedLoot.add(new ItemStack(targetItem));
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
