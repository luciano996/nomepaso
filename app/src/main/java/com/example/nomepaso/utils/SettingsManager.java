package com.example.nomepaso.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/* loaded from: classes9.dex */
public class SettingsManager {
    private static final String KEY_THEME = "pref_theme";
    private static final String PREFS_NAME = "nomepaso_settings";
    public static final int THEME_DARK = 2;
    public static final int THEME_LIGHT = 1;
    public static final int THEME_SYSTEM = 0;
    private final SharedPreferences prefs;

    public SettingsManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public void setTheme(int theme) {
        this.prefs.edit().putInt(KEY_THEME, theme).apply();
        applyTheme(theme);
    }

    public int getTheme() {
        return this.prefs.getInt(KEY_THEME, 0);
    }

    public static void applyTheme(int theme) {
        switch (theme) {
            case 1:
                AppCompatDelegate.setDefaultNightMode(1);
                return;
            case 2:
                AppCompatDelegate.setDefaultNightMode(2);
                return;
            default:
                AppCompatDelegate.setDefaultNightMode(-1);
                return;
        }
    }
}
