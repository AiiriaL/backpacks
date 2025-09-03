package net.aiirial.backpacks;

import net.aiirial.backpacks.client.ClientInit;
import net.neoforged.fml.common.Mod;

@Mod(Backpacks.MOD_ID)
public class Backpacks {

    public static final String MOD_ID = "backpacks";

    public Backpacks() {
        // Client-seitige Registrierung wird automatisch auf dem Client ausgeführt
        ClientInit.register();
    }
}
