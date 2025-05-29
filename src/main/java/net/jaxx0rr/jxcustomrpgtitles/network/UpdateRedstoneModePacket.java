package net.jaxx0rr.jxcustomrpgtitles.network;

import net.jaxx0rr.jxcustomrpgtitles.MyCustomBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateRedstoneModePacket {
    private final BlockPos pos;
    private final boolean useRedstone;

    public UpdateRedstoneModePacket(BlockPos pos, boolean useRedstone) {
        this.pos = pos;
        this.useRedstone = useRedstone;
    }

    public static void encode(UpdateRedstoneModePacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeBoolean(packet.useRedstone);
    }

    public static UpdateRedstoneModePacket decode(FriendlyByteBuf buf) {
        return new UpdateRedstoneModePacket(buf.readBlockPos(), buf.readBoolean());
    }

    public static void handle(UpdateRedstoneModePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Level level = player.level();
                if (level.getBlockEntity(packet.pos) instanceof MyCustomBlockEntity be) {
                    be.setUsingRedstone(packet.useRedstone);
                    be.sendUpdate();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
