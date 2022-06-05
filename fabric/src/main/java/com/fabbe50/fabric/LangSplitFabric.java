package com.fabbe50.fabric;

import com.fabbe50.LangSplit;
import net.fabricmc.api.ClientModInitializer;

public class LangSplitFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LangSplitExpectPlatformImpl.registerConfig();
        LangSplit.init();
    }
}
