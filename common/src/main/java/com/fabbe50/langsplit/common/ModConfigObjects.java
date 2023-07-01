package com.fabbe50.langsplit.common;

import java.awt.*;

public class ModConfigObjects {
    public static class ConfigCustomTextLocation extends ConfigObject {
        private final String key;
        private final int x;
        private final int y;
        private final boolean useAsOffset;
        private final boolean adjustOriginal;

        public ConfigCustomTextLocation(String configName, String key, int x, int y, boolean useAsOffset) {
            this(configName, key, x, y, useAsOffset, false);
        }

        public ConfigCustomTextLocation(String configName, String key, int x, int y, boolean useAsOffset, boolean adjustOriginal) {
            super(configName);
            this.key = key;
            this.x = x;
            this.y = y;
            this.useAsOffset = useAsOffset;
            this.adjustOriginal = adjustOriginal;
        }

        public String getKey() {
            return key;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean getUseAsOffset() {
            return useAsOffset;
        }

        public boolean getAdjustOriginal() {
            return adjustOriginal;
        }
    }

    public static class ConfigObjectColor extends ConfigObject {
        private int color;
        private final int defaultColor;

        public ConfigObjectColor(String configName, int color) {
            super(configName);
            this.color = color;
            this.defaultColor = color;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public void setColor(Color color) {
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            this.color = (r << 16) + (g << 8) + b;
        }

        public int getDefaultColor() {
            return defaultColor;
        }
    }

    public static class ConfigObjectString extends ConfigObject {
        private String value;
        private final String defaultValue;

        public ConfigObjectString(String configName, String value) {
            super(configName);
            this.value = value;
            this.defaultValue = value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String getDefaultValue() {
            return defaultValue;
        }
    }

    public static class ConfigObjectInteger extends ConfigObject {
        private int value;
        private final int defaultValue;

        public ConfigObjectInteger(String configName, int value) {
            super(configName);
            this.value = value;
            this.defaultValue = value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public int getDefaultValue() {
            return defaultValue;
        }
    }

    public static class ConfigObjectBoolean extends ConfigObject {
        private boolean value;
        private final boolean defaultValue;

        public ConfigObjectBoolean(String configName, boolean value) {
            super(configName);
            this.value = value;
            this.defaultValue = value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }

        public boolean getValue() {
            return value;
        }

        public boolean getDefaultValue() {
            return defaultValue;
        }
    }

    public static class ConfigObject {
        private final String configName;

        public ConfigObject(String configName) {
            this.configName = configName;
        }

        public String getConfigName() {
            return configName;
        }
    }
}
