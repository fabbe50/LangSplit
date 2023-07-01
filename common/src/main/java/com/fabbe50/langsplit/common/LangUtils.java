package com.fabbe50.langsplit.common;

import com.google.common.collect.Lists;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.*;

public class LangUtils {
    public static Component[] translate(Component component) {
        Component[] newLines = new Component[2];
        if (Langsplit.getClientLanguage() != null) {
            ComponentContents contents = component.getContents();
            if (contents instanceof TranslatableContents translatable) {
                String originalString = ClientLanguage.getInstance().getOrDefault(translatable.getKey());
                String translatedString = Langsplit.getClientLanguage().getOrDefault(translatable.getKey());
                Object[][] args = translateArg(translatable.getArgs());
                Component[] combinedSiblings = decodeSiblings(component);
                Component originalComponent = combine(Component.translatable(originalString, args[0]), combinedSiblings[0], CombineType.DIRECT).copy().setStyle(component.getStyle());
                Component translatedComponent = combine(Component.translatable(translatedString, args[1]), combinedSiblings[1], CombineType.DIRECT).copy().setStyle(component.getStyle());
                newLines[0] = originalComponent;
                newLines[1] = translatedComponent;
            } else {
                Component[] siblings = translateSiblings(component);
                Component originalComponent = Component.translatable(siblings[0].getString()).setStyle(component.getStyle());
                Component translatedComponent = Component.translatable(siblings[1].getString()).setStyle(component.getStyle());
                newLines[0] = originalComponent;
                newLines[1] = translatedComponent;
            }
        } else {
            newLines[0] = component;
            newLines[1] = component;
        }
        return newLines;
    }

    public static Object[][] translateArg(Object[] args) {
        Object[][] sidedArgs = new Object[2][args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Component c) {
                Component[] temp = translate(c);
                sidedArgs[0][i] = temp[0];
                if (temp.length == 2) {
                    sidedArgs[1][i] = temp[1];
                }
            } else {
                sidedArgs[0][i] = args[i];
                sidedArgs[1][i] = args[i];
            }
        }
        return sidedArgs;
    }

    public static Component[] translateSiblings(Component component) {
        Component original = Component.literal("");
        Component translation = Component.literal("");
        if (component.getContents() instanceof LiteralContents literal) {
            original = combine(original, Component.literal(literal.text()), CombineType.DIRECT);
            translation = combine(translation, Component.literal(literal.text()), CombineType.DIRECT);
        }
        for (Component sibling : component.getSiblings()) {
            Component[] temp = translate(sibling);
            original = combine(original, temp[0], CombineType.DIRECT);
            if (temp.length == 2) {
                translation = combine(translation, temp[1], CombineType.DIRECT);
            }
        }
        return new Component[] {original, translation};
    }

    public static Component[] decodeSiblings(Component component) {
        Component original = Component.literal("");
        Component translation = Component.literal("");
        for (Component sibling : component.getSiblings()) {
            ComponentContents contents = sibling.getContents();
            if (contents instanceof TranslatableContents) {
                Component[] temp = translate(sibling);
                original = combine(original, temp[0], CombineType.DIRECT);
                if (temp.length == 2) {
                    translation = combine(translation, temp[1], CombineType.DIRECT);
                }
            } else if (contents instanceof LiteralContents literal) {
                original = combine(original, Component.literal(literal.text()), CombineType.DIRECT);
                translation = combine(translation, Component.literal(literal.text()), CombineType.DIRECT);
            }
        }
        return new Component[] {original, translation};
    }

    public static Component combine(Component component1, Component component2, CombineType type) {
        String divider = type == CombineType.SPACE ? " " : type == CombineType.DIVIDER ? Langsplit.divider : "";
        ComponentContents contents1 = component1.getContents();
        ComponentContents contents2 = component2.getContents();
        if (contents1 instanceof TranslatableContents translatable1) {
            if (contents2 instanceof TranslatableContents translatable2) {
                return Component.translatable("%1$s" + divider + "%2$s",
                        Component.translatable(component1.getString(), translatable1.getArgs()).withStyle(component1.getStyle()),
                        Component.translatable(component2.getString(), translatable2.getArgs()).withStyle(component2.getStyle()));
            }
            return Component.translatable("%1$s" + divider + "%2$s",
                    Component.translatable(component1.getString(), translatable1.getArgs()).withStyle(component1.getStyle()),
                    Component.translatable(component2.getString(), translatable1.getArgs()).withStyle(component2.getStyle()));
        } else {
            if (contents2 instanceof TranslatableContents translatable2) {
                return Component.translatable("%1$s" + divider + "%2$s",
                        Component.translatable(component1.getString()).withStyle(component1.getStyle()),
                        Component.translatable(component2.getString(), translatable2.getArgs()).withStyle(component2.getStyle()));
            }
            return Component.translatable("%1$s" + divider + "%2$s",
                    Component.translatable(component1.getString()).withStyle(component1.getStyle()),
                    Component.translatable(component2.getString()).withStyle(component2.getStyle()));
        }
    }

    public static Component encase(Component component) {
        ComponentContents contents = component.getContents();
        if (contents instanceof TranslatableContents translatable) {
            return Component.translatable("[" + component.getString() + "]", translatable.getArgs());
        }
        return Component.literal("[" + component.getString() + "]");
    }

    public static String getTranslationKey(Component component) {
        ComponentContents contents = component.getContents();
        if (contents instanceof TranslatableContents translatable) {
            return translatable.getKey();
        }
        return component.getString();
    }

    public enum CombineType {
        DIRECT,
        SPACE,
        DIVIDER
    }
}
