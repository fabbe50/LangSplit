package com.fabbe50.langsplit.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractWidget.class)
public class MixinAbstractWidgetGetMessage {
    @Inject(at = @At("RETURN"), method = "getMessage()Lnet/minecraft/network/chat/Component;", cancellable = true)
    public void getMessage(CallbackInfoReturnable<Component> info) {
        Component result = info.getReturnValue();
        if (result != null) {
            modify(result);
            info.setReturnValue(result);
        }
    }

    private void modify(Component component) {
        if (component instanceof TranslatableContents) {
            ((MixinTranslatableComponentAccessor)component).setKey("WIDGET"+((TranslatableContents)component).getKey());
        }
        for (Component component1 : component.getSiblings()) {
            modify(component1);
        }
    }
}
