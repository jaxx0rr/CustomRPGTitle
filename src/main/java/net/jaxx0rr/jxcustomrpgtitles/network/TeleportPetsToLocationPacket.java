package net.jaxx0rr.jxcustomrpgtitles.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

public class TeleportPetsToLocationPacket {
    private final int x, y, z;
    private final String dimension;

    public TeleportPetsToLocationPacket(int x, int y, int z, String dimension) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }

    public static void encode(TeleportPetsToLocationPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.x);
        buf.writeInt(msg.y);
        buf.writeInt(msg.z);
        buf.writeUtf(msg.dimension);
    }

    public static TeleportPetsToLocationPacket decode(FriendlyByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        String dimension = buf.readUtf();
        return new TeleportPetsToLocationPacket(x, y, z, dimension);
    }

    public static void handle(TeleportPetsToLocationPacket msg, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) return;

            ServerLevel originLevel = (ServerLevel) player.level();
            ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(msg.dimension));
            ServerLevel targetLevel = player.getServer().getLevel(dimensionKey);

            if (targetLevel == null) {
                player.sendSystemMessage(Component.literal("Invalid dimension: " + msg.dimension));
                return;
            }

            // 📦 Store nearby pets before teleporting
            List<Entity> petsToTeleport = originLevel.getEntities(player, player.getBoundingBox().inflate(10), e ->
                    e instanceof TamableAnimal pet &&
                            pet.isTame() &&
                            !pet.isOrderedToSit() &&
                            pet.isOwnedBy(player)
            );

            BlockPos newPos = new BlockPos(msg.x, msg.y, msg.z);

            // 🐾 Now teleport pets to the new location
            for (Entity pet : petsToTeleport) {
                pet.teleportTo(
                        targetLevel,
                        msg.x + 0.5,
                        msg.y,
                        msg.z + 0.5,
                        EnumSet.noneOf(RelativeMovement.class),
                        pet.getYRot(),
                        pet.getXRot()
                );
            }
        });
        context.get().setPacketHandled(true);
    }
}
