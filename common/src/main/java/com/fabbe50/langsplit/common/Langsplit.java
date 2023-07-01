package com.fabbe50.langsplit.common;

import com.google.common.collect.Lists;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Langsplit {
    public static final String MOD_ID = "langsplit";
    private static String loadedLanguage = "";
    private static ClientLanguage clientLanguage = null;
    public static final String divider = "  -  ";
    private static LanguageManager manager;
    private static boolean firstRun = true;

    public static void register() {
        ModConfig.register();
        handleTooltipEvent();
    }

    public static void setupLanguage(String secondary) {
        System.out.println("Attempting initialization of secondary language: " + secondary);
        List<String> list = Lists.newArrayList(secondary);
        LanguageInfo secondaryInfo = manager.getLanguage(secondary);
        if (secondaryInfo != null) {
            clientLanguage = ClientLanguage.loadFrom(Minecraft.getInstance().getResourceManager(), list, secondaryInfo.bidirectional());
            loadedLanguage = ModConfig.language;
            System.out.println("Secondary language initialized. Loaded " + loadedLanguage + " (" + clientLanguage.getOrDefault("language.name") + ")");
        } else {
            System.out.println("Error initializing secondary language: " + secondary);
        }
    }

    private static String secondary = null;
    private static String oldSecondary = null;
    public static boolean isLanguageLoaded() {
        try {
            manager = Minecraft.getInstance().getLanguageManager();
            if (firstRun) {
                for (String lang : manager.getLanguages().keySet()) {
                    System.out.println(lang);
                }
                firstRun = false;
            }

            String primary = manager.getSelected();
            if (!getLoadedLanguage().equals(ModConfig.language) && ModConfig.language != null) {
                secondary = ModConfig.language;
                if (manager.getLanguages().containsKey(secondary) || !Objects.equals(secondary, oldSecondary)) {
                    oldSecondary = secondary;
                }
            }

            if (secondary != null) {
                if (!primary.equals(secondary) && getLoadedLanguage() != null && !getLoadedLanguage().equals(secondary)) {
                    setupLanguage(secondary);
                    return true;
                } else
                    return !primary.equals(secondary) && getLoadedLanguage() != null;
            }
        } catch (NullPointerException ignored) {}
        return false;
    }

    public static void handleTooltipEvent() {
        ClientTooltipEvent.ITEM.register((stack, lines, flag) -> {
            if (!isLanguageLoaded())
                return;

            List<Component> newTooltip = new ArrayList<>();
            for (Component component : lines) {
                Component[] translationPair = LangUtils.translate(component);
                if (translationPair.length >= 2) {
                    TextColor color = component.getStyle().getColor();
                    int colorInt = color != null ? color.getValue() : 0xFFFFFF;
                    if (ModConfig.translationBrackets) {
                        translationPair[1] = LangUtils.encase(translationPair[1]);
                    }
                    if (ModConfig.blendColor) {
                        colorInt = ModConfig.getTextColor(color);
                    }
                    if (!(translationPair[1].getString().isEmpty() || translationPair[1].getString().equals("[]"))) {
                        if (ModConfig.inline) {
                            newTooltip.add(LangUtils.combine(component, translationPair[1].plainCopy().withStyle(component.getStyle().withColor(colorInt)), LangUtils.CombineType.DIVIDER));
                        } else {
                            newTooltip.add(component);
                            newTooltip.add(translationPair[1].plainCopy().withStyle(component.getStyle().withColor(colorInt)));
                        }
                    } else {
                        newTooltip.add(component);
                    }
                } else {
                    newTooltip.add(component);
                }
            }

            lines.clear();
            lines.addAll(newTooltip);
        });
    }

    public static String getLoadedLanguage() {
        return loadedLanguage;
    }

    public static ClientLanguage getClientLanguage() {
        return clientLanguage;
    }
}
