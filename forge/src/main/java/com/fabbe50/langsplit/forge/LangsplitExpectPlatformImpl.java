package com.fabbe50.langsplit.forge;

import com.fabbe50.langsplit.LangsplitExpectPlatform;
import com.google.common.collect.Lists;
import dev.architectury.platform.Platform;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;

public class LangsplitExpectPlatformImpl {
    /**
     * {@link LangsplitExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static void registerConfig() {
//        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
        ModConfig.register();
    }

    /**
     * {@link LangsplitExpectPlatform#getLanguage()}
     */
    public static String getLanguage() {
        return ModConfig.language;
    }

    /**
     * {@link LangsplitExpectPlatform#getInLine()}
     */
    public static boolean getInLine() {
        return ModConfig.inline;
    }

    /**
     * {@link LangsplitExpectPlatform#getTranslationBrackets()}
     */
    public static boolean getTranslationBrackets() {
        return ModConfig.translationBrackets;
    }

    //@Config(name = "langsplit")
    public static class ModConfig /*implements ConfigData*/ {
        public static File configFile;

        static String language;
        static boolean inline;
        static boolean debugger = true;
        static boolean translationBrackets;

        public static ConfigBuilder init() {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setTitle(Component.translatable("text.langsplit.title"))
                    .setSavingRunnable(() -> {
                        try {
                            ModConfig.save(ModConfig.configFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ModConfig.load(ModConfig.configFile);
                    });

            return registerConfig(builder);
        }

        public static void register() {
            configFile = new File(Platform.getConfigFolder().toFile(), "langsplit.properties");
            load(configFile);
        }

        public static ConfigBuilder registerConfig(ConfigBuilder builder) {
            ConfigCategory general = builder.getOrCreateCategory(Component.translatable("text.langsplit.category.general"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            SortedSet<LanguageInfo> languages = Minecraft.getInstance().getLanguageManager().getLanguages();
            List<String> langCodes = new ArrayList<>();
            for (LanguageInfo lang : languages) {
                langCodes.add(lang.getCode());
            }

            general.addEntry(entryBuilder.startDropdownMenu(Component.translatable("text.langsplit.option.language"), DropdownMenuBuilder.TopCellElementBuilder.of(language, (s) -> {
                LanguageInfo info = Minecraft.getInstance().getLanguageManager().getLanguage(s);
                if (info != null) {
                    return info.getCode();
                } else {
                    return "en_us";
                }
            })).setDefaultValue("en_us").setSelections(Lists.newArrayList(langCodes)).setSaveConsumer(s -> language = s).build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("text.langsplit.option.inline"), inline).setDefaultValue(false).setSaveConsumer(s -> inline = s).build());
            general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("text.langsplit.option.translationBrackets"), translationBrackets).setDefaultValue(true).setSaveConsumer(s -> translationBrackets = s).build());
            return builder;
        }

        public static void load(File file) {
            try {
                if (!file.exists() || !file.canRead()) {
                    save(file);
                }
                FileInputStream fis = new FileInputStream(file);
                Properties properties = new Properties();
                properties.load(fis);
                fis.close();

                language = ((String) properties.computeIfAbsent("language", a -> "en_us"));
                inline = ((String) properties.computeIfAbsent("inline", a -> "false")).equalsIgnoreCase("true");
                translationBrackets = ((String) properties.computeIfAbsent("translationBrackets", a -> "true")).equalsIgnoreCase("true");
                save(file);
            } catch (IOException e) {
                e.printStackTrace();
                language = "en_us";
                inline = false;
                translationBrackets = true;
                try {
                    save(file);
                } catch (IOException e1) {
                    e.printStackTrace();
                }
            }
        }

        public static void save(File file) throws IOException {
            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(("language=" + language).getBytes());
            fos.write("\n".getBytes());
            fos.write(("inline=" + inline).getBytes());
            fos.write("\n".getBytes());
            fos.write(("translationBrackets=" + translationBrackets).getBytes());
            fos.close();
        }
    }
}
