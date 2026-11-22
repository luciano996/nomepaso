package com.example.nomepaso;

import android.app.Application;
import com.example.nomepaso.utils.SettingsManager;

/* loaded from: classes5.dex */
public class NoMePasoApp extends Application {
    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        SettingsManager settingsManager = new SettingsManager(this);
        SettingsManager.applyTheme(settingsManager.getTheme());
    }
}
