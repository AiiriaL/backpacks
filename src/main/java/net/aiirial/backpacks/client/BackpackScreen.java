package net.aiirial.backpacks.client;

import net.aiirial.backpacks.menu.BackpackMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {

    private static final ResourceLocation RARE_TEX  = new ResourceLocation("backpacks:textures/gui/rare_gui.png");
    private static final ResourceLocation EPIC_TEX  = new ResourceLocation("backpacks:textures/gui/epic_gui.png");
    private static final ResourceLocation ULT_TEX   = new ResourceLocation("backpacks:textures/gui/ultimate_gui.png");

    private final Inventory playerInventory;

    public BackpackScreen(BackpackMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.playerInventory = inv;

        // Dynamische Berechnung der GUI-Größe
        if (menuIsUltimate()) {
            this.imageWidth = 256;
            this.imageHeight = 440;
        } else {
            int rows = menuRows();
            int cols = menuCols();
            this.imageWidth = 14 + cols * 18;
            this.imageHeight = 17 + rows * 18 + 96;
        }

        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    private boolean menuIsUltimate() {
        return this.menu != null && this.menu.slots.size() > 66;
    }

    private int menuRows() {
        return this.menu != null ? (this.menu.slots.size() + menuCols() - 1) / menuCols() : 0;
    }

    private int menuCols() {
        return 11;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation tex = menuIsUltimate() ? ULT_TEX : (menuRows() <= 6 ? RARE_TEX : EPIC_TEX);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        g.blit(tex, x, y, 0f, 0f, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        g.drawString(this.font, this.playerInventory.getDisplayName(), this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }
}
