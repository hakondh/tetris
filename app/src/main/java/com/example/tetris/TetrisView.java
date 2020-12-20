package com.example.tetris;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Random;
import java.util.ArrayList;
import static android.app.Activity.RESULT_OK;

public class TetrisView extends SurfaceView implements SurfaceHolder.Callback{
    private TetrisThread thread;
    private Tetromino[] tets = new Tetromino[5];
    private Tetromino movingTet;
    private ArrayList<Tetromino> previousTets = new ArrayList<>();
    private int surfaceHeight;
    private final int COMPONENT_SIZE = 50;
    private final int tilesWidth = 16, tilesHeight = 24;
    private final int[][] spots = new int[tilesHeight][tilesWidth];
    private int moveSpeed = 400;
    private long timePassed;
    private long prevTime;
    private Paint[] colors = new Paint[5];
    private Context context;
    private boolean gameStart;
    private final String TAG = "tetris_log";

    public TetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        setFocusable(true); // Get key events

        // The terominos are represented by matrices, where 1 is a component and 0 is empty space.
        // Add all the different tetrominos to the tets-array.
        tets[0] = new Tetromino(new int[][]{{1, 1, 1, 1}});          // I-form
        tets[1] = new Tetromino(new int[][]{{1, 1, 1}, {0, 1, 0}});  // T-form
        tets[2] = new Tetromino(new int[][]{{1, 1, 0}, {0, 1, 1}});  // S-form
        tets[3] = new Tetromino(new int[][]{{0, 0, 1,}, {1, 1, 1}}); // L-form
        tets[4] = new Tetromino(new int[][]{{1, 1}, {1, 1}});        // Box-form

        // The different colors of the different tetrominos
        colors[0] = new Paint();
        colors[0].setColor(Color.CYAN);
        colors[1] = new Paint();
        colors[1].setColor(Color.BLUE);
        colors[2] = new Paint();
        colors[2].setColor(Color.RED);
        colors[3] = new Paint();
        colors[3].setColor(Color.YELLOW);
        colors[4] = new Paint();
        colors[4].setColor(Color.GREEN);

        // Select a random tetromino, and set its color
        Random random = new Random();
        int randomInt = random.nextInt(5);
        movingTet = tets[randomInt];
        movingTet.setPaint(colors[randomInt]);

        timePassed = 0;
        prevTime = 0;

        thread = new TetrisThread(holder, this); // Create thread for the game

        gameStart = true;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
        pause(true);
        Log.i(TAG,"Surface was created");
        Log.i(TAG, "The game starts now!");
        pause(false);
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG,"Surface was changed");
        this.surfaceHeight = height;
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG,"Surface was destroyed");
        endGame();
    }

    /* | ***** DRAWING ***** | */

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.BLACK);

        // Create grid
        Paint p = new Paint();
        p.setColor(Color.GRAY);
        for(int i = 0; i < tilesHeight; i++){
            canvas.drawLine(0, i*COMPONENT_SIZE, tilesWidth*COMPONENT_SIZE, i*COMPONENT_SIZE, p);
        }
        for(int i = 0; i < tilesWidth; i++){
            canvas.drawLine(i*COMPONENT_SIZE, 0, i*COMPONENT_SIZE, tilesHeight*COMPONENT_SIZE, p);
        }


        // As long as the thread is not paused, update the tetromino so that it will continue to move
        if(!thread.isPaused()){
            timePassed += System.currentTimeMillis() - prevTime;
            prevTime = System.currentTimeMillis();
            // If more than the speed we have set of time has passed, move the tetromino down
            if(timePassed > moveSpeed){
                timePassed = 0;
                int length = (movingTet.getyPos()+1)*COMPONENT_SIZE + movingTet.getTetmatrix().length * COMPONENT_SIZE;
                int spotReached = (movingTet.getyPos()+1) + movingTet.getTetmatrix().length - 1;

                // Iterating through the spot-matrix to see if there is any collision
                int[][] tetMatrix = movingTet.getTetmatrix();
                boolean collision = false;

                // If the tet has reached tile 24, it has reached the bottom, no need to check if it collided with another tet
                if(spotReached != 24){
                    for(int row = 0; row < tetMatrix.length; row++){
                        for(int column = 0; column < tetMatrix[row].length; column++){
                            if(tetMatrix[row][column] != 0){
                                if(spots[movingTet.getyPos() + row +1][movingTet.getxPos() + column] == 1) { // If the "spot" below a component in the tetromino is set to 1, we have a collision!
                                    Log.i(TAG, "COLLISION!");
                                    collision = true;
                                    break;
                                }
                            }
                        }
                        if(collision) break;
                    }
                }

                // Check if we can move the tetromino further (not reached the bottom or collided)
                if(!(length > surfaceHeight) && !(collision)){
                    movingTet.incrementyPos();
                }
                // If the tetromino has collided at y=0, it is game over. We go back to main menu
                else if(collision && movingTet.getyPos() == 0){
                    Log.i(TAG, "Finishing...");
                    TetrisActivity ta = (TetrisActivity) context;
                    Intent intent = new Intent();
                    ta.setResult(RESULT_OK, intent);
                    ta.finish(); // This will run surfaceDestroyed, which then will free machine resources

                }
                // If the tetromino has reached the bottom or collided, handle it
                else{
                    // This registers the coordinates of the spots that now are "taken" by the newly arrived tetromino
                    handleCollision();
                }
            }
        }
        drawTetromino(canvas);
    }

    // This method registers where the collided tetromino is positioned, and creates a new tetromino to move down
    private void handleCollision(){
        int x = movingTet.getxPos();
        int y = movingTet.getyPos();
        int[][] tetMatrix = movingTet.getTetmatrix();
        for(int row = 0; row < tetMatrix.length; row++){
            for(int column= 0; column < tetMatrix[row].length; column++){
                if(tetMatrix[row][column] != 0){
                    spots[y + row][x + column] = 1;
                }
            }
        }
        // Save the moving tetromino so we can draw it later
        previousTets.add(movingTet);
        // Create a new, random tetromino to appear
        Random random = new Random();
        int randomInt = random.nextInt(5);
        Tetromino newTetromino = new Tetromino(tets[randomInt].getTetmatrix());
        movingTet = newTetromino;
        movingTet.setPaint(colors[randomInt]);
    }

    // Draw the tetrominos
    public void drawTetromino(Canvas canvas){
        // This will draw the single moving tetromino
        int[][] tetMatrix = movingTet.getTetmatrix();
        for(int row = 0; row < tetMatrix.length; row++){
            for(int column = 0; column < tetMatrix[row].length; column++){
                if(tetMatrix[row][column] != 0){
                        Rect rect = new Rect();
                        rect.set(column*COMPONENT_SIZE + movingTet.getxPos()*COMPONENT_SIZE,
                                row*COMPONENT_SIZE + movingTet.getyPos()*COMPONENT_SIZE ,
                                column*COMPONENT_SIZE + COMPONENT_SIZE + movingTet.getxPos()*COMPONENT_SIZE,
                                row*COMPONENT_SIZE + COMPONENT_SIZE + movingTet.getyPos()*COMPONENT_SIZE);
                        canvas.drawRect(rect, movingTet.getPaint());
                }
            }
        }

        // This will draw the still terominos
        for(int i = 0; i < previousTets.size(); i++){
            Tetromino prevTet = previousTets.get(i);
            int[][] prevTetMatrix = prevTet.getTetmatrix();
            for(int row = 0; row < prevTetMatrix.length; row++){
                for(int column = 0; column < prevTetMatrix[row].length; column++){
                    if(prevTetMatrix[row][column] != 0){
                        Rect rect = new Rect();
                        rect.set(column*COMPONENT_SIZE + prevTet.getxPos()*COMPONENT_SIZE,
                                row*COMPONENT_SIZE + prevTet.getyPos()*COMPONENT_SIZE ,
                                column*COMPONENT_SIZE + COMPONENT_SIZE + prevTet.getxPos()*COMPONENT_SIZE,
                                row*COMPONENT_SIZE + COMPONENT_SIZE + prevTet.getyPos()*COMPONENT_SIZE);
                        canvas.drawRect(rect, prevTet.getPaint());
                    }
                }
            }
        }
    }

    /* | ***** CONTROLLERS ***** | */

    // Function to handle presses to the buttons
    public void buttonPress(int value){
        boolean sideCollision = false;
        int[][] tetMatrix = movingTet.getTetmatrix();

        // 0=left, 1=right
        if (value == 0) {
            // Need to check if we have reached the border or not
            if(!(movingTet.getxPos()-1 == -1)) {
                for(int row = 0; row < tetMatrix.length; row++){
                    for(int column = 0; column < tetMatrix[row].length; column++){
                        if(tetMatrix[row][column] != 0){
                            if(spots[movingTet.getyPos() + row][movingTet.getxPos() + column - 1] == 1) { // If the "spot" to the side of  a component in the tetromino is set to 1, we have a collision!
                                Log.i(TAG, "COLLISION!");
                                sideCollision = true;
                                break;
                            }
                        }
                    }
                    if(sideCollision) break;
                }
                if(!sideCollision) movingTet.changexPos(-1);
            }
        }
        else if(value == 1){
            // Need to check if we have reached the border or not, OR if we are colliding with another tetromino
            if(!((movingTet.getxPos()+1)*COMPONENT_SIZE + movingTet.getTetmatrix()[0].length*COMPONENT_SIZE > 800)){
                for(int row = 0; row < tetMatrix.length; row++){
                    for(int column = 0; column < tetMatrix[row].length; column++){
                        if(tetMatrix[row][column] != 0){
                            if(spots[movingTet.getyPos() + row][movingTet.getxPos() + column + 1] == 1) { // If the "spot" to the side of  a component in the tetromino is set to 1, we have a collision!
                                Log.i(TAG, "COLLISION!");
                                sideCollision = true;
                                break;
                            }
                        }
                    }
                    if(sideCollision) break;
                }
                if(!sideCollision) movingTet.changexPos(1);
            }
        }
        else if(value == 2){
            // If the tetromino is at the bottom, there is no need to sink it down
            if((movingTet.getyPos() + tetMatrix.length) != 24) {
                //Log.i(TAG, "Not at the bottom...");
                for(int yInc = 0; (movingTet.getyPos() + tetMatrix.length + yInc) < spots.length + 1; yInc++){
                    for(int row = 0; row < tetMatrix.length; row++){
                        for(int column = 0; column < tetMatrix[row].length; column++){
                            if(tetMatrix[row][column] != 0){
                                if(spots[movingTet.getyPos() + row +yInc][movingTet.getxPos() + column] == 1) { // If the "spot" below a component in the tetromino is set to 1, we have a collision!
                                    Log.i(TAG, "COLLISION!");
                                    movingTet.setyPos(movingTet.getyPos() + (yInc - 1));
                                    handleCollision();
                                    return;
                                }
                            }
                        }

                    }
                }
                // If there is no tetromino to collide with below, just go to the bottom
                movingTet.setyPos(24 - tetMatrix.length);
                Log.i(TAG, "Go to the bottom...");
                handleCollision();
            }
        }
        else if(value == 3){
            if(canRotate(movingTet.getxPos(), COMPONENT_SIZE, spots)) movingTet.rotate();
        }
    }

    // Check if a rotation won't lead to the tetromino to go outside of the screen OR colliding into another tetromino
    public boolean canRotate(int xPos, int componentSize, int[][] spots){
        int[][] rotatedTetomino = movingTet.transposeMatrix(movingTet.getTetmatrix());
        rotatedTetomino = movingTet.reverseMatrix(rotatedTetomino);


        if(movingTet.getyPos() + rotatedTetomino.length > spots.length) return false; // Check if a rotation will flip the tetromino out of the screen (y), don
        // Check that we don't go outside the screen
        if(xPos*componentSize + rotatedTetomino[0].length*componentSize > 800) return false;

        // Check that we don't collide with another tetromino
        for(int row = 0; row < rotatedTetomino.length; row++){
            for(int column = 0; column < rotatedTetomino[row].length; column++){
                if(rotatedTetomino[row][column] != 0){
                    if(spots[movingTet.getyPos() + row][xPos + column] == 1) {
                        Log.i(TAG, "COLLISION!");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /* | ***** FUNCTIONS FOR PAUSING/UNPAUSING/ENDING...  ***** | */

    public void pause(boolean value){
        thread.setPaused(value);
    }

    public void endGame(){
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean getGameStart(){ return gameStart; }

    public void setGameStart(boolean value) { this.gameStart = value; }

}
