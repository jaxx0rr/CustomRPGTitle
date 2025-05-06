package net.jaxx0rr.jxcustomrpgtitles.block;

import net.jaxx0rr.jxcustomrpgtitles.JxCustomRPGTitles;
import net.jaxx0rr.jxcustomrpgtitles.MyCustomBlock;
import net.jaxx0rr.jxcustomrpgtitles.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;
/*
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
DeferredRegister.create(ForgeRegistries.BLOCKS, JxCustomRPGTitles.MODID);

    public static final RegistryObject<Block> JXCMDBLOCK_BLOCK = registerBlock("jxcommandblock_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

}
*/
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, JxCustomRPGTitles.MODID);

    // Register your custom block here
    public static final RegistryObject<Block> JXCMDBLOCK_BLOCK = registerBlock("jxcommandblock_block",
            () -> new MyCustomBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}