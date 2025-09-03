package net.aiirial.backpacks.item;

import net.aiirial.backpacks.menu.BackpackMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Gemeinsame Basisklasse für Rare, Epic und Ultimate Backpacks.
 * Verwaltet das Inventar, NBT-Speicherung und Interaktion.
 */
public abstract class BackpackItem extends Item {
    private final int rows;
    private final int columns;
    private final boolean specialUltimateLayout; // true → 158 Slots Layout

    /**
     * Standard-Backpack (Rare/Epic)
     */
    protected BackpackItem(Properties props, int rows, int columns) {
        super(props.stacksTo(1));
        this.rows = rows;
        this.columns = columns;
        this.specialUltimateLayout = false;
    }

    /**
     * Ultimate-Backpack mit speziellem Layout
     */
    protected BackpackItem(Properties props, boolean specialUltimateLayout) {
        super(props.stacksTo(1));
        this.rows = 0;
        this.columns = 0;
        this.specialUltimateLayout = specialUltimateLayout;
    }

    public boolean isUltimateLayout() {
        return specialUltimateLayout;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getSlotCount() {
        return specialUltimateLayout ? 158 : rows * columns;
    }

    /**
     * Inventar aus NBT laden
     */
    public SimpleContainer getInventory(ItemStack stack) {
        SimpleContainer inv = new SimpleContainer(getSlotCount()) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack toInsert) {
                // Verhindert, dass Rucksäcke in andere Rucksäcke gelegt werden
                return !(toInsert.getItem() instanceof BackpackItem);
            }
        };

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("BackpackItems", 9)) { // 9 = LIST
            ListTag list = tag.getList("BackpackItems", 10); // 10 = Compound
            int max = Math.min(list.size(), inv.getContainerSize());
            for (int i = 0; i < max; i++) {
                CompoundTag itemTag = list.getCompound(i);
                inv.setItem(i, ItemStack.of(itemTag)); // NeoForge 1.21.x: of()
            }
        }

        return inv;
    }

    /**
     * Inventar in NBT speichern
     */
    public void saveInventory(ItemStack stack, Container inv) {
        ListTag list = new ListTag();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack it = inv.getItem(i);
            if (!it.isEmpty()) {
                list.add(it.save(new CompoundTag()));
            } else {
                list.add(new CompoundTag()); // Leerer Slot → leeres Tag
            }
        }

        CompoundTag tag = stack.getTag();
        if (tag == null) tag = new CompoundTag();
        tag.put("BackpackItems", list);
        stack.setTag(tag);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            final boolean ultimate = isUltimateLayout();
            final int rows = getRows();
            final int cols = getColumns();

            sp.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("item.backpacks.backpack");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
                    return new BackpackMenu(id, inv, stack, ultimate, rows, cols);
                }
            }, buf -> {
                buf.writeBoolean(ultimate);
                buf.writeVarInt(rows);
                buf.writeVarInt(cols);
                buf.writeItem(stack);
            });

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }
}
