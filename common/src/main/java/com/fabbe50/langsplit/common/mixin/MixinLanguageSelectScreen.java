package com.fabbe50.langsplit.common.mixin;

import com.fabbe50.langsplit.common.ClothScreen;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageSelectScreen.class)
public abstract class MixinLanguageSelectScreen extends OptionsSubScreen {
    public MixinLanguageSelectScreen(Screen screen, Options options, Component component) {
        super(screen, options, component);
    }

    @Inject(at = @At("HEAD"), method = "init")
    private void injectInit(CallbackInfo ci) {
        this.addRenderableWidget(Button.builder(Component.translatable("text.langsplit.title"), button -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(ClothScreen.getConfigScreen(this));
            }
        }).bounds(this.width - 160, 8, 150, 20).build());
    }
}
