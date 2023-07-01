package com.fabbe50.langsplit.common.mixin;

import com.fabbe50.langsplit.common.LangUtils;
import com.fabbe50.langsplit.common.ModConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static net.minecraft.client.gui.GuiComponent.*;

@Mixin(AbstractWidget.class)
public class MixinAbstractWidget {
    @Inject(at = @At("HEAD"), method = "renderScrollingString(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIII)V", cancellable = true)
    private static void injectRenderScrollingString(PoseStack poseStack, Font font, Component component, int i, int j, int k, int l, int m, CallbackInfo ci) {
        Component componentToMeasure = component.copy();
        Component[] components = LangUtils.translate(component);
        if (components.length == 2) {
            if (ModConfig.inline) {
                componentToMeasure = LangUtils.combine(components[0], components[1], LangUtils.CombineType.DIVIDER);
            } else {
                componentToMeasure = font.width(components[0]) > font.width(components[1]) ? components[0] : components[1];
            }
        }
        int n = font.width(componentToMeasure);
        int var10000 = j + l;
        Objects.requireNonNull(font);
        int o = (var10000 - 9) / 2 + 1;
        int p = k - i;
        if (n > p) {
            int q = n - p;
            double d = (double) Util.getMillis() / 1000.0;
            double e = Math.max((double)q * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * d / e)) / 2.0 + 0.5;
            double g = Mth.lerp(f, 0.0, q);
            enableScissor(i, j, k, l);
            drawString(poseStack, font, component, i - (int) g, o, m);
            disableScissor();
        } else {
            drawCenteredString(poseStack, font, component, (i + k) / 2, o, m);
        }
        ci.cancel();
    }
}
