package com.fabbe50.langsplit.common.mixin;

import com.fabbe50.langsplit.common.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiGraphics.class)
public abstract class MixinGuiComponent {
    @Shadow @Final private PoseStack pose;

    @Shadow @Final private MultiBufferSource.BufferSource bufferSource;

    @Inject(at = @At("HEAD"), method = "drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V", cancellable = true)
    private void injectDrawCenteredString(Font font, Component component, int x, int y, int color, CallbackInfo ci) {
        if (Langsplit.isLanguageLoaded() && component.getContents() instanceof TranslatableContents) {
            Component[] components = LangUtils.translate(component);
            pose.pushPose();
            try {
                if (components.length == 2 && !components[0].getString().equals(components[1].getString())) {
                    if (ModConfig.translationBrackets) {
                        components[1] = LangUtils.encase(components[1]);
                    }
                    if (ModConfig.inline) {
                        TextRenderHelper.drawSingleLine(pose, bufferSource, font, components, component.getStyle(), x, y, color, true);
                    } else {
                        FormattedCharSequence originalText = components[0].getVisualOrderText();
                        FormattedCharSequence translatedText = components[1].getVisualOrderText();
                        TextRenderHelper.GuiPositions positions = new TextRenderHelper.GuiPositions(x, y).getCenteredTwoLinesOnButton(pose, component, font.width(originalText), font.width(translatedText));
                        TextRenderHelper.drawTwoLines(pose, bufferSource, font, component, components, positions.getOriginalX(), positions.getOriginalY(), positions.getTranslationX(), positions.getTranslationY(), color, true);
                    }
                } else {
                    FormattedCharSequence formattedCharSequence = component.getVisualOrderText();
                    font.drawInBatch(formattedCharSequence, (float)(x - font.width(formattedCharSequence) / 2), (float)y, color, true, this.pose.last().pose(), this.bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                }
            } catch (NullPointerException ignored) {}
            pose.popPose();
        } else {
            FormattedCharSequence formattedCharSequence = component.getVisualOrderText();
            font.drawInBatch(formattedCharSequence, (float)(x - font.width(formattedCharSequence) / 2), (float)y, color, true, this.pose.last().pose(), this.bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        }
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I", cancellable = true)
    private void injectDrawString(Font font, Component component, int x, int y, int color, boolean shadow, CallbackInfoReturnable<Integer> cir) {
        if (Langsplit.isLanguageLoaded() && component.getContents() instanceof TranslatableContents) {
            Component[] components = LangUtils.translate(component);
            pose.pushPose();
            try {
                if (components.length == 2 && !components[0].getString().equals(components[1].getString())) {
                    if (ModConfig.translationBrackets) {
                        components[1] = LangUtils.encase(components[1]);
                    }
                    if (ModConfig.inline) {
                        TextRenderHelper.drawSingleLine(pose, bufferSource, font, components, component.getStyle(), x, y, color, shadow);
                    } else {
                        FormattedCharSequence originalText = components[0].getVisualOrderText();
                        FormattedCharSequence translatedText = components[1].getVisualOrderText();
                        TextRenderHelper.GuiPositions positions = new TextRenderHelper.GuiPositions(x, y).getTwoLinesOnButton(pose, component, font.width(originalText), font.width(translatedText));
                        TextRenderHelper.drawTwoLines(pose, bufferSource, font, component, components, positions.getOriginalX(), positions.getOriginalY(), positions.getTranslationX(), positions.getTranslationY(), color, shadow);
                    }
                }
            } catch (NullPointerException ignored) {}
            pose.popPose();
        } else {
            font.drawInBatch(component.getVisualOrderText(), x, y, color, shadow, this.pose.last().pose(), this.bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        }
        cir.cancel();
    }
}
