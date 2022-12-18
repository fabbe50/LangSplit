package com.fabbe50.langsplit.mixin;

import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TranslatableContents.class)
public interface MixinTranslatableComponentAccessor {
    @Final
    @Mutable
    @Accessor("key")
    void setKey(String key);
}
