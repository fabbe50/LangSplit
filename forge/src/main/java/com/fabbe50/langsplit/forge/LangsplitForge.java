package com.fabbe50.langsplit.forge;

import com.fabbe50.langsplit.Langsplit;
import dev.architectury.platform.forge.EventBuses;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

@Mod(Langsplit.MOD_ID)
public class LangsplitForge {
    public LangsplitForge() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> LangsplitForge::initClient);
    }

    private static void initClient() {
        LangsplitExpectPlatformImpl.registerConfig();
        EventBuses.registerModEventBus(Langsplit.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Langsplit.init();
//        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> AutoConfig.getConfigScreen(LangsplitExpectPlatformImpl.ModConfig.class, screen).get()));
    }
}
