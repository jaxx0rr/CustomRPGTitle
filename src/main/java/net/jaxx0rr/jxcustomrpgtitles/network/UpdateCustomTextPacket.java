package net.jaxx0rr.jxcustomrpgtitles.network;

import net.jaxx0rr.jxcustomrpgtitles.MyCustomBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateCustomTextPacket {
    private final BlockPos pos;
    private final String text;

    public UpdateCustomTextPacket(BlockPos pos, String text) {
        this.pos = pos;
        this.text = text;
    }

    public static void encode(UpdateCustomTextPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeUtf(packet.text);
    }

    public static UpdateCustomTextPacket decode(FriendlyByteBuf buf) {
        return new UpdateCustomTextPacket(buf.readBlockPos(), buf.readUtf());
    }

    public static void handle(UpdateCustomTextPacket packet, Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Level level = player.level();
                if (level.getBlockEntity(packet.pos) instanceof MyCustomBlockEntity be) {
                    be.setText(packet.text);
                    be.sendUpdate();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

