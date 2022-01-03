import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class GamePanel extends JPanel implements Runnable, KeyListener{
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String TURN = "turn";
    public static final String DOWN = "down";
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 25;

    public int[][] board = new int[BOARD_WIDTH][BOARD_HEIGHT];
    public Tetrimino currentPiece;
    public int[] currentPieceLocation = new int[2];
    public int[] ghostPieceLocation = new int[2];

    public boolean held_Z = false;
    public boolean held_UP = false;
    public boolean held_A = false;

    public boolean held_LEFT = false;
    public boolean held_RIGHT = false;

    public boolean softDropHeld = false;

    @Override
    public void keyTyped(KeyEvent e){

    }

    @Override
    public void keyPressed(KeyEvent e){
        if (e.getKeyCode() == KeyEvent.VK_Z){
            if (!held_Z){
                held_Z = true;
                rotate(LEFT);
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_UP){
            if (!held_UP){
                held_UP = true;
                rotate(RIGHT);
            }
        }
        
        if (e.getKeyCode() == KeyEvent.VK_A){
            if (!held_A){
                held_A = true;
                rotate(TURN);
            }
        }

        // TODO: unfinished
        if (e.getKeyCode() == KeyEvent.VK_LEFT){
            if (!held_LEFT){
                held_LEFT = true;
                move(LEFT);
            }

        }

        if (e.getKeyCOde() == KeyEvent.VK_RIGHT){
            if (!held_RIGHT){
                held_RIGHT = true;
                move(RIGHT)
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e){
        if (e.getKeyCode() == KeyEvent.VK_Z){
            held_Z = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP){
            held_UP = false;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_A){
            held_A = false;
        }
        
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

        // TODO: draw ghost piece
        // TODO: draw current piece
        // TODO: draw queue
    }

    public void rotate(String direction){
        int[] rotationAnchorOriginal;
        int[] anchorOriginal;
        int counter;
        int testSqaureX = 0;
        int testSqaureY = 0;
        int[] best = new int[2];
        double bestDistance = 100;
        boolean found = false;

        // TODO: add way to update position?

        if (direction.equals("left")){
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

                        if ((testSqaureX < 0) || (testSqaureX >= BOARD_WIDTH)){
                            break;
                        }

                        if ((testSqaureY < 0) || (testSqaureY >= BOARD_HEIGHT)){
                            break; // TODO: board height is higher than actual playing field, may need to account
                        }

                        if (board[testSqaureX][testSqaureY] == 0){
                            counter++;
                        }
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

            if (found){
                currentPieceLocation[0] = best[0];
                currentPieceLocation[1] = best[1];
            }
            else {
                currentPiece.rotate(RIGHT);
            }
        }
        else if (direction.equals("right")){
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

                        if ((testSqaureX < 0) || (testSqaureX >= BOARD_WIDTH)){
                            break;
                        }

                        if ((testSqaureY < 0) || (testSqaureY >= BOARD_HEIGHT)){
                            break; // TODO: board height is higher than actual playing field, may need to account
                        }

                        if (board[testSqaureX][testSqaureY] == 0){
                            counter++;
                        }
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

            if (!found){
                currentPiece.rotate(LEFT);
            }
        }
        else if (direction.equals("turn")){
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

                        if ((testSqaureX < 0) || (testSqaureX >= BOARD_WIDTH)){
                            break;
                        }

                        if ((testSqaureY < 0) || (testSqaureY >= BOARD_HEIGHT)){
                            break; // TODO: board height is higher than actual playing field, may need to account
                        }

                        if (board[testSqaureX][testSqaureY] == 0){
                            counter++;
                        }
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

            if (!found){
                currentPiece.rotate(TURN);
            }
        }
        else {
            throw new java.lang.Error("Invalid rotation type.");
        }
    }

    public void ghostPiece(){
        int counter;
        int x = currentPieceLocation[0];
        int y;
        int testSqaureX;
        int testSqaureY;

        for (y = currentPieceLocation[1]; y >= 0; y--){
            counter = 0;

            for (int i = 0; i < 4; i++){
                testSqaureX = x + currentPiece.block(i)[0];
                testSqaureY = y + currentPiece.block(i)[1];

                if ((testSqaureX < 0) || (testSqaureX >= BOARD_WIDTH)){
                    continue;
                }

                if ((testSqaureY < 0) || (testSqaureY >= BOARD_HEIGHT)){
                    continue;
                }

                if (board[testSqaureX][testSqaureY] == 0){
                    counter++;
                }
            }

            if (counter < 4){
                break;
            }
        }
        ghostPieceLocation = new int[] {x,y};
    }

    public void hardDrop(){
        // TODO: finish hard drop, currently just thinking of setting currentPieceLocation to ghostPieceLocation
    }

    public void move(String direction){
        int counter = 0;
        int x = currentPieceLocation[0];
        int y = currentPieceLocation[1];
        int testSqaureX;
        int testSqaureY;

        if (direction.equals("down")){
            y--;

            for (int i = 0; i < 4; i++){
                testSqaureX = x + currentPiece.block(i)[0];
                testSqaureY = y + currentPiece.block(i)[1];

                if ((testSqaureX < 0) || (testSqaureX >= BOARD_WIDTH)){
                    continue;
                }

                if ((testSqaureY < 0) || (testSqaureY >= BOARD_HEIGHT)){
                    continue;
                }

                if (board[testSqaureX][testSqaureY] == 0){
                    counter++;
                }
            }

            if (counter == 4){
                currentPieceLocation = new int[] {x,y};
            }
        }
        else if (direction.equals("left")){
            x--;

            for (int i = 0; i < 4; i++){
                testSqaureX = x + currentPiece.block(i)[0];
                testSqaureY = y + currentPiece.block(i)[1];

                if ((testSqaureX < 0) || (testSqaureX >= BOARD_WIDTH)){
                    continue;
                }

                if ((testSqaureY < 0) || (testSqaureY >= BOARD_HEIGHT)){
                    continue;
                }

                if (board[testSqaureX][testSqaureY] == 0){
                    counter++;
                }
            }

            if (counter == 4){
                currentPieceLocation = new int[] {x,y};
            }
        }
        else if (direction.equals("right")){
            x++;

            for (int i = 0; i < 4; i++){
                testSqaureX = x + currentPiece.block(i)[0];
                testSqaureY = y + currentPiece.block(i)[1];

                if ((testSqaureX < 0) || (testSqaureX >= BOARD_WIDTH)){
                    continue;
                }

                if ((testSqaureY < 0) || (testSqaureY >= BOARD_HEIGHT)){
                    continue;
                }

                if (board[testSqaureX][testSqaureY] == 0){
                    counter++;
                }
            }

            if (counter == 4){
                currentPieceLocation = new int[] {x,y};
            }
        }
        else {
            throw new java.lang.Error("Invalid movement direction.");
        }
    }

    public double distance(int[] point1, int[] point2){
        double square1 = Math.pow(point1[0] - point2[0], 2);
        double square2 = Math.pow(point1[1] - point2[1], 2);
        return Math.sqrt(square1 + square2);
    }
}