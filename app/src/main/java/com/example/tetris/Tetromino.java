package com.example.tetris;

import android.graphics.Paint;
import android.util.Log;

public class Tetromino {
    private int[][] tetMatrix;
    private int xPos;
    private int yPos;
    private Paint paint;
    private final String TAG = "tetris_log";

    public Tetromino(int[][] tetMatrix){
        this.tetMatrix = tetMatrix;
        // The standard starting position for a tetronimo
        xPos = 7;
        yPos = 0;
    }

    public void rotate(){
        int[][] rotMatrix;
        rotMatrix = transposeMatrix(tetMatrix);
        rotMatrix = reverseMatrix(rotMatrix);
        tetMatrix = rotMatrix;
    }

    // "Flip" the matrix over its diagonal. The indexes of the matrix are switched.
    public int[][] transposeMatrix(int[][] mat){
        int[][] transposedMatrix = new int[mat[0].length][mat.length];
        for(int i = 0; i < mat.length; i++){
            for(int h = 0; h < mat[0].length; h++){
                transposedMatrix[h][i] = mat[i][h];
            }
        }
        return transposedMatrix;
    }

    public int[][] reverseMatrix(int[][] mat){
        int mid = mat.length/2;
        for(int i = 0; i < mid; i++){
            int[] helper = mat[i];
            mat[i] = mat[mat.length - i - 1];
            mat[mat.length - i - 1] = helper;
        }
        return mat;
    }

    // Get- and set-methods
    public int[][] getTetmatrix(){
        return tetMatrix;
    }
    public int getxPos(){ return xPos; }
    public int getyPos(){ return yPos; }
    public void changexPos (int value){ this.xPos += value; }
    public void setyPos(int value){ this.yPos = value; }
    public void incrementyPos(){this.yPos++; }
    public void setPaint(Paint paint){this.paint = paint;}
    public Paint getPaint(){return paint; }
}
