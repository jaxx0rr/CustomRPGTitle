package net.jaxx0rr.jxcustomrpgtitles;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;


public class MyBlockMenu extends AbstractContainerMenu {
    public final MyCustomBlockEntity blockEntity;
    public final String syncedText;

    // Constructor used on the server side (writes to the buffer)
    public MyBlockMenu(int id, Inventory inv, MyCustomBlockEntity blockEntity) {
        super(ModMenus.MY_BLOCK_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.syncedText = blockEntity.getText(); // get the latest server-side text
    }

    // Constructor used on the client side (reads from the buffer)
    public MyBlockMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        super(ModMenus.MY_BLOCK_MENU.get(), id);
        BlockPos pos = buf.readBlockPos();
        this.syncedText = buf.readUtf(); // read text from buffer

        BlockEntity be = inv.player.level().getBlockEntity(pos);
        if (be instanceof MyCustomBlockEntity myBe) {
            this.blockEntity = myBe;
        } else {
            this.blockEntity = null; // fallback to avoid crash
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    public MyCustomBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}
