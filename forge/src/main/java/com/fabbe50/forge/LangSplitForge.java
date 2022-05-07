package com.fabbe50.forge;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.platform.forge.EventBuses;
import com.fabbe50.LangSplit;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LangSplit.MOD_ID)
public class LangSplitForge {
    public LangSplitForge() {
        LangSplitExpectPlatformImpl.registerConfig();
        EventBuses.registerModEventBus(LangSplit.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        LangSplit.init();
    }
}
