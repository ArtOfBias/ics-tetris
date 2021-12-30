import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class GamePanel extends JPanel implements Runnable, KeyListener{
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String TURN = "turn";
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 25;

    public int[][] board = new int[BOARD_WIDTH][BOARD_HEIGHT];
    public Tetrimino currentPiece;
    public int[] currentPieceLocation = new int[2];

    @Override
    public void keyTyped(KeyEvent e){

    }

    @Override
    public void keyPressed(KeyEvent e){
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyReleased(KeyEvent e){
        // TODO Auto-generated method stub
        
    }

    @Override
    public void run(){
        // TODO Auto-generated method stub
        
    }

    public void draw(Graphics g){
        for (int x = 0; x < BOARD_WIDTH; x++){
            for (int y = 0; y < BOARD_HEIGHT; y++){
                // TODO: finish drawing board
                if (board[x][y] == 0){
                    
                }
                else if (board[x][y] == 1){

                }
                else if (board[x][y] == 2){
                    
                }
                else if (board[x][y] == 3){
                    
                }
                else if (board[x][y] == 4){
                    
                }
                else if (board[x][y] == 5){
                    
                }
                else if (board[x][y] == 6){
                    
                }
                else if (board[x][y] == 7){
                    
                }
                else {
                    throw new java.lang.Error("invalid value " + board[x][y] + " in board at " + x + " " + y);
                }
            }
        }
    }

    public void rotate(String rotationDirection){
        int[] rotationAnchorOriginal;
        int[] anchorOriginal;
        int counter;
        int testSqaureX = 0;
        int testSqaureY = 0;
        int[] best = new int[2];
        double bestDistance = 100;
        boolean found = false;

        if (rotationDirection.equals("left")){
            rotationAnchorOriginal = currentPiece.block(currentPiece.anchorLeftIndex()).clone();
            anchorOriginal = currentPiece.block(currentPiece.anchorTurnIndex()).clone();
            currentPiece.rotate(LEFT);

            // TODO: feel like there is some logic error here
            for (int anchorX = rotationAnchorOriginal[0] - 1; anchorX <= rotationAnchorOriginal[0] + 1; anchorX++){
                for (int anchorY = rotationAnchorOriginal[1] - 1; anchorY <= rotationAnchorOriginal[1] + 1; anchorY++){
                    counter = 0;
                    for (int i = 0; i < 4; i++){
                        testSqaureX = anchorX - currentPiece.block(currentPiece.anchorLeftIndex())[0] + currentPiece.block(i)[0];
                        testSqaureY = anchorY - currentPiece.block(currentPiece.anchorLeftIndex())[1] + currentPiece.block(i)[1];
                        if ((testSqaureX < 0) || (testSqaureX >= BOARD_WIDTH)) break;
                        if ((testSqaureY < 0) || (testSqaureY >= BOARD_HEIGHT)) break; // TODO: board height is higher than actual playing field, may need to account
                        if (board[testSqaureX][testSqaureY] == 0) counter++;
                    }
                    if (counter == 4){
                        if (!found){
                            found = true;
                            best[0] = anchorX;
                            best[1] = anchorY;
                            bestDistance = distance(best, anchorOriginal);
                        }
                        else {
                            if (distance(new int[] {anchorX,anchorY}, anchorOriginal) > bestDistance){
                                best[0] = anchorX;
                                best[1] = anchorY;
                                bestDistance = distance(best, anchorOriginal);
                            }
                            else {
                                if (anchorY < best[1]){
                                    best[0] = anchorX;
                                    best[1] = anchorY;
                                    bestDistance = distance(best, anchorOriginal);
                                }
                                else {
                                    if (anchorX > best[1]){
                                        best[0] = anchorX;
                                        best[1] = anchorY;
                                        bestDistance = distance(best, anchorOriginal);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else if (rotationDirection.equals("right")){
            rotationAnchorOriginal = currentPiece.block(currentPiece.anchorRightIndex()).clone();
            anchorOriginal = currentPiece.block(currentPiece.anchorTurnIndex()).clone();
            currentPiece.rotate(RIGHT);

            // TODO: feel like there is some logic error here
            for (int anchorX = rotationAnchorOriginal[0] - 1; anchorX <= rotationAnchorOriginal[0] + 1; anchorX++){
                for (int anchorY = rotationAnchorOriginal[1] - 1; anchorY <= rotationAnchorOriginal[1] + 1; anchorY++){
                    counter = 0;
                    for (int i = 0; i < 4; i++){
                        testSqaureX = anchorX - currentPiece.block(currentPiece.anchorRightIndex())[0] + currentPiece.block(i)[0];
                        testSqaureY = anchorY - currentPiece.block(currentPiece.anchorRightIndex())[1] + currentPiece.block(i)[1];
                        if ((testSqaureX < 0) || (testSqaureX >= BOARD_WIDTH)) break;
                        if ((testSqaureY < 0) || (testSqaureY >= BOARD_HEIGHT)) break; // TODO: board height is higher than actual playing field, may need to account
                        if (board[testSqaureX][testSqaureY] == 0) counter++;
                    }
                    if (counter == 4){
                        if (!found){
                            found = true;
                            best[0] = anchorX;
                            best[1] = anchorY;
                            bestDistance = distance(best, anchorOriginal);
                        }
                        else {
                            if (distance(new int[] {anchorX,anchorY}, anchorOriginal) > bestDistance){
                                best[0] = anchorX;
                                best[1] = anchorY;
                                bestDistance = distance(best, anchorOriginal);
                            }
                            else {
                                if (anchorY < best[1]){
                                    best[0] = anchorX;
                                    best[1] = anchorY;
                                    bestDistance = distance(best, anchorOriginal);
                                }
                                else {
                                    if (anchorX < best[1]){
                                        best[0] = anchorX;
                                        best[1] = anchorY;
                                        bestDistance = distance(best, anchorOriginal);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else if (rotationDirection.equals("turn")){
            rotationAnchorOriginal = currentPiece.block(currentPiece.anchorTurnIndex()).clone();
            anchorOriginal = currentPiece.block(currentPiece.anchorTurnIndex()).clone();
            currentPiece.rotate(TURN);

            // TODO: feel like there is some logic error here
            for (int anchorX = rotationAnchorOriginal[0] - 1; anchorX <= rotationAnchorOriginal[0] + 1; anchorX++){
                for (int anchorY = rotationAnchorOriginal[1] - 1; anchorY <= rotationAnchorOriginal[1] + 1; anchorY++){
                    counter = 0;
                    for (int i = 0; i < 4; i++){
                        testSqaureX = anchorX - currentPiece.block(currentPiece.anchorLeftIndex())[0] + currentPiece.block(i)[0];
                        testSqaureY = anchorY - currentPiece.block(currentPiece.anchorLeftIndex())[1] + currentPiece.block(i)[1];
                        if ((testSqaureX < 0) || (testSqaureX >= BOARD_WIDTH)) break;
                        if ((testSqaureY < 0) || (testSqaureY >= BOARD_HEIGHT)) break; // TODO: board height is higher than actual playing field, may need to account
                        if (board[testSqaureX][testSqaureY] == 0) counter++;
                    }
                    if (counter == 4){
                        if (!found){
                            found = true;
                            best[0] = anchorX;
                            best[1] = anchorY;
                            bestDistance = distance(best, anchorOriginal);
                        }
                        else {
                            if (distance(new int[] {anchorX,anchorY}, anchorOriginal) > bestDistance){
                                best[0] = anchorX;
                                best[1] = anchorY;
                                bestDistance = distance(best, anchorOriginal);
                            }
                            else {
                                if (anchorY < best[1]){
                                    best[0] = anchorX;
                                    best[1] = anchorY;
                                    bestDistance = distance(best, anchorOriginal);
                                }
                                else {
                                    if (anchorX > best[1]){
                                        best[0] = anchorX;
                                        best[1] = anchorY;
                                        bestDistance = distance(best, anchorOriginal);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public double distance(int[] point1, int[] point2){
        double square1 = Math.pow(point1[0] - point2[0], 2);
        double square2 = Math.pow(point1[1] - point2[1], 2);
        return Math.sqrt(square1 + square2);
    }
}