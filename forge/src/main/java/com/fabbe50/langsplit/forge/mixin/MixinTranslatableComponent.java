package com.fabbe50.langsplit.forge.mixin;

import com.fabbe50.langsplit.common.Langsplit;
import com.fabbe50.langsplit.common.ModConfig;
import com.google.common.collect.ImmutableList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Final
    @Shadow
    private static Pattern FORMAT_PATTERN;

    @Final
    @Shadow
    private static FormattedText TEXT_PERCENT;

    @Final
    @Shadow
    private Object[] args;

    @Shadow
    protected abstract void decomposeTemplate(String s, Consumer<FormattedText> textConsumer);

    @Shadow
    protected abstract FormattedText getArgument(int i);

    @Inject(at = @At("HEAD"), method = "decompose", cancellable = true)
    private void decompose(CallbackInfo ci) {
        if (Langsplit.isLanguageLoaded()) {
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
                        if (ModConfig.inline) {
                            builder.add(FormattedText.of(" " + Langsplit.divider));
                        } else {
                            builder.add(FormattedText.of(Langsplit.divider));
                        }
                        if (ModConfig.translationBrackets) {
                            this.decomposeTemplateWithColor("[" + alt + "]", builder::add);
                        } else {
                            this.decomposeTemplateWithColor(alt, builder::add);
                        }
                    }
                }
                this.decomposedParts = builder.build();
            } catch (TranslatableFormatException e) {
                this.decomposedParts.add(FormattedText.of(s));
            }
            ci.cancel();
        }
    }

    private boolean shouldIgnore(String key) {
        return IGNORE_LIST.contains(key);
    }

    private void decomposeTemplateWithColor(String string, Consumer<FormattedText> consumer) {
        Matcher matcher = FORMAT_PATTERN.matcher(string);

        try {
            int i = 0;

            int j;
            int l;
            for(j = 0; matcher.find(j); j = l) {
                int k = matcher.start();
                l = matcher.end();
                String string2;
                if (k > j) {
                    string2 = string.substring(j, k);
                    if (string2.indexOf(37) != -1) {
                        throw new IllegalArgumentException();
                    }

                    consumer.accept(FormattedText.of(string2, Style.EMPTY.withColor(ModConfig.getTextColor(0xFFFFFF))));
                }

                string2 = matcher.group(2);
                String string3 = string.substring(k, l);
                if ("%".equals(string2) && "%%".equals(string3)) {
                    consumer.accept(TEXT_PERCENT);
                } else {
                    if (!"s".equals(string2)) {
                        throw new IllegalArgumentException("Unsupported format: '" + string3 + "'");
                    }

                    String string4 = matcher.group(1);
                    int m = string4 != null ? Integer.parseInt(string4) - 1 : i++;
                    if (m < this.args.length) {
                        consumer.accept(this.getArgument(m));
                    }
                }
            }

            if (j < string.length()) {
                String string5 = string.substring(j);
                if (string5.indexOf(37) != -1) {
                    throw new IllegalArgumentException();
                }

                consumer.accept(FormattedText.of(string5, Style.EMPTY.withColor(ModConfig.getTextColor(0xFFFFFF))));
            }

        } catch (IllegalArgumentException var12) {
            throw new IllegalArgumentException(var12);
        }
    }
}
