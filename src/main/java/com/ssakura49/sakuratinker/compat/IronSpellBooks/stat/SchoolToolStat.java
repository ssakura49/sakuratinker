package com.ssakura49.sakuratinker.compat.IronSpellBooks.stat;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class SchoolToolStat implements IToolStat<SchoolType> {

    private final ToolStatId name;
    private final Supplier<SchoolType> defaultValue;

    public SchoolToolStat(ToolStatId name, Supplier<SchoolType> defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    @Override
    public ToolStatId getName() {
        return name;
    }

    @Override
    public SchoolType getDefaultValue() {
        return defaultValue.get();
    }

    @Override
    public Object makeBuilder() {
        return new AtomicReference<>(getDefaultValue()); // ✅ 注意这里
    }

    @Override
    public SchoolType build(ModifierStatsBuilder parent, Object builder) {
        if (builder == null) {
            return getDefaultValue();
        }
        return ((AtomicReference<SchoolType>) builder).get();
    }

    @Override
    public void update(ModifierStatsBuilder builder, SchoolType value) {
        builder.updateStat(this, (AtomicReference<SchoolType> ref) -> {
            ref.set(value);
        });
    }

    @Override
    public SchoolType read(Tag tag) {
        if (tag instanceof StringTag s) {
            ResourceLocation id = ResourceLocation.parse(s.getAsString());
            SchoolType school = SchoolRegistry.getSchool(id);
            return school != null ? school : getDefaultValue();
        }
        return getDefaultValue();
    }

    @Override
    public Tag write(SchoolType value) {
        return StringTag.valueOf(value.getId().toString());
    }

    @Override
    public SchoolType deserialize(JsonElement json) {
        ResourceLocation id = ResourceLocation.parse(json.getAsString());
        SchoolType school = SchoolRegistry.getSchool(id);
        return school != null ? school : getDefaultValue();
    }

    @Override
    public JsonElement serialize(SchoolType value) {
        return new JsonPrimitive(value.getId().toString());
    }

    @Override
    public SchoolType fromNetwork(FriendlyByteBuf buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        SchoolType school = SchoolRegistry.getSchool(id);
        return school != null ? school : getDefaultValue();
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, SchoolType value) {
        buffer.writeResourceLocation(value.getId());
    }

    @Override
    public Component formatValue(SchoolType value) {
        return value.getDisplayName();
    }
}
