package com.fabbe50.langsplit.common;

import dev.architectury.platform.Platform;
import net.minecraft.network.chat.TextColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ModConfig {
    public static File configFile;

    public static String language;
    public static boolean inline;
    public static boolean translationBrackets;

    public static boolean blendColor;
    public static float blendingRatio;
    public static int textColor;

    public static void register() {
        configFile = new File(Platform.getConfigFolder().toFile(), "langsplit.properties");
        load(configFile);
    }

    public static void load(File file) {
        try {
            textColor = 0xffffff;
            if (!file.exists() || !file.canRead()) {
                save(file);
            }
            FileInputStream fis = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(fis);
            fis.close();

            language = ((String) properties.computeIfAbsent("language", a -> "en_us"));
            inline = ((String) properties.computeIfAbsent("inline", a -> "false")).equalsIgnoreCase("true");
            translationBrackets = ((String) properties.computeIfAbsent("translationBrackets", a -> "true")).equalsIgnoreCase("true");
            blendColor = ((String) properties.computeIfAbsent("blendTextColor", a -> "true")).equalsIgnoreCase("true");
            blendingRatio = Float.parseFloat((String) properties.computeIfAbsent("blendingRatio", a -> "0.5f"));
            {
                int r, g, b;
                r = Integer.parseInt((String) properties.computeIfAbsent("textColorRed", a -> "255"));
                g = Integer.parseInt((String) properties.computeIfAbsent("textColorGreen", a -> "255"));
                b = Integer.parseInt((String) properties.computeIfAbsent("textColorBlue", a -> "255"));
                textColor = (r << 16) + (g << 8) + b;
            }
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
            language = "en_us";
            inline = false;
            translationBrackets = true;
            blendColor = true;
            blendingRatio = 0.5f;
            textColor = 0xffffff;
            try {
                save(file);
            } catch (IOException e1) {
                e.printStackTrace();
            }
        }
    }

    public static void save(File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        fos.write(("language=" + language).getBytes());
        fos.write("\n".getBytes());
        fos.write(("inline=" + inline).getBytes());
        fos.write("\n".getBytes());
        fos.write(("translationBrackets=" + translationBrackets).getBytes());
        fos.write("\n".getBytes());
        fos.write(("blendTextColor=" + blendColor).getBytes());
        fos.write("\n".getBytes());
        fos.write(("blendingRatio=" + blendingRatio).getBytes());
        fos.write("\n".getBytes());
        fos.write(("textColorRed=" + ((textColor >> 16) & 255)).getBytes());
        fos.write("\n".getBytes());
        fos.write(("textColorGreen=" + ((textColor >> 8) & 255)).getBytes());
        fos.write("\n".getBytes());
        fos.write(("textColorBlue=" + (textColor & 255)).getBytes());
        fos.close();
    }

    public static int getTextColor(TextColor textColor1) {
        if (textColor1 != null) {
            return getTextColor(textColor1.getValue());
        }
        return getTextColor(0xffffff);
    }

    public static int getTextColor(int originalColor) {
        if (blendColor && originalColor != 0xffffff) {
            if (blendingRatio > 1f) {
                blendingRatio = 1f;
            } else if (blendingRatio < 0f) {
                blendingRatio = 0f;
            }
            float iRatio = 1.0f - blendingRatio;

            int oR = ((originalColor & 0xff0000) >> 16);
            int oG = ((originalColor & 0xff00) >> 8);
            int oB = (originalColor & 0xff);

            int tR = ((textColor & 0xff0000) >> 16);
            int tG = ((textColor & 0xff00) >> 8);
            int tB = (textColor & 0xff);

            int newR = (int)((oR * iRatio) + (tR * blendingRatio));
            int newG = (int)((oG * iRatio) + (tG * blendingRatio));
            int newB = (int)((oB * iRatio) + (tB * blendingRatio));

            return newR << 16 | newG << 8 | newB;
        }
        return textColor;
    }
}
