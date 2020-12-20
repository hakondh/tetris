package com.example.tetris;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class TetrisActivity extends AppCompatActivity {
    private TetrisView tetrisView;
    private String resume;
    private String exit;
    private final String TAG = "tetris_log";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tetris);
        tetrisView = findViewById(R.id.tetrisView);
        tetrisView.setFocusable(true);
        resume = getResources().getString(R.string.resume);
        exit = getResources().getString(R.string.exit);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        Log.i(TAG , "Handeling post create...");
        tetrisView.pause(false);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        menu.add(resume);
        menu.add(exit);
        return true;
    }

    // Pauses the game when the menu is opened
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        Log.i(TAG , "Preparing options menu...");
        if(tetrisView.getGameStart()) {
            tetrisView.pause(false);
            tetrisView.setGameStart(false);
        }
        else tetrisView.pause(true);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getTitle().equals(resume)){
            Log.i(TAG, "resume");
            tetrisView.pause(false);

        }
        else if(item.getTitle().equals(exit)){
            Log.i(TAG, "exit");
            tetrisView.endGame();
            this.finish(); // Finish this activity
        }
        return true;
    }


    // The method below implements the possibility to use the keyboard to play.
    // However, it has been commented out, as it caused my emulator to freeze when a key was pressed, for some unknown reason.
    // I did not manage to find the reason why this happened. Therefore, the game can also be controlled by buttons.
    // If you want to try it out anyway, you only need to uncomment the method below, and you can use the following keys to play: W=ROTATE, D=RIGHT, S=DOWN, A=LEFT

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        Log.i(TAG, "onKeyDown " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_A:
                tetrisView.buttonPress(0);
                return true;
            case KeyEvent.KEYCODE_D:
                tetrisView.buttonPress(1);
                return true;
            case KeyEvent.KEYCODE_W:
                tetrisView.buttonPress(2);
                return true;
            case KeyEvent.KEYCODE_S:
                tetrisView.buttonPress(3);
                return true;
            default:
                return super.onKeyDown(keyCode, keyEvent);
        }
    }*/

    public void goLeft(View view){
        tetrisView.buttonPress(0);
    }

    public void goRight(View view){
        tetrisView.buttonPress(1);
    }

    public void moveDown(View view){ tetrisView.buttonPress(2); }

    public void rotate(View view){ tetrisView.buttonPress(3); }

}
