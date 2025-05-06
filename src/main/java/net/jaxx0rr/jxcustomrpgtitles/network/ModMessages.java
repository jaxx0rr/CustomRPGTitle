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
    }
}

