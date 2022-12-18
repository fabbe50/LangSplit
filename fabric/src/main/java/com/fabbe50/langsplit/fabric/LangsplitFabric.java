package com.fabbe50.langsplit.fabric;

import com.fabbe50.langsplit.Langsplit;
import net.fabricmc.api.ClientModInitializer;

public class LangsplitFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LangsplitExpectPlatformImpl.registerConfig();
        Langsplit.init();
    }
}
