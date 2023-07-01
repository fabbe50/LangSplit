package com.fabbe50.langsplit.common.mixin;

import com.fabbe50.langsplit.common.LangUtils;
import com.fabbe50.langsplit.common.Langsplit;
import com.fabbe50.langsplit.common.ModConfig;
import com.fabbe50.langsplit.common.TextRenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Font.class)
public abstract class MixinFont {
    private PoseStack poseStack = null;

    @Shadow public abstract int width(FormattedCharSequence string);

    @Shadow protected abstract int drawInternal(FormattedCharSequence formattedCharSequence, float f, float g, int i, boolean bl, Matrix4f matrix4f, MultiBufferSource multiBufferSource, Font.DisplayMode displayMode, int j, int k);

    @Inject(at = @At("HEAD"), method = "drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I", cancellable = true)
    private void injectDrawInBatch(Component component, float x, float y, int color, boolean bl, Matrix4f matrix4f, MultiBufferSource multiBufferSource, Font.DisplayMode displayMode, int j, int k, CallbackInfoReturnable<Integer> cir) {
        if (Langsplit.isLanguageLoaded() && component.getContents() instanceof TranslatableContents) {
            if (poseStack == null) {
                poseStack = new PoseStack();
            }
            Component[] components = LangUtils.translate(component);
            poseStack.pushPose();
            try {
                if (components.length == 2 && !components[0].getString().equals(components[1].getString())) {
                    if (ModConfig.translationBrackets) {
                        components[1] = LangUtils.encase(components[1]);
                    }
                    if (ModConfig.inline) {
                        Style style = component.getStyle();
                        if (ModConfig.blendColor) {
                            color = ModConfig.getTextColor(color);
                            TextColor textColor = style.getColor();
                            if (textColor != null) {
                                style = style.withColor(ModConfig.getTextColor(textColor.getValue()));
                                components[1] = components[1].plainCopy().withStyle(style.withColor(style.getColor()));
                            }
                        }
                        Component splitComponent = Component.empty().append(components[0].copy().withStyle(style)).append(Langsplit.divider).append(components[1].copy().withStyle(style.withColor(color)));
                        drawInternal(splitComponent.getVisualOrderText(), x, y, color, bl, matrix4f, multiBufferSource, displayMode, j, k);
                    } else {
                        FormattedCharSequence originalText = components[0].getVisualOrderText();
                        FormattedCharSequence translatedText = components[1].getVisualOrderText();
                        TextRenderHelper.GuiPositions positions = new TextRenderHelper.GuiPositions(x, y).getTwoLinesWithinMaxHeight(poseStack, component, 0.6f);
                        drawInternal(originalText, positions.getOriginalX(), positions.getOriginalY(), color, bl, matrix4f, multiBufferSource, displayMode, j, k);
                        if (ModConfig.blendColor) {
                            color = ModConfig.getTextColor(color);
                            TextColor textColor = component.getStyle().getColor();
                            if (textColor != null) {
                                Style style = component.getStyle().withColor(ModConfig.getTextColor(textColor.getValue()));
                                components[1] = components[1].plainCopy().withStyle(component.getStyle().withColor(style.getColor()));
                            }
                        }
                        drawInternal(translatedText, positions.getTranslationX(), positions.getTranslationY(), color, bl, poseStack.last().pose(), multiBufferSource, displayMode, j, k);
                    }
                }
            } catch (NullPointerException ignored) {}
            poseStack.popPose();
        } else {
            cir.setReturnValue(drawInternal(component.getVisualOrderText(), x, y, color, bl, poseStack.last().pose(), multiBufferSource, displayMode, j, k));
        }
        cir.cancel();
    }
}
