package com.fabbe50.langsplit.common;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

public class ClothScreen {
    public static Screen getConfigScreen(Screen parent) {
        var builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("text.langsplit.title"));

        var entryBuilder = builder.entryBuilder();
        var general = builder.getOrCreateCategory(Component.translatable("text.langsplit.category.general"));
        general.addEntry(entryBuilder.startDropdownMenu(Component.translatable("text.langsplit.option.language"), DropdownMenuBuilder.TopCellElementBuilder.of(ModConfig.language, (s) -> {
            LanguageInfo info = Minecraft.getInstance().getLanguageManager().getLanguage(s);
            if (info != null) {
                return info.getCode();
            } else {
                return "en_us";
            }
        })).setDefaultValue("en_us").setSelections(Lists.newArrayList(getLanguagesFromGame())).setSaveConsumer(s -> ModConfig.language = s).build());
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("text.langsplit.option.inline"), ModConfig.inline).setDefaultValue(false).setSaveConsumer(s -> ModConfig.inline = s).build());
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("text.langsplit.option.translationbrackets"), ModConfig.translationBrackets).setDefaultValue(true).setSaveConsumer(s -> ModConfig.translationBrackets = s).build());
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("text.langsplit.option.blendcolor"), ModConfig.blendColor)
                .setDefaultValue(true)
                .setSaveConsumer(s -> ModConfig.blendColor = s)
                .setTooltip(
                        Component.translatable("text.langsplit.option.blendcolor.desc"),
                        Component.translatable("text.langsplit.option.blendcolor.desc2"),
                        Component.translatable("text.langsplit.option.blendcolor.desc3")
                ).build());
        general.addEntry(entryBuilder.startIntSlider(Component.translatable("text.langsplit.option.blendingratio"), (int)(ModConfig.blendingRatio * 100), 0, 100)
                .setDefaultValue(50)
                .setSaveConsumer(s -> ModConfig.blendingRatio = (s / 100f))
                .setTextGetter((var) -> Component.translatable("text.langsplit.option.blendingratio.value", var))
                .setTooltip(
                        Component.translatable("text.langsplit.option.blendingratio.desc"),
                        Component.translatable("text.langsplit.option.blendingratio.desc2")
                ).build());
        general.addEntry(entryBuilder.startColorField(Component.translatable("text.langsplit.option.color"), ModConfig.textColor).setDefaultValue(0xffffff).setSaveConsumer(s -> ModConfig.textColor = s).build());

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
        SortedSet<LanguageInfo> languages = Minecraft.getInstance().getLanguageManager().getLanguages();
        List<String> langCodes = new ArrayList<>();
        for (LanguageInfo lang : languages) {
            langCodes.add(lang.getCode());
        }
        return langCodes;
    }
}
