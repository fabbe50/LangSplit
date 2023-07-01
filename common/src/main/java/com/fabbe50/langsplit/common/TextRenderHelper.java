package com.fabbe50.langsplit.common;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;

public class TextRenderHelper {
    public static void drawSingleLine(PoseStack poseStack, MultiBufferSource multiBufferSource, Font font, Component[] components, Style style, float x, float y, int color, boolean shadow) {
        if (ModConfig.blendColor) {
            color = ModConfig.getTextColor(color);
            TextColor textColor = style.getColor();
            if (textColor != null) {
                style = style.withColor(ModConfig.getTextColor(textColor.getValue()));
                components[1] = components[1].plainCopy().withStyle(style.withColor(style.getColor()));
            }
        }
        Component splitComponent = Component.empty().append(components[0].copy().withStyle(style)).append(Langsplit.divider).append(components[1].copy().withStyle(style.withColor(color)));
//        font.drawShadow(poseStack, splitComponent, x, y, color);
        font.drawInBatch(splitComponent.getVisualOrderText(), x, y, color, shadow, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
    }

    public static void drawTwoLines(PoseStack poseStack, MultiBufferSource multiBufferSource, Font font, Component component, Component[] components, float originalX, float originalY, float translationX, float translationY, int color, boolean shadow) {
        FormattedCharSequence originalText = components[0].getVisualOrderText();
        font.drawInBatch(originalText, originalX, originalY, color, shadow, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        if (ModConfig.blendColor) {
            color = ModConfig.getTextColor(color);
            TextColor textColor = component.getStyle().getColor();
            if (textColor != null) {
                Style style = component.getStyle().withColor(ModConfig.getTextColor(textColor.getValue()));
                components[1] = components[1].plainCopy().withStyle(component.getStyle().withColor(style.getColor()));
            }
        }
        FormattedCharSequence translatedText = components[1].getVisualOrderText();
        font.drawInBatch(translatedText, translationX, translationY, color, shadow, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
    }

    private static float calculateCoord(float coord, int ctlCoord) {
        return coord + ctlCoord;
    }

    public static class GuiPositions {
        private float scaleHeightFactor;
        private final float inputX;
        private final float inputY;
        private float originalX, originalY, translationX, translationY;

        public GuiPositions(float inputX, float inputY) {
            this.inputX = inputX;
            this.inputY = inputY;
        }

        public GuiPositions getCenteredTwoLinesOnButton(PoseStack poseStack, Component component, float originalTextWidth, float translationTextWidth) {
            originalX = getPositionX(originalTextWidth);
            originalY = getPositionY(2, 0);
            translationX = getPositionX(translationTextWidth);
            translationY = getPositionY(1, 2);
            poseStack.scale(1, scaleHeightFactor, 1);
            applyOverrides(component);
            return this;
        }

        public GuiPositions getTwoLinesOnButton(PoseStack poseStack, Component component, float originalTextWidth, float translationTextWidth) {
            float maxWidth = Math.max(originalTextWidth, translationTextWidth);
            originalX = getPositionX(originalTextWidth, maxWidth);
            originalY = getPositionY(2, 0);
            translationX = getPositionX(translationTextWidth, maxWidth);
            translationY = getPositionY(1, 2);
            poseStack.scale(1, scaleHeightFactor, 1);
            applyOverrides(component);
            return this;
        }

        public GuiPositions getTwoLinesWithinMaxHeight(PoseStack poseStack, Component component, float heightScale) {
            originalX = inputX;
            originalY = getPositionY(2, 14, heightScale);
            translationX = inputX;
            translationY = getPositionY(1, 18, heightScale);
            poseStack.scale(1f, heightScale, 1f);
            applyOverrides(component);
            return this;
        }

        private float getPositionX(float width) {
            return inputX - (width / 2f);
        }

        private float getPositionX(float width, float maxWidth) {
            return (inputX - (width / 2f)) + (maxWidth / 2f);
        }

        private float getPositionY(float line, int offset) {
            float mockupButtonHeight = 9 * 2.3f;
            scaleHeightFactor = 1f / (19 / (mockupButtonHeight - 6f));
            return ((inputY + mockupButtonHeight / 2f) * (1f / scaleHeightFactor)) + (-(line) * 9) + offset;
        }

        private float getPositionY(float line, int offset, float heightScale) {
            return ((inputY + heightScale / 2f) * (1f / heightScale)) + (-(line) * 9) + offset;
        }

        private void applyOverrides(Component component) {
            for (String ctlName : ModConfig.textLocations.keySet()) {
                ModConfigObjects.ConfigCustomTextLocation ctl = ModConfig.textLocations.get(ctlName);
                String key = ctl.getKey();
                if (LangUtils.getTranslationKey(component).contains(key)) {
                    if (ctl.getAdjustOriginal()) {
                        if (ctl.getUseAsOffset()) {
                            float vx = calculateCoord(originalX, ctl.getX());
                            float vy = calculateCoord(originalY, ctl.getY());
                            originalX = vx;
                            originalY = vy;
                        } else {
                            originalX = ctl.getX();
                            originalY = ctl.getY();
                        }
                    } else {
                        if (ctl.getUseAsOffset()) {
                            float vx = calculateCoord(translationX, ctl.getX());
                            float vy = calculateCoord(translationY, ctl.getY());
                            translationX = vx;
                            translationY = vy;
                        } else {
                            translationX = ctl.getX();
                            translationY = ctl.getY();
                        }
                    }
                }
            }
        }

        public float getOriginalX() {
            return originalX;
        }

        public float getOriginalY() {
            return originalY;
        }

        public float getTranslationX() {
            return translationX;
        }

        public float getTranslationY() {
            return translationY;
        }
    }
}
