package com.fabbe50.langsplit;

import com.google.common.collect.Lists;
import dev.architectury.event.events.client.ClientTooltipEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.ArrayList;
import java.util.List;

public class Langsplit {
    public static final String MOD_ID = "langsplit";
    private static String loadedLanguage = "";
    private static ClientLanguage clientLanguage = null;
    public static final String divider = "  -  ";

    public static void init() {
        handleTooltipEvent();
    }

    public static void setupLanguage() {
        LanguageInfo primary = Minecraft.getInstance().getLanguageManager().getSelected();
        LanguageInfo secondary = Minecraft.getInstance().getLanguageManager().getLanguage(LangsplitExpectPlatform.getLanguage());

        if (secondary != null) {
            if (primary != secondary) {
                List<LanguageInfo> list = Lists.newArrayList(secondary);
                clientLanguage = ClientLanguage.loadFrom(Minecraft.getInstance().getResourceManager(), list);
                loadedLanguage = LangsplitExpectPlatform.getLanguage();
            }
        }
    }

    public static void handleTooltipEvent() {
        ClientTooltipEvent.ITEM.register((stack, lines, flag) -> {
            if (!loadedLanguage.equals(LangsplitExpectPlatform.getLanguage())) {
                setupLanguage();
            }

            List<Component> newTooltip = new ArrayList<>();
            for (Component component : lines) {
                boolean hasContent = false;
                ComponentContents contents = component.getContents();
                if (contents instanceof TranslatableContents translatableContents) {
                    if (translatableContents.getArgs().length != 0) {
                        String newText = getClientLanguage().getOrDefault(translatableContents.getKey());
                        Object[] newArgs = LangUtils.fixArguments(translatableContents.getArgs());
                        StringBuilder original = new StringBuilder(component.getString().substring(0, component.getString().indexOf(Langsplit.divider)));
                        boolean argumentFlag = false;
                        for (Object o : newArgs) {
                            if (original.toString().startsWith("+" + o.toString())) {
                                argumentFlag = true;
                                break;
                            }
                        }
                        String translation = String.format(newText, newArgs);
                        if (newArgs.length == 3) {
                            Object[] mergedArgs = new Object[]{newArgs[0] + " " + newArgs[1], newArgs[2]};
                            if (!argumentFlag) {
                                original.append(" ").append(newArgs[1]).append(" (").append(newArgs[2]).append(")");
                            }
                            translation = String.format(newText, mergedArgs);
                        } else if (newArgs.length == 2) {
                            if (!argumentFlag) {
                                original.append(" (").append(newArgs[1]).append(")");
                            }
                        }

//                            translation = translation.substring(0, translation.length() / 2);

                        if (LangsplitExpectPlatform.getInLine()) {
                            if (LangsplitExpectPlatform.getTranslationBrackets()) {
                                newTooltip.add(Component.literal(original.toString() + " [" + translation + "]").setStyle(component.getStyle()));
                            } else {
                                newTooltip.add(Component.literal(original.toString() + " " + translation).setStyle(component.getStyle()));
                            }
                        } else {
                            if (LangsplitExpectPlatform.getTranslationBrackets()) {
                                newTooltip.add(Component.literal(original.toString()).setStyle(component.getStyle()));
                                newTooltip.add(Component.literal("[" + translation + "]").setStyle(component.getStyle()));
                            } else {
                                newTooltip.add(Component.literal(original.toString()).setStyle(component.getStyle()));
                                newTooltip.add(Component.literal(translation).setStyle(component.getStyle()));
                            }
                        }
                        hasContent = true;
                    } else {
                        //Target "item.modifiers.mainhand" as this has no arguments.
                        String newText = getClientLanguage().getOrDefault(translatableContents.getKey());
                        System.out.println(translatableContents);
                    }
                }
                if (!hasContent) {
                    if (LangsplitExpectPlatform.getInLine()) {
                        newTooltip.add(Component.literal(component.getString().replace(Langsplit.divider, " ")).setStyle(component.getStyle()));
                    } else {
                        String[] newLines = component.getString().split(Langsplit.divider);
                        for (String newLine : newLines) {
                            newTooltip.add(Component.literal(newLine).setStyle(component.getStyle()));
                        }
                    }
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
