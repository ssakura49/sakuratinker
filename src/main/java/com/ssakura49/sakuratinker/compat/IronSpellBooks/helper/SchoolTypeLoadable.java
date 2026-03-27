package com.ssakura49.sakuratinker.compat.IronSpellBooks.helper;

import com.google.gson.JsonSyntaxException;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.ResourceLocationLoadable;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.util.typed.TypedMap;

public class SchoolTypeLoadable {
    public static final StringLoadable<SchoolType> SCHOOL =
            Loadables.RESOURCE_LOCATION.xmap((id, error) -> {
                SchoolType school = SchoolRegistry.getSchool(id);
                if (school != null) {
                    return school;
                }
                throw error.create("Unknown school " + id);
            }, (school, error) -> {
                ResourceLocation id = school.getId();
                if (id != null) {
                    return id;
                }
                throw error.create("Attempt to serialize unregistered school " + school);
            });
}
