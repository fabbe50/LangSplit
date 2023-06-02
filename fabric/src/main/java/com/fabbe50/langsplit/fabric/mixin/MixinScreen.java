package com.fabbe50.langsplit.fabric.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Screen.class)
public class MixinScreen {
    @Inject(at = @At("RETURN"), method = "getTooltipFromItem", cancellable = true)
    public void getTooltipFromItem(ItemStack stack, CallbackInfoReturnable<List<Component>> callbackInfoReturnable) {
        List<Component> result = callbackInfoReturnable.getReturnValue();
        for (Component component : result) {
            if (component != null) {
                modify(component);
            }
        }
        callbackInfoReturnable.setReturnValue(result);
    }

    private void modify(Component component) {
        if (component instanceof TranslatableContents) {
            ((MixinTranslatableComponentAccessor) component).setKey("TOOLTIP" + ((TranslatableContents) component).getKey());
        }
        for (Component component1 : component.getSiblings()) {
            modify(component1);
        }
    }
}
