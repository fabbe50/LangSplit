package com.fabbe50.langsplit.common.mixin;

import com.fabbe50.langsplit.common.LangUtils;
import com.fabbe50.langsplit.common.Langsplit;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractWidget.class)
public abstract class MixinAbstractWidget extends GuiComponent implements Widget, GuiEventListener, NarratableEntry {
    @Shadow protected int width;
    @Shadow protected int height;
    @Shadow public int x;
    @Shadow public int y;
    @Shadow protected float alpha;
    @Shadow protected abstract void renderBg(PoseStack poseStack, Minecraft minecraft, int i, int j);
    @Shadow protected abstract int getYImage(boolean bl);
    @Shadow public abstract Component getMessage();
    @Shadow public abstract boolean isHoveredOrFocused();
    @Shadow public boolean active;

    @Inject(at = @At("HEAD"), method = "renderButton", cancellable = true)
    public void renderButton(PoseStack stack, int posX, int posY, float f, CallbackInfo ci) {
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

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, AbstractWidget.WIDGETS_LOCATION);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
            int i = this.getYImage(this.isHoveredOrFocused());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            this.blit(stack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.blit(stack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            this.renderBg(stack, minecraft, posX, posY);
            int j = this.active ? 16777215 : 10526880;
            stack.pushPose();
            try {
                if (lines.size() >= 2) {
                    stack.scale(scaleWidthFactor, scaleHeightFactor, 1f);
                    int index = -1;
                    for (Component c : lines) {
                        if (c != null && c.getVisualOrderText() != null) {
                            drawCenteredString(stack, font, c, (int) (((float) this.x + (float) this.width / 2f) * (1f / scaleWidthFactor)), (int) (((float) this.y + (float) this.height / 2f) * (1f / scaleHeightFactor)) + (index * offset), j | Mth.ceil(this.alpha * 255.0F) << 24);
                            index++;
                        }
                    }
                } else {
                    stack.scale(scaleWidthFactor, 1f, 1f);
                    drawCenteredString(stack, font, buttonText.plainCopy(), (int) (((float) this.x + (float) this.width / 2f) * (1f / scaleWidthFactor)), this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
                }
            } catch (NullPointerException ignored) {}
            stack.popPose();
            ci.cancel();
        }
    }
}
