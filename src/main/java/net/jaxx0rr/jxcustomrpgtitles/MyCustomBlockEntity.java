package net.jaxx0rr.jxcustomrpgtitles;

import io.netty.buffer.Unpooled;
import net.jaxx0rr.jxcustomrpgtitles.util.PlayerSlotRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MyCustomBlockEntity extends BlockEntity implements MenuProvider {
    private String customText = "";
    private UUID lastShownPlayerUUID;
    private boolean useRedstone = false;
    private boolean wasPowered = false;
    private boolean singleUse = false;
    private final Set<UUID> triggeredOnce = new HashSet<>();
    private boolean creativeOnly = false;


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

    public boolean isUsingRedstone() {
        return this.useRedstone;
    }

    public boolean isSingleUse() {
        return this.singleUse;
    }

    public boolean isCreativeOnly() {
        return creativeOnly;
    }

    public void setUsingRedstone(boolean v) {
        this.useRedstone = v;
        setChanged();
    }

    public void setSingleUse(boolean v) {
        this.singleUse = v;
        setChanged();
    }

    public void setCreativeOnly(boolean value) {
        this.creativeOnly = value;
        setChanged();
    }

    public void clearTriggerHistory() {
        triggeredOnce.clear();
        setChanged();
    }

    // Save/load from NBT
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("CustomText", customText);
        tag.putBoolean("UseRedstone", useRedstone);
        tag.putBoolean("SingleUse", singleUse);
        ListTag uuidList = new ListTag();
        for (UUID uuid : triggeredOnce) {
            uuidList.add(NbtUtils.createUUID(uuid));
        }
        tag.put("TriggeredOnce", uuidList);
        tag.putBoolean("CreativeOnly", creativeOnly);
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
        useRedstone = tag.getBoolean("UseRedstone");
        singleUse = tag.getBoolean("SingleUse");
        if (tag.contains("TriggeredOnce", Tag.TAG_LIST)) {
            triggeredOnce.clear();
            ListTag uuidList = tag.getList("TriggeredOnce", Tag.TAG_INT_ARRAY); // UUIDs are stored as int arrays
            for (Tag uuidTag : uuidList) {
                triggeredOnce.add(NbtUtils.loadUUID((net.minecraft.nbt.IntArrayTag) uuidTag));
            }
        }
        creativeOnly = tag.getBoolean("CreativeOnly");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putString("CustomText", customText);
        tag.putBoolean("UseRedstone", useRedstone);
        tag.putBoolean("SingleUse", singleUse);
        tag.putBoolean("CreativeOnly", creativeOnly);

        ListTag uuidList = new ListTag();
        for (UUID uuid : triggeredOnce) {
            uuidList.add(NbtUtils.createUUID(uuid));
        }
        tag.put("TriggeredOnce", uuidList);

        return tag;
    }


    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);

        customText = tag.getString("CustomText");
        useRedstone = tag.getBoolean("UseRedstone");
        singleUse = tag.getBoolean("SingleUse");
        creativeOnly = tag.getBoolean("CreativeOnly");

        triggeredOnce.clear();
        if (tag.contains("TriggeredOnce", Tag.TAG_LIST)) {
            ListTag uuidList = tag.getList("TriggeredOnce", Tag.TAG_INT_ARRAY);
            for (Tag uuidTag : uuidList) {
                triggeredOnce.add(NbtUtils.loadUUID((IntArrayTag) uuidTag));
            }
        }
    }


    // This is a helper method we called earlier
    public void sendUpdate() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }


    private int tickCounter = 0;
    private final Set<UUID> recentlyTriggeredPlayers = new HashSet<>();

    public void tick() {
        if (level == null || level.isClientSide) return;
        if (JxCustomRPGTitles.GLOBAL_BLOCK_DISABLE) return;

        boolean isPowered = level.hasNeighborSignal(worldPosition);

        if (useRedstone) {
            if (isPowered && !wasPowered) {
                // Redstone just turned ON — remember that
                wasPowered = true;

            } else if (!isPowered && wasPowered) {
                // Redstone just turned OFF — trigger here
                wasPowered = false;

                List<ServerPlayer> players = level.getEntitiesOfClass(ServerPlayer.class, new AABB(worldPosition).inflate(10));
                if (!players.isEmpty()) {
                    ServerPlayer triggeringPlayer = players.get(0);
                    double minDistance = triggeringPlayer.distanceToSqr(Vec3.atCenterOf(worldPosition));

                    for (ServerPlayer player : players) {
                        double dist = player.distanceToSqr(Vec3.atCenterOf(worldPosition));
                        if (dist < minDistance) {
                            triggeringPlayer = player;
                            minDistance = dist;
                        }
                    }

                    if (!singleUse || !triggeredOnce.contains(triggeringPlayer.getUUID())) {
                        executeCommand(triggeringPlayer, getText());
                        if (singleUse) triggeredOnce.add(triggeringPlayer.getUUID());
                    }
                }
            }

            return;
        }


        // --- Proximity mode ---
        tickCounter++;
        int threshold = 20 + level.random.nextInt(2); // ~1 second
        if (tickCounter >= threshold) {
            tickCounter = 0;

            List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(worldPosition).inflate(10));
            Set<UUID> currentlyNearby = players.stream().map(Player::getUUID).collect(Collectors.toSet());

            for (Player player : players) {
                UUID uuid = player.getUUID();
                if (!recentlyTriggeredPlayers.contains(uuid)) {
                    if (singleUse && triggeredOnce.contains(player.getUUID())) continue;
                    executeCommand((ServerPlayer) player, getText());
                    recentlyTriggeredPlayers.add(uuid);
                    if (singleUse) triggeredOnce.add(uuid);
                }
            }

            recentlyTriggeredPlayers.retainAll(currentlyNearby);
        }

    }


    private void executeCommand(ServerPlayer player, String commandInput) {
        if (player == null || commandInput.isBlank()) return;

        MinecraftServer server = player.getServer();
        if (server == null) return;

        BlockPos pos = this.getBlockPos(); // 'this' is the BlockEntity
        int rotation = 0; // Default rotation (facing south)

        if (this.getBlockState().hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction dir = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            rotation = switch (dir) {
                case NORTH -> 180;
                case EAST -> -90;
                case WEST -> 90;
                default -> 0; // SOUTH
            };
        }

        String[] commands = commandInput.split("[;\n]");

        for (String cmd : commands) {
            String trimmed = cmd.trim();
            if (trimmed.isEmpty()) continue;

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

            String playerName = player.getName().getString();
            String replaced = trimmed.replace("@p", playerName);

            int slot = PlayerSlotRegistry.getSlotForPlayer(player);
            replaced = replaced.replace("~s", String.valueOf(slot));

            // Replace r[x-y] with a random number between x and y
            Pattern randomPattern = Pattern.compile("r\\[(\\d+)-(\\d+)]");
            Matcher matcher = randomPattern.matcher(replaced);
            StringBuffer replacedBuffer = new StringBuffer();
            while (matcher.find()) {
                int min = Integer.parseInt(matcher.group(1));
                int max = Integer.parseInt(matcher.group(2));
                int randomValue = ThreadLocalRandom.current().nextInt(min, max + 1);
                matcher.appendReplacement(replacedBuffer, String.valueOf(randomValue));
            }
            matcher.appendTail(replacedBuffer);

            String formatted = replacedBuffer.toString();
            if (formatted.contains("%")) {
                try {
                    formatted = String.format(formatted, (int) fx, (int) fy, (int) fz, rotation);
                } catch (IllegalFormatException e) {
                    System.err.println("[JxCustomRPGTitles] Format error in command: " + formatted);
                    e.printStackTrace();
                    continue; // skip malformed command
                }
            }

            ServerLevel targetLevel = null;
            double targetX = 0, targetY = 0, targetZ = 0;
            boolean isTeleportCommand = false;


            if (formatted.contains("tp ")) {
                String[] parts = formatted.split(" ");

                try {
                    targetZ = Double.parseDouble(parts[parts.length - 1]);
                    targetY = Double.parseDouble(parts[parts.length - 2]);
                    targetX = Double.parseDouble(parts[parts.length - 3]);

                    // Try to extract dimension with flexible pattern: supports "execute in ..." or "execute as @p in ..."
                    Pattern dimPattern = Pattern.compile("execute(?: as \\S+)? in ([^\\s]+) run tp");
                    Matcher matcher2 = dimPattern.matcher(formatted);

                    if (matcher2.find()) {
                        String rawDimension = matcher2.group(1); // e.g., minecraft:overworld
                        targetLevel = server.getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(rawDimension)));
                        System.out.println("[JxCustomRPGTitles] Dimension-based teleport to " + rawDimension);
                    } else {
                        targetLevel = player.serverLevel(); // fallback if not found
                        System.out.println("[JxCustomRPGTitles] Local teleport");
                    }

                    isTeleportCommand = true;
                    System.out.println("[JxCustomRPGTitles] Target coords: " + targetX + " " + targetY + " " + targetZ);
                } catch (Exception e) {
                    System.out.println("[JxCustomRPGTitles] Failed to parse teleport command: " + formatted);
                    e.printStackTrace();
                }
            }



            if (isTeleportCommand && targetLevel != null) {

                ServerLevel originLevel = player.serverLevel();
                BlockPos newPos = BlockPos.containing(targetX, targetY, targetZ);

                System.out.println("[DEBUG] Player: " + player.getName().getString());
                System.out.println("[DEBUG] From Dimension: " + originLevel.dimension().location());
                System.out.println("[DEBUG] To Dimension: " + targetLevel.dimension().location());
                System.out.println("[DEBUG] Target Coords: " + targetX + ", " + targetY + ", " + targetZ);

                // 1️⃣ Scan for pets BEFORE teleporting player
                List<Entity> petsToTeleport = originLevel.getEntities(player, player.getBoundingBox().inflate(10), e ->
                        e instanceof TamableAnimal pet &&
                                pet.isTame() &&
                                !pet.isOrderedToSit() &&
                                pet.isOwnedBy(player)
                );
                System.out.println("[DEBUG] Found " + petsToTeleport.size() + " pets to teleport.");

                // 2️⃣ Teleport player
                try {
                    player.teleportTo(
                            targetLevel,
                            targetX + 0.5,
                            targetY,
                            targetZ + 0.5,
                            player.getYRot(),
                            player.getXRot()
                    );
                    System.out.println("[DEBUG] Player teleport issued.");
                } catch (Exception ex) {
                    System.out.println("[ERROR] Player teleport failed: " + ex.getMessage());
                    ex.printStackTrace();
                }

                // 3️⃣ Teleport pets AFTER player
                for (Entity pet : petsToTeleport) {
                    try {
                        pet.teleportTo(
                                targetLevel,
                                targetX + 0.5,
                                targetY,
                                targetZ + 0.5,
                                EnumSet.noneOf(RelativeMovement.class),
                                pet.getYRot(),
                                pet.getXRot()
                        );
                        System.out.println("[DEBUG] Pet teleported: " + pet.getEncodeId());
                    } catch (Exception ex) {
                        System.out.println("[ERROR] Pet teleport failed: " + pet.getEncodeId());
                        ex.printStackTrace();
                    }
                }


            } else {
                // Run other commands normally
                CommandSourceStack source = player.createCommandSourceStack()
                        .withSuppressedOutput()
                        .withPermission(2);
                server.getCommands().performPrefixedCommand(source, formatted);
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
