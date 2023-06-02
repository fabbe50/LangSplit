package com.fabbe50.langsplit.fabric.mixin;

import com.fabbe50.langsplit.common.LangUtils;
import com.fabbe50.langsplit.common.Langsplit;
import com.fabbe50.langsplit.common.ModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractButton.class)
public abstract class MixinAbstractButton extends AbstractWidget {
    @Shadow protected abstract int getTextureY();

    @Shadow public abstract void renderString(PoseStack poseStack, Font font, int i);

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

    @Inject(at = @At("HEAD"), method = "renderWidget", cancellable = true)
    public void renderWidget(PoseStack stack, int posX, int posY, float f, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        Component buttonText = getMessage();

        if (buttonText != null && Langsplit.isLanguageLoaded()) {
            List<Component> lines = LangUtils.splitLines(buttonText);

            int offset = font.lineHeight;
            int strHeight = font.lineHeight;
            if (lines.size() >= 2) {
                strHeight = font.lineHeight * 2 + 1;
            }
            float scaleHeightFactor = strHeight > height - 6 ? 1f / (strHeight / (height - 6f)) : 1f;
            int strWidth = font.width(buttonText);
            float scaleWidthFactor = strWidth > width - 6 ? 1f / (strWidth / (width - 6f)) : 1f;

            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            blitNineSliced(stack, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            int j = this.active ? 16777215 : 10526880;
            stack.pushPose();
            try {
                if (lines.size() >= 2) {
                    stack.scale(scaleWidthFactor, scaleHeightFactor, 1f);
                    int index = -1;
                    for (Component c : lines) {
                        if (c != null && c.getVisualOrderText() != null) {
                            drawCenteredString(stack, font, c, (int) (((float) this.getX() + (float) this.width / 2f) * (1f / scaleWidthFactor)), (int) (((float) this.getY() + (float) this.height / 2f) * (1f / scaleHeightFactor)) + (index * offset), j | Mth.ceil(this.alpha * 255.0F) << 24);
                            index++;
                        }
                    }
                } else {
                    //stack.scale(scaleWidthFactor, 1f, 1f);
                    //drawCenteredString(stack, font, buttonText.plainCopy(), (int) (((float) this.x + (float) this.width / 2f) * (1f / scaleWidthFactor)), this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
                    this.renderString(stack, font, j | Mth.ceil(this.alpha * 255.0F) << 24);
                }
            } catch (NullPointerException ignored) {}
            stack.popPose();
            ci.cancel();
        }
    }
}
