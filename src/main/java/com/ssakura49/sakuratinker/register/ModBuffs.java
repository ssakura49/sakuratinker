package com.ssakura49.sakuratinker.register;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.api.entity.buff.CustomBuff;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class ModBuffs {
    public static final DeferredRegister<CustomBuff> BUFFS = DeferredRegister.create(SakuraTinker.getResource("custom_buffs"), SakuraTinker.MODID);

    public static final Supplier<IForgeRegistry<CustomBuff>> REGISTRY =
            BUFFS.makeRegistry(RegistryBuilder::new);

    public static void register(IEventBus eventBus) {
        BUFFS.register(eventBus);
    }
}
