package com.fabbe50.langsplit.common;

public class Utilities {
    private static final Utilities INSTANCE = new Utilities();

    public static Utilities getInstance() {
        return INSTANCE;
    }

    public boolean checkIfClassExists(String className) {
        try {
            Class.forName(className, false, getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
