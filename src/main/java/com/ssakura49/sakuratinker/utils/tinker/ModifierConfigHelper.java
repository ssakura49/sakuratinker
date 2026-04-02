package com.ssakura49.sakuratinker.utils.tinker;

import com.ssakura49.sakuratinker.STConfig;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ModifierConfigHelper {
    private static Set<ResourceLocation> cachedBlacklist = new HashSet<>();

    public static void reload() {
        cachedBlacklist = STConfig.Common.MODIFIER_BLACKLIST.get().stream()
                .map(ResourceLocation::new)
                .collect(Collectors.toSet());
    }

    public static boolean isBlacklisted(ModifierEntry entry) {
        return cachedBlacklist.contains(entry.getId());
    }
}
