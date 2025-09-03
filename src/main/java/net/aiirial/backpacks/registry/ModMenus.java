package net.aiirial.backpacks.registry;

import net.aiirial.backpacks.Backpacks;
import net.aiirial.backpacks.menu.BackpackMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class ModMenus {

    public static MenuType<BackpackMenu> BACKPACK_MENU;

    static {
        NeoForge.EVENT_BUS.addListener(ModMenus::onRegister);
    }

    private ModMenus() {}

    public static void onRegister(RegisterEvent event) {
        event.register(Registries.MENU, helper -> {
            BACKPACK_MENU = new MenuType<>((windowId, inv, buf) -> BackpackMenu.fromNetwork(windowId, inv, buf));

            helper.register(new ResourceLocation(Backpacks.MOD_ID, "backpack_menu"), BACKPACK_MENU);
        });
    }
}
