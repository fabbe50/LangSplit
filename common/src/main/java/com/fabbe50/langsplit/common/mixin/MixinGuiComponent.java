package com.fabbe50.langsplit.common.mixin;

import com.fabbe50.langsplit.common.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiComponent.class)
public class MixinGuiComponent {
    @Inject(at = @At("HEAD"), method = "drawCenteredString(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V", cancellable = true)
    private static void injectDrawCenteredString(PoseStack poseStack, Font font, Component component, int x, int y, int color, CallbackInfo ci) {
        if (Langsplit.isLanguageLoaded() && component.getContents() instanceof TranslatableContents) {
            Component[] components = LangUtils.translate(component);
            poseStack.pushPose();
            try {
                if (components.length == 2 && !components[0].getString().equals(components[1].getString())) {
                    if (ModConfig.translationBrackets) {
                        components[1] = LangUtils.encase(components[1]);
                    }
                    if (ModConfig.inline) {
                        TextRenderHelper.drawSingleLine(poseStack, font, components, component.getStyle(), x, y, color);
                    } else {
                        FormattedCharSequence originalText = components[0].getVisualOrderText();
                        FormattedCharSequence translatedText = components[1].getVisualOrderText();
                        TextRenderHelper.GuiPositions positions = new TextRenderHelper.GuiPositions(x, y).getCenteredTwoLinesOnButton(poseStack, component, font.width(originalText), font.width(translatedText));
                        TextRenderHelper.drawTwoLines(poseStack, font, component, components, positions.getOriginalX(), positions.getOriginalY(), positions.getTranslationX(), positions.getTranslationY(), color);
                    }
                } else {
                    FormattedCharSequence formattedCharSequence = component.getVisualOrderText();
                    font.drawShadow(poseStack, formattedCharSequence, (float)(x - font.width(formattedCharSequence) / 2), (float)y, color);
                }
            } catch (NullPointerException ignored) {}
            poseStack.popPose();
        } else {
            FormattedCharSequence formattedCharSequence = component.getVisualOrderText();
            font.drawShadow(poseStack, formattedCharSequence, (float)(x - font.width(formattedCharSequence) / 2), (float)y, color);
        }
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "drawString(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V", cancellable = true)
    private static void injectDrawString(PoseStack poseStack, Font font, Component component, int x, int y, int color, CallbackInfo ci) {
        if (Langsplit.isLanguageLoaded() && component.getContents() instanceof TranslatableContents) {
            Component[] components = LangUtils.translate(component);
            poseStack.pushPose();
            try {
                if (components.length == 2 && !components[0].getString().equals(components[1].getString())) {
                    if (ModConfig.translationBrackets) {
                        components[1] = LangUtils.encase(components[1]);
                    }
                    if (ModConfig.inline) {
                        TextRenderHelper.drawSingleLine(poseStack, font, components, component.getStyle(), x, y, color);
                    } else {
                        FormattedCharSequence originalText = components[0].getVisualOrderText();
                        FormattedCharSequence translatedText = components[1].getVisualOrderText();
                        TextRenderHelper.GuiPositions positions = new TextRenderHelper.GuiPositions(x, y).getTwoLinesOnButton(poseStack, component, font.width(originalText), font.width(translatedText));
                        TextRenderHelper.drawTwoLines(poseStack, font, component, components, positions.getOriginalX(), positions.getOriginalY(), positions.getTranslationX(), positions.getTranslationY(), color);
                    }
                }
            } catch (NullPointerException ignored) {}
            poseStack.popPose();
        } else {
            font.drawShadow(poseStack, component.getVisualOrderText(), x, y, color);
        }
        ci.cancel();
    }
}
