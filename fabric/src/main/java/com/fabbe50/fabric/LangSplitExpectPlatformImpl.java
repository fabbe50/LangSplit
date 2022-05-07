package com.fabbe50.fabric;

import com.fabbe50.LangSplitExpectPlatform;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class LangSplitExpectPlatformImpl {
    /**
     * This is our actual method to {@link LangSplitExpectPlatform#getConfigDirectory()}.
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
     * {@link LangSplitExpectPlatform#getLanguage()}
     */
    public static String getLanguage() {
        return getConfig().language;
    }

    /**
     * {@link LangSplitExpectPlatform#getInLine()}
     */
    public static boolean getInLine() {
        return getConfig().inline;
    }

    /**
     * {@link LangSplitExpectPlatform#getDebugger()}
     */
    public static boolean getDebugger() {
        return getConfig().debugger;
    }

    @Config(name = "langsplit")
    static class ModConfig implements ConfigData {
        String language = "en_us";
        boolean inline = false;
        boolean debugger = false;
    }
}
