package com.ssakura49.sakuratinker.data.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ssakura49.sakuratinker.register.STLootConditions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Set;

public class TimeKillCondition implements LootItemCondition {
    public static final SerializerImpl SERIALIZER = new SerializerImpl();

    private final long minTime;
    private final long maxTime;
    @Nullable private final Item mainhandItem;
    @Nullable
    private final Item offhandItem;
    private final MatchMode matchMode;

    public TimeKillCondition(long minTime, long maxTime, @Nullable Item mainhandItem, @Nullable Item offhandItem, MatchMode matchMode) {
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.mainhandItem = mainhandItem;
        this.offhandItem = offhandItem;
        this.matchMode = matchMode;
    }

    @Override
    public boolean test(LootContext context) {
        long time = context.getLevel().getDayTime() % 24000L;
        if (time < minTime || time > maxTime) return false;

        Entity killer = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
        if (!(killer instanceof LivingEntity entity)) return false;

        boolean mainMatch = true;
        boolean offMatch = true;

        if (mainhandItem != null) {
            ItemStack main = entity.getItemBySlot(EquipmentSlot.MAINHAND);
            mainMatch = !main.isEmpty() && main.getItem() == mainhandItem;
        }

        if (offhandItem != null) {
            ItemStack off = entity.getItemBySlot(EquipmentSlot.OFFHAND);
            offMatch = !off.isEmpty() && off.getItem() == offhandItem;
        }

        return switch (matchMode) {
            case MAIN_ONLY -> mainMatch;
            case OFF_ONLY -> offMatch;
            case BOTH -> mainMatch && offMatch;
            case ANY -> mainMatch || offMatch;
        };
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.KILLER_ENTITY);
    }

    @Override
    public LootItemConditionType getType() {
        return STLootConditions.TIME_KILL_CONDITION;
    }

    public static class SerializerImpl implements Serializer<TimeKillCondition> {
        @Override
        public void serialize(JsonObject json, TimeKillCondition condition, JsonSerializationContext context) {
            json.addProperty("min_time", condition.minTime);
            json.addProperty("max_time", condition.maxTime);
            json.addProperty("match_mode", condition.matchMode.name().toLowerCase(Locale.ROOT));

            if (condition.mainhandItem != null)
                json.addProperty("mainhand", BuiltInRegistries.ITEM.getKey(condition.mainhandItem).toString());
            if (condition.offhandItem != null)
                json.addProperty("offhand", BuiltInRegistries.ITEM.getKey(condition.offhandItem).toString());
        }

        @Override
        public TimeKillCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            long min = json.has("min_time") ? json.get("min_time").getAsLong() : 0L;
            long max = json.has("max_time") ? json.get("max_time").getAsLong() : 24000L;
            MatchMode mode = json.has("match_mode") ? MatchMode.fromString(json.get("match_mode").getAsString()) : MatchMode.ANY;

            Item main = null;
            Item off = null;
            if (json.has("mainhand")) {
                ResourceLocation id = ResourceLocation.parse(json.get("mainhand").getAsString());
                main = BuiltInRegistries.ITEM.get(id);
            }
            if (json.has("offhand")) {
                ResourceLocation id = ResourceLocation.parse(json.get("offhand").getAsString());
                off = BuiltInRegistries.ITEM.get(id);
            }

            return new TimeKillCondition(min, max, main, off, mode);
        }
    }
    public enum MatchMode {
        ANY,
        MAIN_ONLY,
        OFF_ONLY,
        BOTH;

        public static MatchMode fromString(String s) {
            return switch (s.toLowerCase(Locale.ROOT)) {
                case "main_only" -> MAIN_ONLY;
                case "off_only" -> OFF_ONLY;
                case "both" -> BOTH;
                default -> ANY;
            };
        }
    }
}
