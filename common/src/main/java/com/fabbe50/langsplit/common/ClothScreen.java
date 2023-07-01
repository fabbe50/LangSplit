package com.fabbe50.langsplit.common;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.util.*;

public class ClothScreen {
    public static Screen getConfigScreen(Screen parent) {
        var builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("text.langsplit.title"));

        var entryBuilder = builder.entryBuilder();
        var general = builder.getOrCreateCategory(Component.translatable("text.langsplit.category.general"));
        general.addEntry(entryBuilder.startDropdownMenu(Component.translatable("text.langsplit.option.language"),
            DropdownMenuBuilder.TopCellElementBuilder.of(ModConfig.language, (s) -> {
                Map<String, String> mappedLanguages = getLocalizedLanguagesFromGame();
                if (mappedLanguages.containsKey(s)) {
                    return mappedLanguages.get(s);
                }
                return "en_us";
            }, (t) -> {
                Map<String, String> mappedLanguages = getLocalizedLanguagesFromGame();
                if (mappedLanguages.containsKey(t)) {
                    return Component.literal(t);
                } else if (mappedLanguages.containsValue(t)) {
                    LanguageInfo info = Minecraft.getInstance().getLanguageManager().getLanguage(t);
                    if (info != null) {
                        return info.toComponent().copy().append(" ").append(Component.translatable("text.langsplit.language." + t));
                    }
                }
                return Component.literal("English (US) American English");
            }),
            DropdownMenuBuilder.CellCreatorBuilder.of((t) -> {
                if (getLocalizedLanguagesFromGame().containsKey(t)) {
                    return Component.literal(t);
                }
                return Component.literal("English (US) American English");
            })).setDefaultValue("en_us").setSelections(Lists.newArrayList(getLocalizedLanguagesFromGame().keySet())).setSaveConsumer(s -> ModConfig.language = s).setSuggestionMode(false).build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("text.langsplit.option.inline"), ModConfig.inline).setDefaultValue(false).setSaveConsumer(s -> ModConfig.inline = s).build());
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("text.langsplit.option.translationbrackets"), ModConfig.translationBrackets).setDefaultValue(false).setSaveConsumer(s -> ModConfig.translationBrackets = s).build());
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("text.langsplit.option.blendcolor"), ModConfig.blendColor)
                .setDefaultValue(true)
                .setSaveConsumer(s -> ModConfig.blendColor = s)
                .setTooltip(
                        Component.translatable("text.langsplit.option.blendcolor.desc"),
                        Component.translatable("text.langsplit.option.blendcolor.desc2"),
                        Component.translatable("text.langsplit.option.blendcolor.desc3")
                ).build());
        general.addEntry(entryBuilder.startIntSlider(Component.translatable("text.langsplit.option.blendingratio"), (int)(ModConfig.blendingRatio * 100), 0, 100)
                .setDefaultValue(35)
                .setSaveConsumer(s -> ModConfig.blendingRatio = (s / 100f))
                .setTextGetter((var) -> Component.translatable("text.langsplit.option.blendingratio.value", var))
                .setTooltip(
                        Component.translatable("text.langsplit.option.blendingratio.desc"),
                        Component.translatable("text.langsplit.option.blendingratio.desc2")
                ).build());
        general.addEntry(entryBuilder.startColorField(Component.translatable("text.langsplit.option.color"), ModConfig.textColor).setDefaultValue(0x77ff77).setSaveConsumer(s -> ModConfig.textColor = s).build());

        return builder.setSavingRunnable(() -> {
            try {
                ModConfig.save(ModConfig.configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ModConfig.load(ModConfig.configFile);
        }).build();
    }

    private static List<String> getLanguagesFromGame() {
        LanguageManager manager = Minecraft.getInstance().getLanguageManager();
        SortedMap<String, LanguageInfo> languages = manager.getLanguages();
        return new ArrayList<>(languages.keySet());
    }

    private static Map<String, String> getLocalizedLanguagesFromGame() {
        LanguageManager manager = Minecraft.getInstance().getLanguageManager();
        List<String> languages = getLanguagesFromGame();
        Map<String, String> formattedNames = new HashMap<>();
        for (String language : languages) {
            LanguageInfo languageInfo = manager.getLanguage(language);
            if (languageInfo != null) {
                formattedNames.put(languageInfo.toComponent().getString() + " " + ClientLanguage.getInstance().getOrDefault("text.langsplit.language." + language), language);
            }
        }
        return formattedNames;
    }
}
