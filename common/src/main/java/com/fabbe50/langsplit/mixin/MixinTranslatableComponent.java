package com.fabbe50.langsplit.mixin;

import com.fabbe50.langsplit.Langsplit;
import com.fabbe50.langsplit.LangsplitExpectPlatform;
import com.google.common.collect.ImmutableList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.chat.contents.TranslatableFormatException;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Mixin(TranslatableContents.class)
public abstract class MixinTranslatableComponent {
    private static final List<String> IGNORE_LIST = new ArrayList<>();
    static {
        IGNORE_LIST.add("translation.test.invalid");
        IGNORE_LIST.add("translation.test.invalid2");
        IGNORE_LIST.add("options.off.composed");
        IGNORE_LIST.add("options.on.composed");
        IGNORE_LIST.add("options.generic_value");
        IGNORE_LIST.add("options.pixel_value");
        IGNORE_LIST.add("options.percent_value");
        IGNORE_LIST.add("options.percent_add_value");
    }

    @Final
    @Shadow
    @Mutable
    private String key;

    @Shadow
    private Language decomposedWith;

    @Shadow
    private List<FormattedText> decomposedParts;

    @Shadow
    protected abstract void decomposeTemplate(String s, Consumer<FormattedText> textConsumer);

    @Inject(at = @At("HEAD"), method = "decompose()V", cancellable = true)
    private void decompose(CallbackInfo ci) {
        if (!Langsplit.getLoadedLanguage().equals(LangsplitExpectPlatform.getLanguage())) {
            Langsplit.setupLanguage();
        }

        boolean fromTooltip = false;
        if (this.key.startsWith("TOOLTIP")) {
            fromTooltip = true;
            this.key = this.key.replace("TOOLTIP", "");
        }
        boolean fromWidget = false;
        if (this.key.startsWith("WIDGET")) {
            fromWidget = true;
            this.key = this.key.replace("WIDGET", "");
        }

        Language language = Language.getInstance();

        this.decomposedWith = language;
        String s = language.getOrDefault(this.key);

        try {
            ImmutableList.Builder<FormattedText> builder = ImmutableList.builder();
            Objects.requireNonNull(builder);
            this.decomposeTemplate(s, builder::add);
            if (Langsplit.getClientLanguage() != null && !fromTooltip && !fromWidget) {
                String alt = language.getOrDefault(Langsplit.getClientLanguage().getOrDefault(this.key));
                if (!alt.equals(s) && !shouldIgnore(key)) {
                    if (LangsplitExpectPlatform.getInLine()) {
                        builder.add(FormattedText.of(" "));
                    } else {
                        builder.add(FormattedText.of(Langsplit.divider));
                    }
                    if (LangsplitExpectPlatform.getTranslationBrackets()) {
                        this.decomposeTemplate("[" + alt + "]", builder::add);
                    } else {
                        this.decomposeTemplate(alt, builder::add);
                    }
                }
            }
            this.decomposedParts = builder.build();
        } catch (TranslatableFormatException e) {
            this.decomposedParts.add(FormattedText.of(s));
        }
        ci.cancel();
    }

    private boolean shouldIgnore(String key) {
        if (IGNORE_LIST.contains(key)) {
            return true;
        }
        return false;
    }
}
