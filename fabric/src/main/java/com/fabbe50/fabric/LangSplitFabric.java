package com.fabbe50.fabric;

import com.fabbe50.LangSplit;
import com.fabbe50.LangSplitExpectPlatform;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.fabricmc.api.ModInitializer;

public class LangSplitFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LangSplitExpectPlatformImpl.registerConfig();
        LangSplit.init();
    }
}
