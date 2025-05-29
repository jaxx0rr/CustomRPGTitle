package net.jaxx0rr.jxcustomrpgtitles.util;

import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerSlotRegistry {
    private static final List<UUID> playerOrder = new ArrayList<>();
    private static final int MAX_SLOTS = 26; // chest slots: 0–26

    public static int getSlotForPlayer(ServerPlayer player) {
        UUID uuid = player.getUUID();

        if (!playerOrder.contains(uuid)) {
            if (playerOrder.size() <= MAX_SLOTS) {
                playerOrder.add(uuid);
            } else {
                // Fallback if too many players: assign based on hash
                return Math.abs(uuid.hashCode() % (MAX_SLOTS + 1));
            }
        }

        return playerOrder.indexOf(uuid);
    }

    public static void reset() {
        playerOrder.clear();
    }
}
