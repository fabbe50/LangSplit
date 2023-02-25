package com.fabbe50.langsplit.common;

import com.google.common.collect.Lists;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.MissingFormatArgumentException;

public class LangUtils {
    public static List<Component> translateComponentBulk(List<Component> lines) {
        List<Component> newLines = Lists.newArrayList();
        for (Component component : lines) {
            newLines.addAll(translateComponent(component));
        }
        return newLines;
    }

    public static List<Component> translateComponent(Component component) {
        Language language = Language.getInstance();
        List<Component> newLines = Lists.newArrayList();
        ComponentContents contents = component.getContents();
        if (contents instanceof TranslatableContents old) {
            contents = new TranslatableContents(old.getKey().replace("WIDGET", "").replace("TOOLTIP", ""));
            List<Object> args = new ArrayList<>();
            for (Object o : Arrays.stream(old.getArgs()).toList()) {
                if (o instanceof Component c) {
                    ComponentContents contents1 = c.getContents();
                    if (contents1 instanceof TranslatableContents tsc) {
                        if (!tsc.getKey().isEmpty()) {
                            args.add(Langsplit.getClientLanguage().getOrDefault(tsc.getKey()));
                            args.addAll(Arrays.asList(tsc.getArgs()));
                        }
                    } else if (contents1 instanceof LiteralContents tc) {
                        if (!tc.text().isEmpty()) {
                            args.add(tc.text());
                        }
                    } else {
                        args.add(contents1.toString());
                    }
                }
            }
            StringBuilder extras = new StringBuilder();
            if (!component.getSiblings().isEmpty()) {
                for (Component c : component.getSiblings()) {
                    if (c.getContents() instanceof TranslatableContents tsc) {
                        extras.append(Langsplit.getClientLanguage().getOrDefault(tsc.getKey()));
                    } else {
                        extras.append(c.getString());
                    }
                }
            }
            try {
                String original = language.getOrDefault(old.getKey());
                String translation = String.format(Langsplit.getClientLanguage().getOrDefault(((TranslatableContents) contents).getKey()), args.toArray()) + extras;
                if (!original.equals(translation) && !translation.equals(((TranslatableContents) contents).getKey())) {
                    if (ModConfig.inline) {
                        if (ModConfig.translationBrackets) {
                            newLines.add(MutableComponent.create(new LiteralContents(original + " [" + translation + "]")).setStyle(component.getStyle()));
                        } else {
                            newLines.add(MutableComponent.create(new LiteralContents(original + " " + translation)).setStyle(component.getStyle()));
                        }
                    } else {
                        newLines.add(MutableComponent.create(old).setStyle(component.getStyle()));
                        if (ModConfig.translationBrackets) {
                            newLines.add(MutableComponent.create(new LiteralContents("[" + translation + "]")).setStyle(component.getStyle().withColor(ModConfig.getTextColor(component.getStyle().getColor()))));
                        } else {
                            newLines.add(MutableComponent.create(new LiteralContents(translation)).setStyle(component.getStyle().withColor(ModConfig.getTextColor(component.getStyle().getColor()))));
                        }
                    }
                } else {
                    newLines.add(MutableComponent.create(old).setStyle(component.getStyle()));
                }
            } catch (MissingFormatArgumentException ignored) {}
        } else if (contents instanceof LiteralContents) {
            if (!component.getSiblings().isEmpty()) {
                for (Component component1 : component.getSiblings()) {
                    if (component1 instanceof TranslatableContents old) {
                        List<String> args = new ArrayList<>();
                        for (Object o : Arrays.stream(old.getArgs()).toList()) {
                            if (o instanceof TranslatableContents tsc) {
                                args.add(tsc.getKey());
                            } else if (o instanceof LiteralContents tc) {
                                args.add(tc.text());
                            }
                        }
                        TranslatableContents contents1 = new TranslatableContents(old.getKey().replace("WIDGET", ""));
                        String original = language.getOrDefault(old.getKey());
                        String translation = String.format(Langsplit.getClientLanguage().getOrDefault(contents1.getKey()), args);
                        if (!original.equals(translation) && !translation.equals(contents1.getKey())) {
                            if (ModConfig.inline) {
                                if (ModConfig.translationBrackets) {
                                    newLines.add(MutableComponent.create(new LiteralContents(original + " [" + translation + "]")).setStyle(component.getStyle()));
                                } else {
                                    newLines.add(MutableComponent.create(new LiteralContents(original + " " + translation)).setStyle(component.getStyle()));
                                }
                            } else {
                                newLines.add(MutableComponent.create(old).setStyle(component.getStyle()));
                                if (ModConfig.translationBrackets) {
                                    newLines.add(MutableComponent.create(new LiteralContents("[" + translation + "]")).setStyle(component.getStyle().withColor(ModConfig.getTextColor(component.getStyle().getColor()))));
                                } else {
                                    newLines.add(MutableComponent.create(new LiteralContents(translation)).setStyle(component.getStyle().withColor(ModConfig.getTextColor(component.getStyle().getColor()))));
                                }
                            }
                        } else {
                            newLines.add(MutableComponent.create(old).setStyle(component.getStyle()));
                        }
                    }
                }
            }
        } else {
            newLines.add(component);
        }
        return newLines;
    }

    public static List<Component> splitLines(Component component) {
        List<Component> newLines = Lists.newArrayList();
        String[] ss = component.getString().split(Langsplit.divider);
        for (int i = 0; i < ss.length; i++) {
            if (i % 2 == 0) {
                newLines.add(Component.literal(ss[i]).setStyle(component.getStyle()));
            } else {
                newLines.add(Component.literal(ss[i]).setStyle(component.getStyle().withColor(ModConfig.getTextColor(component.getStyle().getColor()))));
            }
        }
        return newLines;
    }

    public static Object[] fixArguments(Object[] args) {
        List<Object> oldArgs = Lists.newArrayList(args);
        List<Object> newArgs = new ArrayList<>();
        for (Object oldArg : oldArgs) {
            newArgs.addAll(flattenArguments(oldArg));
        }
        return newArgs.toArray();
    }

    public static List<Object> flattenArguments(Object arg) {
        List<Object> newArgs = new ArrayList<>();
        if (arg instanceof MutableComponent component1) {
            ComponentContents componentContents = component1.getContents();
            if (componentContents instanceof TranslatableContents translatableContents1) {
                if (translatableContents1.getArgs().length == 0) {
                    newArgs.add(Langsplit.getClientLanguage().getOrDefault(translatableContents1.getKey()));
                }
                for (Object o : translatableContents1.getArgs()) {
                    newArgs.addAll(flattenArguments(o));
                }
            }
        } else {
            newArgs.add(arg);
        }
        return newArgs;
    }

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
