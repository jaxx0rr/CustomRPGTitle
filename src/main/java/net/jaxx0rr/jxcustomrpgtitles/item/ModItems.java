package net.jaxx0rr.jxcustomrpgtitles.item;

import net.jaxx0rr.jxcustomrpgtitles.JxCustomRPGTitles;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, JxCustomRPGTitles.MODID);

//    public static final RegistryObject<Item> JXCMDBLOCK = ITEMS.register("jxcommandblock",
//            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
