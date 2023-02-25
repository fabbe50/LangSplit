package com.fabbe50.langsplit.forge;

import com.fabbe50.langsplit.common.ClothScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

public class LangsplitCloth {
    public static void register() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ClothScreen.getConfigScreen(parent)));
    }
}
