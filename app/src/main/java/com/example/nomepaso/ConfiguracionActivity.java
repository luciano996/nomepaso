package com.example.nomepaso;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.nomepaso.utils.SettingsManager;

/* loaded from: classes5.dex */
public class ConfiguracionActivity extends AppCompatActivity {
    private SettingsManager settingsManager;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        this.settingsManager = new SettingsManager(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarConfig);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: com.example.nomepaso.ConfiguracionActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ConfiguracionActivity.this.m138lambda$onCreate$0$comexamplenomepasoConfiguracionActivity(view);
            }
        });
        RadioGroup rgTheme = (RadioGroup) findViewById(R.id.rgTheme);
        int currentTheme = this.settingsManager.getTheme();
        if (currentTheme == 1) {
            rgTheme.check(R.id.rbLight);
        } else if (currentTheme == 2) {
            rgTheme.check(R.id.rbDark);
        } else {
            rgTheme.check(R.id.rbSystem);
        }
        rgTheme.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.example.nomepaso.ConfiguracionActivity$$ExternalSyntheticLambda1
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public final void onCheckedChanged(RadioGroup radioGroup, int i) {
                ConfiguracionActivity.this.m139lambda$onCreate$1$comexamplenomepasoConfiguracionActivity(radioGroup, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$0$com-example-nomepaso-ConfiguracionActivity, reason: not valid java name */
    public /* synthetic */ void m138lambda$onCreate$0$comexamplenomepasoConfiguracionActivity(View v) {
        onBackPressed();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$1$com-example-nomepaso-ConfiguracionActivity, reason: not valid java name */
    public /* synthetic */ void m139lambda$onCreate$1$comexamplenomepasoConfiguracionActivity(RadioGroup group, int checkedId) {
        int selectedTheme;
        if (checkedId == R.id.rbLight) {
            selectedTheme = 1;
        } else {
            int selectedTheme2 = R.id.rbDark;
            if (checkedId == selectedTheme2) {
                selectedTheme = 2;
            } else {
                selectedTheme = 0;
            }
        }
        this.settingsManager.setTheme(selectedTheme);
    }
}
