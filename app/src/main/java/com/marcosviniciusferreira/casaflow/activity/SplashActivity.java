package com.marcosviniciusferreira.casaflow.activity;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.pm.PackageInfoCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.marcosviniciusferreira.casaflow.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);

        TextView textAppVersion = findViewById(R.id.textAppVersion);

        PackageManager packageManager = this.getPackageManager();
        String packageName = this.getPackageName();
        PackageInfo packageInfo;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            try {
                packageInfo = packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0));
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                packageInfo = packageManager.getPackageInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        long versionNumber = PackageInfoCompat.getLongVersionCode(packageInfo);
        textAppVersion.setText("Version: 0." + Double.parseDouble(String.valueOf(versionNumber)));
        textAppVersion.setTextColor(R.color.purple_700);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.casaflow));


        new Handler().postDelayed(this::openMainActivity, 3000);
    }

    private void openMainActivity() {
        startActivity(new Intent(SplashActivity.this, IntroductionsActivity.class));
        finish();
    }


}