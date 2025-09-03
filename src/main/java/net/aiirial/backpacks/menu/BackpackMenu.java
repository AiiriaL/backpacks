package net.aiirial.backpacks.menu;

import net.aiirial.backpacks.item.BackpackItem;
import net.aiirial.backpacks.registry.ModMenus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Menü für alle Backpack-Größen (Rare, Epic, Ultimate).
 */
public class BackpackMenu extends AbstractContainerMenu {
    private final ItemStack backpackStack;
    private final SimpleContainer backpackInv;
    private final boolean ultimateLayout;
    private final int rows, cols;

    // Server-seitig aufgerufen
    public BackpackMenu(int id, Inventory playerInv, ItemStack stack, boolean ultimateLayout, int rows, int cols) {
        super(ModMenus.BACKPACK_MENU, id);
        this.backpackStack = stack;
        this.ultimateLayout = ultimateLayout;
        this.rows = rows;
        this.cols = cols;

        BackpackItem bp = (BackpackItem) stack.getItem();
        this.backpackInv = bp.getInventory(stack);

        addBackpackSlots();
        addPlayerSlots(playerInv);
    }

    // Client-side (receive)
    public static BackpackMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        boolean ultimate = buf.readBoolean();
        int rows = buf.readVarInt();
        int cols = buf.readVarInt();

        ItemStack stack = buf.readWithCodec(ItemStack.STREAM_CODEC).orElse(ItemStack.EMPTY);

        return new BackpackMenu(id, playerInv, stack, ultimate, rows, cols);
    }

    // Server-side (send)
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeBoolean(ultimateLayout);
        buf.writeVarInt(rows);
        buf.writeVarInt(cols);
        buf.writeWithCodec(ItemStack.STREAM_CODEC, backpackStack);
    }


    private void addBackpackSlots() {
        class NoBackpackSlot extends Slot {
            public NoBackpackSlot(Container c, int idx, int x, int y) { super(c, idx, x, y); }
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !(stack.getItem() instanceof BackpackItem);
            }
        }

        int index = 0;

        if (ultimateLayout) {
            // Top-Grid
            int topX = 8, topY = 18;
            for (int row = 0; row < 16; row++) {
                for (int col = 0; col < 8; col++) {
                    this.addSlot(new NoBackpackSlot(backpackInv, index++, topX + col * 18, topY + row * 18));
                }
            }
            // Left-Grid
            int leftX = -60, leftY = 40;
            for (int r = 0; r < 5; r++) {
                for (int c = 0; c < 3; c++) {
                    this.addSlot(new NoBackpackSlot(backpackInv, index++, leftX + c * 18, leftY + r * 18));
                }
            }
            // Right-Grid
            int rightX = 190, rightY = 40;
            for (int r = 0; r < 5; r++) {
                for (int c = 0; c < 3; c++) {
                    this.addSlot(new NoBackpackSlot(backpackInv, index++, rightX + c * 18, rightY + r * 18));
                }
            }
        } else {
            int startX = 8, startY = 18;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    this.addSlot(new NoBackpackSlot(backpackInv, index++, startX + c * 18, startY + r * 18));
                }
            }
        }
    }

    private void addPlayerSlots(Inventory playerInv) {
        int baseY = ultimateLayout ? 350 : 18 + rows * 18 + 14;

        // Spieler-Inventar
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, baseY + row * 18));
            }
        }

        // Hotbar
        int hotbarY = baseY + 58;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, hotbarY));
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide && backpackStack.getItem() instanceof BackpackItem bp) {
            bp.saveInventory(backpackStack, backpackInv);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return empty;

        ItemStack in = slot.getItem();
        ItemStack copy = in.copy();

        int backpackSize = backpackInv.getContainerSize();
        int totalSlots = this.slots.size();

        if (index < backpackSize) {
            if (!this.moveItemStackTo(in, backpackSize, totalSlots, true)) return empty;
        } else {
            if (!this.moveItemStackTo(in, 0, backpackSize, false)) return empty;
        }

        if (in.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }
}
