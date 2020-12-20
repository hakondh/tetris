package com.example.tetris;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private final String TAG = "tetris_log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    // Can be made into a single method?
    public void changeToNorwegian(View v){
        Log.i(TAG, "Changing language to Norwegian...");
        setLocale("no");
        recreate(); // Refresh the activity to get the new language
    }

    public void changeToEnglish(View v){
        Log.i(TAG, "Changing language to English...");
        setLocale("en");
        recreate(); // Refresh the activity to get the new language
    }

    // Set the locale to the newly selected language
    private void setLocale(String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        // Save data to shared preferences (so language is saved for the next session)
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("chosen_language", language);
        editor.apply();
    }
}
