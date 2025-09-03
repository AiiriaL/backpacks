package net.aiirial.backpacks.registry;

import net.aiirial.backpacks.Backpacks;
import net.aiirial.backpacks.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.aiirial.backpacks.item.BackpackItem;

public final class ModItems {

    public static Item RARE_BACKPACK;
    public static Item EPIC_BACKPACK;
    public static Item ULTIMATE_BACKPACK;

    static {
        NeoForge.EVENT_BUS.addListener(ModItems::onRegister);
        NeoForge.EVENT_BUS.addListener(ModItems::addToCreativeTab);
    }

    private ModItems() {}

    public static void onRegister(RegisterEvent event) {
        event.register(Registries.ITEM, helper -> {

            RARE_BACKPACK = new RareBackpackItem(new Item.Properties().stacksTo(1));
            EPIC_BACKPACK = new EpicBackpackItem(new Item.Properties().stacksTo(1));
            ULTIMATE_BACKPACK = new UltimateBackpackItem(new Item.Properties().stacksTo(1));

            helper.register(new ResourceLocation(Backpacks.MOD_ID, "rare_backpack"), RARE_BACKPACK);
            helper.register(new ResourceLocation(Backpacks.MOD_ID, "epic_backpack"), EPIC_BACKPACK);
            helper.register(new ResourceLocation(Backpacks.MOD_ID, "ultimate_backpack"), ULTIMATE_BACKPACK);
        });
    }

    private static void addToCreativeTab(BuildCreativeModeTabContentsEvent e) {
        if (e.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            e.accept(RARE_BACKPACK);
            e.accept(EPIC_BACKPACK);
            e.accept(ULTIMATE_BACKPACK);
        }
    }
}
