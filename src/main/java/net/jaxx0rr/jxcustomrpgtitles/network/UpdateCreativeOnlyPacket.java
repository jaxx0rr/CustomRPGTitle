package net.jaxx0rr.jxcustomrpgtitles.network;

import net.jaxx0rr.jxcustomrpgtitles.MyCustomBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateCreativeOnlyPacket {
    private final BlockPos pos;
    private final boolean creativeOnly;

    public UpdateCreativeOnlyPacket(BlockPos pos, boolean creativeOnly) {
        this.pos = pos;
        this.creativeOnly = creativeOnly;
    }

    public static void encode(UpdateCreativeOnlyPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeBoolean(packet.creativeOnly);
    }

    public static UpdateCreativeOnlyPacket decode(FriendlyByteBuf buf) {
        return new UpdateCreativeOnlyPacket(buf.readBlockPos(), buf.readBoolean());
    }

    public static void handle(UpdateCreativeOnlyPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Level level = player.level();
                if (level.getBlockEntity(packet.pos) instanceof MyCustomBlockEntity be) {
                    be.setCreativeOnly(packet.creativeOnly);
                    be.sendUpdate();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
