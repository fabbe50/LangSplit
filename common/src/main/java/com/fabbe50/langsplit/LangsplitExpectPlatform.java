package com.fabbe50.langsplit;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.platform.Platform;

import java.nio.file.Path;

public class LangsplitExpectPlatform {
    /**
     * We can use {@link Platform#getConfigFolder()} but this is just an example of {@link ExpectPlatform}.
     * <p>
     * This must be a public static method. The platform-implemented solution must be placed under a
     * platform sub-package, with its class suffixed with {@code Impl}.
     * <p>
     * Example:
     * Expect: com.fabbe50.langsplit.ExampleExpectPlatform#getConfigDirectory()
     * Actual Fabric: com.fabbe50.langsplit.fabric.ExpectPlatformImpl#getConfigDirectory()
     * Actual Forge: com.fabbe50.langsplit.forge.ExpectPlatformImpl#getConfigDirectory()
     */
    @ExpectPlatform
    public static Path getConfigDirectory() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    @ExpectPlatform
    public static String getLanguage() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean getInLine() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean getTranslationBrackets() {
        throw new AssertionError();
    }
}
