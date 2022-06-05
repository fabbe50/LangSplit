package com.fabbe50.forge;

import dev.architectury.platform.forge.EventBuses;
import com.fabbe50.LangSplit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

@Mod(LangSplit.MOD_ID)
public class LangSplitForge {
    public LangSplitForge() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> LangSplitForge::initClient);
    }

    private static void initClient() {
        LangSplitExpectPlatformImpl.registerConfig();
        EventBuses.registerModEventBus(LangSplit.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        LangSplit.init();
    }
}
