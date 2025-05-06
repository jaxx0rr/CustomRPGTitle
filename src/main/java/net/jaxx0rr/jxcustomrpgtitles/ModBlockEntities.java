package net.jaxx0rr.jxcustomrpgtitles;

import net.jaxx0rr.jxcustomrpgtitles.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.jaxx0rr.jxcustomrpgtitles.JxCustomRPGTitles.MODID;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, JxCustomRPGTitles.MODID);

    public static final RegistryObject<BlockEntityType<MyCustomBlockEntity>> MY_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("jxcommandblock_block",
                    () -> BlockEntityType.Builder.of(MyCustomBlockEntity::new, ModBlocks.JXCMDBLOCK_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
