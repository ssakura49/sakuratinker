package com.ssakura49.sakuratinker.common.items;

import com.ssakura49.sakuratinker.SakuraTinker;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;

public class SakuraTinkerItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SakuraTinker.MODID);
    private static final ItemDeferredRegisterExtension ITEMSA = new ItemDeferredRegisterExtension(SakuraTinker.MODID);

    //public static BlockItem registerItemBlock(Block block) {
    //    return new BlockItem(block, new Item.Properties());
    //}
    //private static final Item.Properties ToolItem = new Item.Properties().stacksTo(1);
    //private static final Item.Properties CastItem = new Item.Properties().stacksTo(64);
    //private static final Item.Properties Item = new Item.Properties().stacksTo(64);


    //public static RegistryObject<Item> void_steel_ingot = ITEMS.register("void_steel_ingot", SakuraTinkerItems::register);
    public static RegistryObject<Item> youkai_ingot = ITEMS.register("youkai_ingot", SakuraTinkerItems::register);
    public static RegistryObject<Item> soul_sakura = ITEMS.register("soul_sakura", SakuraTinkerItems::register);

    public static RegistryObject<Item> test_ingot = ITEMS.register("test_ingot", SakuraTinkerItems::register);


    public static Item register() {
        return new Item(new Item.Properties());
    }

    public static void addTabItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        output.accept(test_ingot.get());
        output.accept(youkai_ingot.get());
        output.accept(soul_sakura.get());
    }
}
