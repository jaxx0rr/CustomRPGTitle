package net.jaxx0rr.jxcustomrpgtitles;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.jaxx0rr.jxcustomrpgtitles.block.ModBlocks;
import net.jaxx0rr.jxcustomrpgtitles.item.ModItems;
import net.jaxx0rr.jxcustomrpgtitles.network.ModMessages;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(JxCustomRPGTitles.MODID)
public class JxCustomRPGTitles
{
    public static final String MODID = "jxcustomrpgtitles";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static boolean GLOBAL_BLOCK_DISABLE = false;

    public JxCustomRPGTitles(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenus.register(modEventBus);
        ModMessages.register();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            //event.accept(ModItems.JXCMDBLOCK);
            event.accept(ModBlocks.JXCMDBLOCK_BLOCK);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            MenuScreens.register(ModMenus.MY_BLOCK_MENU.get(), MyBlockScreen::new);
        }
    }

    private void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("jxcmd_disable_all_blocks")
                .requires(source -> source.hasPermission(2))
                .executes(ctx -> {
                    GLOBAL_BLOCK_DISABLE = true;
                    ctx.getSource().sendSuccess(() -> Component.literal("All custom blocks disabled."), true);
                    return 1;
                }));

        dispatcher.register(Commands.literal("jxcmd_enable_all_blocks")
                .requires(source -> source.hasPermission(2))
                .executes(ctx -> {
                    GLOBAL_BLOCK_DISABLE = false;
                    ctx.getSource().sendSuccess(() -> Component.literal("All custom blocks enabled."), true);
                    return 1;
                }));

    }

}
