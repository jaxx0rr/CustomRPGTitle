package net.jaxx0rr.jxcustomrpgtitles;

import com.google.gson.JsonSyntaxException;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class MyCustomBlockEntity extends BlockEntity implements MenuProvider {
    private String customText = "";
    private UUID lastShownPlayerUUID;

    public MyCustomBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MY_BLOCK_ENTITY.get(), pos, state);
    }
    public String getText() {
        return customText;
    }

    public void setText(String text) {
        this.customText = text;
        setChanged();
    }

    // Save/load from NBT
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("CustomText", customText);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Command(s)");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBlockPos(this.getBlockPos());
        buf.writeUtf(this.getText()); // sync current text
        return new MyBlockMenu(id, inventory, buf); // client-side constructor will receive this
    }


    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        customText = tag.getString("CustomText");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putString("CustomText", customText);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag); // don't forget this
        customText = tag.getString("CustomText");
    }

    // This is a helper method we called earlier
    public void sendUpdate() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private int tickCounter = 0;
    public void tick() {
        if (level == null || level.isClientSide) return;
        tickCounter++;
        int threshold = 20 + level.random.nextInt(2); // 20 or 21 ticks
        if (tickCounter >= threshold) { // 20 ticks = 1 second
            tickCounter = 0;
            // Find players within 5 blocks
            List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(worldPosition).inflate(10));
            for (Player player : players) {
                // Ensure we are only sending a title to one player at a time
                if (!player.getUUID().equals(lastShownPlayerUUID)) {
                    lastShownPlayerUUID = player.getUUID();
                    //showTitleToPlayer((ServerPlayer) player, getText());
                    executeCommand((ServerPlayer) player, getText());
                }
            }
        }
    }

    private void showTitleToPlayer(ServerPlayer player, String text) {
        Component cmp;

        try {
            cmp = Component.Serializer.fromJson(text);

            // Safeguard: fallback if JSON parses to null
            if (cmp == null) {
                cmp = Component.literal(text);
            }
        } catch (JsonSyntaxException | IllegalArgumentException e) {
            // Invalid JSON, fallback to literal text
            cmp = Component.literal(text);
        }

        player.connection.send(new ClientboundSetTitleTextPacket(cmp));
        player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 40, 10)); // fadeIn, stay, fadeOut
    }

    private void executeCommand(ServerPlayer player, String commandInput) {
        if (player == null || commandInput.isBlank()) return;

        MinecraftServer server = player.getServer();
        if (server == null) return;

        // Get position of this block
        BlockPos pos = this.getBlockPos(); // 'this' is the BlockEntity

        // Split by newline or semicolon
        String[] commands = commandInput.split("[;\n]");

        for (String cmd : commands) {
            String trimmed = cmd.trim();
            if (!trimmed.isEmpty()) {
                double fx = pos.getX();
                double fy = pos.getY();
                double fz = pos.getZ();

                if (trimmed.toLowerCase().contains("summon armor_stand")) {
                    fx += 0.5;
                    fy += 3;
                    fz += 0.5;
                } else {
                    fy += 2;
                }
                String command = String.format(trimmed, (int) fx, (int) fy, (int) fz);
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
            }
        }
    }


    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return ClientboundBlockEntityDataPacket.create(this, be -> tag);
    }

}
