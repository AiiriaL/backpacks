package net.aiirial.backpacks.client;

import net.aiirial.backpacks.registry.ModMenus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class ClientInit {

    private ClientInit() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ClientInit::onRegisterScreens);
    }

    public static void onRegisterScreens(RegisterMenuScreensEvent e) {
        e.register(ModMenus.BACKPACK_MENU, BackpackScreen::new);
    }
}
