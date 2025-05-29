package net.jaxx0rr.jxcustomrpgtitles.network;

import net.jaxx0rr.jxcustomrpgtitles.MyCustomBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateSingleUsePacket {
    private final BlockPos pos;
    private final boolean singleUse;

    public UpdateSingleUsePacket(BlockPos pos, boolean singleUse) {
        this.pos = pos;
        this.singleUse = singleUse;
    }

    public static void encode(UpdateSingleUsePacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeBoolean(packet.singleUse);
    }

    public static UpdateSingleUsePacket decode(FriendlyByteBuf buf) {
        return new UpdateSingleUsePacket(buf.readBlockPos(), buf.readBoolean());
    }

    public static void handle(UpdateSingleUsePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Level level = player.level();
                if (level.getBlockEntity(packet.pos) instanceof MyCustomBlockEntity be) {
                    be.setSingleUse(packet.singleUse);
                    be.sendUpdate();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
