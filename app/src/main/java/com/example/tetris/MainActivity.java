package com.example.tetris;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity  {
    private final String TAG = "tetris_log";
    private Button btnStartGame;
    private Button btnHtp;
    private Button btnSettings;
    private final int request_code = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the selected language (saved in shared_prefs), and set it as locale
        loadAndSetLocale();

        // Get the UI-components
        btnStartGame = findViewById(R.id.start_game_btn);
        btnHtp = findViewById(R.id.htp_btn);
        btnSettings = findViewById(R.id.settings_btn);

        // Update the UI-components to the correct language
        setLanguage();
    }

    // Make sure to set the right language when returning from another activity
    @Override
    protected void onResume(){
        super.onResume();
        setLanguage();
    }

    // Enter game
    public void startGame(View v){
        Log.i(TAG, "Starting the game...");
        Intent i = new Intent(MainActivity.this, TetrisActivity.class);
        startActivityForResult(i, request_code);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast toast = Toast.makeText(this, R.string.game_over, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 625);
                ViewGroup viewGroup = (ViewGroup) toast.getView();
                TextView textView = (TextView) viewGroup.getChildAt(0);
                textView.setTextSize(25);
                textView.setTextColor(Color.parseColor("#F6D55C"));
                viewGroup.setBackgroundColor(Color.parseColor("#20639B"));
                toast.show();
            }
        }
    }

    // Enter how to play
    public void showHowToPlay(View v){
        Log.i(TAG, "Showing how to play...");
        Intent i = new Intent(MainActivity.this, HowToPlayActivity.class);
        startActivity(i);
    }

    // Enter settings
    public void showSettings(View v){
        Log.i(TAG, "Showing settings...");
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(i);
    }

    // Set the selected language
    public void loadAndSetLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("chosen_language", "");
        setLocale(language);
    }

    private void setLocale(String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        // Save data to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("chosen_language", language);
        editor.apply();
    }

    // Update the UI-components to the selected language
    private void setLanguage(){
        btnStartGame.setText(R.string.start_game);
        btnHtp.setText(R.string.how);
        btnSettings.setText(R.string.settings);
    }


}
