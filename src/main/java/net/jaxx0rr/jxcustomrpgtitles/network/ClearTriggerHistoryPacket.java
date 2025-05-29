package net.jaxx0rr.jxcustomrpgtitles.network;

import net.jaxx0rr.jxcustomrpgtitles.MyCustomBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClearTriggerHistoryPacket {
    private final BlockPos pos;

    public ClearTriggerHistoryPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(ClearTriggerHistoryPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
    }

    public static ClearTriggerHistoryPacket decode(FriendlyByteBuf buf) {
        return new ClearTriggerHistoryPacket(buf.readBlockPos());
    }

    public static void handle(ClearTriggerHistoryPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Level level = player.level();
                if (level.getBlockEntity(packet.pos) instanceof MyCustomBlockEntity be) {
                    be.clearTriggerHistory();
                    be.sendUpdate();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
