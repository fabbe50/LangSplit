package com.fabbe50.langsplit.fabric;

import com.fabbe50.langsplit.LangsplitExpectPlatform;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class LangsplitExpectPlatformImpl {
    /**
     * This is our actual method to {@link LangsplitExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static void registerConfig() {
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
    }

    public static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    /**
     * {@link LangsplitExpectPlatform#getLanguage()}
     */
    public static String getLanguage() {
        return getConfig().language;
    }

    /**
     * {@link LangsplitExpectPlatform#getInLine()}
     */
    public static boolean getInLine() {
        return false;
    }

    /**
     * {@link LangsplitExpectPlatform#getDebugger()}
     */
    public static boolean getDebugger() {
        return getConfig().debugger;
    }

    /**
     * {@link LangsplitExpectPlatform#getTranslationBrackets()}
     */
    public static boolean getTranslationBrackets() {
        return getConfig().translationBrackets;
    }

    @Config(name = "langsplit")
    public static class ModConfig implements ConfigData {
        String language = "en_us";
        boolean inline = false;
        boolean debugger = false;
        boolean translationBrackets = true;
    }
}
