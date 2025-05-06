package net.jaxx0rr.jxcustomrpgtitles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.jaxx0rr.jxcustomrpgtitles.JxCustomRPGTitles.MODID;


public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

    public static final RegistryObject<MenuType<MyBlockMenu>> MY_BLOCK_MENU =
            MENUS.register("my_block_menu",
                    () -> IForgeMenuType.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        Level level = inv.player.level();
                        if (level.getBlockEntity(pos) instanceof MyCustomBlockEntity blockEntity) {
                            return new MyBlockMenu(windowId, inv, blockEntity);
                        }
                        return null; // fallback if block entity isn't correct
                    }));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
