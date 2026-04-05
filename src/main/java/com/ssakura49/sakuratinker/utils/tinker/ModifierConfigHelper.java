package com.ssakura49.sakuratinker.utils.tinker;

import com.ssakura49.sakuratinker.STConfig;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ModifierConfigHelper {
    public static boolean isBlacklisted(ModifierEntry entry) {
        List<? extends String> list = STConfig.Common.MODIFIER_BLACKLIST.get();

        ResourceLocation id = entry.getId();

        for (String s : list) {
            if (id.equals(ResourceLocation.parse(s))) {
                return true;
            }
        }
        return false;
    }
}
