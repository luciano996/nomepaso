package com.example.nomepaso;

import android.os.Bundle;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.nomepaso.utils.SettingsManager;

public class ConfiguracionActivity extends AppCompatActivity {
    private SettingsManager settingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        this.settingsManager = new SettingsManager(this);
        Toolbar toolbar = findViewById(R.id.toolbarConfig);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        RadioGroup rgTheme = findViewById(R.id.rgTheme);
        int currentTheme = this.settingsManager.getTheme();
        if (currentTheme == SettingsManager.THEME_LIGHT) {
            rgTheme.check(R.id.rbLight);
        } else if (currentTheme == SettingsManager.THEME_DARK) {
            rgTheme.check(R.id.rbDark);
        } else {
            rgTheme.check(R.id.rbSystem);
        }

        rgTheme.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedTheme;
            if (checkedId == R.id.rbLight) {
                selectedTheme = SettingsManager.THEME_LIGHT;
            } else if (checkedId == R.id.rbDark) {
                selectedTheme = SettingsManager.THEME_DARK;
            } else {
                selectedTheme = SettingsManager.THEME_SYSTEM;
            }
            this.settingsManager.setTheme(selectedTheme);
        });
    }
}
