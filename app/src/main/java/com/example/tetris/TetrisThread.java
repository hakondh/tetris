package com.example.tetris;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class TetrisThread extends Thread{
    private final String TAG = "tetris_log";
    private SurfaceHolder surfaceHolder;
    private TetrisView tetrisView;
    private final static int sleepInterval = 200;
    private boolean run = false;
    private boolean pause = false;

    public TetrisThread(SurfaceHolder holder, TetrisView tetrisView){
        this.surfaceHolder = holder;
        this.tetrisView = tetrisView;
    }

    @Override
    public void run(){
        Log.i(TAG, "Thread was started.");
        Canvas canvas = null;



        while(isRunning()){
            try{
                // Lock the canvas to make sure it is not altered or deleted while drawing is in progress
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder){
                    tetrisView.draw(canvas);
                }
            }
            finally{
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            mySleep(sleepInterval);
        }
    }

    private void mySleep(int length) {
        try {
            sleep(length);
        }
        catch (InterruptedException e) {
            Log.i(TAG,"Error in mySleep");
        }
    }

    // Start or stop the game thread
    public void setRunning(boolean run) {
        this.run=run;
    }

    public boolean isRunning() {
        return run;
    }

    public void setPaused(boolean p) {
        synchronized(surfaceHolder) {
            pause = p;
        }
    }

    public boolean isPaused() {
        synchronized(surfaceHolder) {
            return pause;
        }
    }
}
