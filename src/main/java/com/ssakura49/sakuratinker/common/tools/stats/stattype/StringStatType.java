package com.ssakura49.sakuratinker.common.tools.stats.stattype;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;

public class StringStatType implements IToolStat<String> {
    private final ToolStatId name;
    private final String defaultValue;

    public StringStatType(ToolStatId name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    @Override
    public ToolStatId getName() {
        return name;
    }

    @Override
    public @NotNull String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public @NotNull Object makeBuilder() {
        return new StringBuilder();
    }

    @Override
    public @NotNull String build(@NotNull ModifierStatsBuilder parent, Object builder) {
        return builder.toString();
    }

    @Override
    public void update(ModifierStatsBuilder builder, @NotNull String value) {
        builder.updateStat(this, (StringBuilder sb) -> {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(value);
        });
    }

    @Override
    public @Nullable String read(Tag tag) {
        if (tag instanceof StringTag stringTag) {
            return stringTag.getAsString();
        }
        return null;
    }

    @Override
    public @Nullable Tag write(String value) {
        return StringTag.valueOf(value);
    }

    @Override
    public @NotNull String deserialize(JsonElement json) {
        return json.getAsString();
    }

    @Override
    public @NotNull JsonElement serialize(@NotNull String value) {
        return new JsonPrimitive(value);
    }

    @Override
    public @NotNull String fromNetwork(FriendlyByteBuf buffer) {
        return buffer.readUtf();
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, String value) {
        buffer.writeUtf(value);
    }

    @Override
    public @NotNull Component formatValue(@NotNull String value) {
        return Component.literal(value);
    }
}
