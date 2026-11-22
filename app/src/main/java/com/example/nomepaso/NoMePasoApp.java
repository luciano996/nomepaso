package com.example.nomepaso;

import android.app.Application;
import com.example.nomepaso.utils.SettingsManager;

public class NoMePasoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SettingsManager settingsManager = new SettingsManager(this);
        SettingsManager.applyTheme(settingsManager.getTheme());
    }
}
