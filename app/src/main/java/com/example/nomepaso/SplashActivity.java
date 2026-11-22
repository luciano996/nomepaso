package com.example.nomepaso;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

/* loaded from: classes5.dex */
public class SplashActivity extends AppCompatActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.example.nomepaso.SplashActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SplashActivity.this.m148lambda$onCreate$0$comexamplenomepasoSplashActivity();
            }
        }, 2000L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$0$com-example-nomepaso-SplashActivity, reason: not valid java name */
    public /* synthetic */ void m148lambda$onCreate$0$comexamplenomepasoSplashActivity() {
        Intent intent = new Intent(this, (Class<?>) MainActivity.class);
        startActivity(intent);
        finish();
    }
}
