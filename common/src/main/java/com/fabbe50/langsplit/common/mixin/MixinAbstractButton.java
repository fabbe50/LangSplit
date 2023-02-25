package com.fabbe50.langsplit.common.mixin;

import com.fabbe50.langsplit.common.LangUtils;
import com.fabbe50.langsplit.common.Langsplit;
import com.fabbe50.langsplit.common.ModConfig;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractButton.class)
public abstract class MixinAbstractButton extends AbstractWidget {
    public MixinAbstractButton(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @Override
    public @NotNull Component getMessage() {
        if (Langsplit.isLanguageLoaded() && !ModConfig.inline) {
            Component component = super.getMessage();

            StringBuilder original = new StringBuilder();
            StringBuilder translation = new StringBuilder();
            String[] langArray = component.getString().split(": ");
            for (int i = 0; i < langArray.length; i++) {
                String[] temp = langArray[i].split(Langsplit.divider);
                if (temp.length == 2) {
                    original.append(temp[0]);
                    translation.append(temp[1]);
                } else if (temp.length == 1) {
                    if (LangUtils.isInteger(temp[0]) || temp[0].endsWith("%")) {
                        original.append(temp[0]);
                        translation.append(temp[0]);
                    } else {
                        original.append(temp[0]);
                    }
                }
                if (i == 0 && i != langArray.length - 1) {
                    original.append(": ");
                    translation.append(": ");
                }
            }
            return Component.literal(original + (translation.isEmpty() ? "" : Langsplit.divider + translation)).setStyle(component.getStyle());
        } else {
            return super.getMessage();
        }
    }
}