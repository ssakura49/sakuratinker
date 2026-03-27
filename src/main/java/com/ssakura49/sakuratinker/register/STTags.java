package com.ssakura49.sakuratinker.register;

import com.ssakura49.sakuratinker.SakuraTinker;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class STTags {
    static boolean tagsLoaded = false;

    public static void init() {
        Items.init();
        Entities.init();
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TagsUpdatedEvent.class, (event) -> tagsLoaded = true);
    }

    public static boolean isTagsLoaded() {
        return tagsLoaded;
    }

    public static class Items {
        public static final TagKey<Item> TINKER_CURIO = local("modifiable/curio");
        public static final TagKey<Item> TINKER_CHARM = local("modifiable/curio/tinker_charm");
        public static final TagKey<Item> TINKER_SPELL_BOOK = local("modifiable/curio/tinker_spell_book");

        public static final TagKey<Item> OUTLINE_METAL = local("outline_metal");
        public static final TagKey<Item> STAR_PARTICLE_ITEM = local( "star_particle_item");
        public static final TagKey<Item> SAKURA_TINKER_METAL = local("sakura_tinker_metal");

        public Items() {
        }

        private static void init() {
        }

        private static TagKey<Item> local(String name) {
            return TagKey.create(Registries.ITEM, SakuraTinker.getResource(name));
        }
    }

    public static class Blocks {

    }

    public static class Entities {
        public static final TagKey<EntityType<?>> NO_GRAPPLE = local("no_grapple");

        private static TagKey<EntityType<?>> local(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, SakuraTinker.getResource(name));
        }

        public Entities(){}

        private static void init(){}
    }

    public static boolean isNoGrapple(Entity entity) {
        if (!tagsLoaded) return false;
        return entity.getType().is(STTags.Entities.NO_GRAPPLE);
    }
}
