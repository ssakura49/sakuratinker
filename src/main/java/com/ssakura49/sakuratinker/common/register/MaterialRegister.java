package com.ssakura49.sakuratinker.common.register;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.SakuraTinkerModule;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.registration.deferred.SynchronizedDeferredRegister;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;

public class MaterialRegister extends SakuraTinkerModule {
    public static final String MODID = "sakuratinker";
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SakuraTinker.MODID);
    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(SakuraTinker.MODID);
    protected static final SynchronizedDeferredRegister<ParticleType<?>> PARTICLE_TYPES = SynchronizedDeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, SakuraTinker.MODID);

    public static final MaterialId test = createMaterial("storm");
    public static final MaterialId soul_sakura = createMaterial("soul_sakura");
    public static final MaterialId youkai = createMaterial("youkai");


    private static ModifierId id(String name) {
        return new ModifierId(SakuraTinker.MODID, name);
    }
    public static MaterialId createMaterial(String name) {
        return new MaterialId(new ResourceLocation(SakuraTinker.MODID, name));
    }
}
