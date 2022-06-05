package com.fabbe50;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;

public class LangSplit {
    public static final String MOD_ID = "langsplit";
    
    public static void init() {
        handleTooltipEvent();
    }

    public static void handleTooltipEvent() {
        ClientTooltipEvent.ITEM.register((stack, lines, flag) -> {
            LanguageInfo primary = Minecraft.getInstance().getLanguageManager().getSelected();
            LanguageInfo secondary = Minecraft.getInstance().getLanguageManager().getLanguage(LangSplitExpectPlatform.getLanguage());

            if (secondary != null && !stack.hasCustomHoverName()) {
                if (primary != secondary) {
                    List<LanguageInfo> list = Lists.newArrayList(secondary);
                    ClientLanguage clientLanguage = ClientLanguage.loadFrom(Minecraft.getInstance().getResourceManager(), list);
                    List<Component> newTooltip = Lists.newArrayList();
                    int posMul = 0;
                    for (Component component : lines) {
                        if (component instanceof TranslatableComponent) {
                            Object[] args = new Object[((TranslatableComponent) component).getArgs().length];
                            for (int i = 0; i < args.length; i++) {
                                if (((TranslatableComponent) component).getArgs()[i] instanceof TranslatableComponent) {
                                    args[i] = clientLanguage.getOrDefault(((TranslatableComponent) component).getKey());
                                } else if (((TranslatableComponent) component).getArgs()[i] instanceof TextComponent) {
                                    args[i] = ((Component) ((TranslatableComponent) component).getArgs()[i]).getString();
                                } else {
                                    args[i] = ((TranslatableComponent) component).getArgs()[i];
                                }
                            }
                            StringBuilder extras = new StringBuilder();
                            if (!component.getSiblings().isEmpty()) {
                                for (Component c : component.getSiblings()) {
                                    if (c instanceof TranslatableComponent) {
                                        extras.append(clientLanguage.getOrDefault(((TranslatableComponent) c).getKey()));
                                    } else {
                                        extras.append(c.getString());
                                    }
                                }
                            }
                            if (LangSplitExpectPlatform.getInLine()) {
                                String translation = String.format(clientLanguage.getOrDefault(((TranslatableComponent) component).getKey()), args) + extras;
                                newTooltip.add(new TextComponent(component.getString() + " [" + translation + "]").setStyle(component.getStyle()));
                            } else {
                                newTooltip.add(component);
                                String translation = String.format(clientLanguage.getOrDefault(((TranslatableComponent) component).getKey()), args) + extras;
                                newTooltip.add(new TextComponent("[" + translation + "]").setStyle(component.getStyle()));
                            }
                        } else if (component instanceof TextComponent) {
                            if (!component.getSiblings().isEmpty()) {
                                for (Component component1 : component.getSiblings()) {
                                    if (component1 instanceof TranslatableComponent) {
                                        Object[] args = new Object[((TranslatableComponent) component1).getArgs().length];
                                        for (int i = 0; i < args.length; i++) {
                                            if (((TranslatableComponent) component1).getArgs()[i] instanceof TranslatableComponent)
                                                args[i] = clientLanguage.getOrDefault(((TranslatableComponent) ((TranslatableComponent) component1).getArgs()[i]).getKey());
                                            else if (((TranslatableComponent) component1).getArgs()[i] instanceof TextComponent)
                                                args[i] = ((TextComponent) ((TranslatableComponent) component1).getArgs()[i]).getString();
                                            else
                                                args[i] = ((TranslatableComponent) component1).getArgs()[i];
                                        }
                                        if (LangSplitExpectPlatform.getInLine()) {
                                            String translation = String.format(clientLanguage.getOrDefault(((TranslatableComponent) component1).getKey()), args);
                                            newTooltip.add(new TextComponent(component.getString() + " [" + translation + "]").setStyle(component.getStyle()));
                                        } else {
                                            newTooltip.add(component);
                                            String translation = String.format(clientLanguage.getOrDefault(((TranslatableComponent) component1).getKey()), args);
                                            newTooltip.add(new TextComponent("[" + translation + "]").setStyle(component.getStyle()));
                                        }
                                    }
                                }
                            }
                        } else {
                            newTooltip.add(component);
                        }

                        if (LangSplitExpectPlatform.getDebugger()) {
                            String[] textCom = component.toString().split(",");
                            for (String s : textCom) {
                                if (!s.contains("=null") && !s.contains("=[]")) {
                                    Minecraft.getInstance().font.draw(new PoseStack(), s + ",", 10, (10) + (10 * posMul), 0xFFFFFF);
                                    posMul++;
                                }
                            }
                            posMul++;
                        }
                    }
                    lines.clear();
                    lines.addAll(newTooltip);
                }
            }
        });
        /*ClientGuiEvent.RENDER_HUD.register((matrices, tickDelta) -> {

        });*/
    }
}
