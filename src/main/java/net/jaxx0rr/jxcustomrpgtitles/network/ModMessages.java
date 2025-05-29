package net.jaxx0rr.jxcustomrpgtitles.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("jxcustomrpgtitles", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void register() {
        INSTANCE.registerMessage(
                id++,
                UpdateCustomTextPacket.class,
                UpdateCustomTextPacket::encode,
                UpdateCustomTextPacket::decode,
                UpdateCustomTextPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                UpdateRedstoneModePacket.class,
                UpdateRedstoneModePacket::encode,
                UpdateRedstoneModePacket::decode,
                UpdateRedstoneModePacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                UpdateSingleUsePacket.class,
                UpdateSingleUsePacket::encode,
                UpdateSingleUsePacket::decode,
                UpdateSingleUsePacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                UpdateCreativeOnlyPacket.class,
                UpdateCreativeOnlyPacket::encode,
                UpdateCreativeOnlyPacket::decode,
                UpdateCreativeOnlyPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                ClearTriggerHistoryPacket.class,
                ClearTriggerHistoryPacket::encode,
                ClearTriggerHistoryPacket::decode,
                ClearTriggerHistoryPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                TeleportPetsToLocationPacket.class,
                TeleportPetsToLocationPacket::encode,
                TeleportPetsToLocationPacket::decode,
                TeleportPetsToLocationPacket::handle
        );
    }
}

