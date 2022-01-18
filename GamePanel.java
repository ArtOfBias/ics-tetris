import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class GamePanel extends JPanel implements Runnable, KeyListener{
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String TURN = "turn";
    public static final String DOWN = "down";

    public static final int BOARD_WIDTH = 10;
    public static final int END_HEIGHT = 20;
    public static final int BOARD_HEIGHT = 25;

    public static final int LOCK_TIME = 500;

    public static final int[][] START_POSITIONS = new int[][] {
        {5,1},
        {6,1},
        {5,1},
        {5,1},
        {5,2},
        {5,1}
    };

    public double das = 120; // delayed auto shift
    public double arr = 10; // auto repeat rate

    public double dropDelay = 1000; // time it takes for piece to fall automatically
    // TODO this should be dynamic and change with levels

    public int[][] board = new int[BOARD_WIDTH][BOARD_HEIGHT];
    public Tetrimino currentPiece;
    public int[] currentPieceLocation = new int[2];
    public int[] ghostPieceLocation = new int[2];

    public int hold = 0;

    // variables for handling piece queue
    public Bag bag1 = new Bag();
    public Bag bag2 = new Bag();
    public int bagPosition = 0;

    public boolean held_Z = false;
    public boolean held_UP = false;
    public boolean held_A = false;
    public boolean held_C = false;

    public boolean hold_pressed = false;

    public boolean held_LEFT = false;
    public boolean first_LEFT = true;
    public Stopwatch stopwatchLeft = new Stopwatch();

    public boolean held_RIGHT = false;
    public boolean first_RIGHT = true;
    public Stopwatch stopwatchRight = new Stopwatch();

    public boolean held_DOWN = false;
    public boolean first_DOWN = true;
    public Stopwatch stopwatchDown = new Stopwatch();

    public Stopwatch stopwatchFall = new Stopwatch();

    public Stopwatch stopwatchLock = new Stopwatch();

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

        if (e.getKeyCode() == KeyEvent.VK_LEFT){
            if (!held_LEFT){
                stopwatchLeft.start();
                move(LEFT);
                held_LEFT = true;
            }
            else {
                if (first_LEFT && (stopwatchLeft.elapsed() >= das)){
                    stopwatchLeft.restart();
                    move(LEFT);
                    first_LEFT = false;
                }
                else if ((!first_LEFT) && (stopwatchLeft.elapsed() >= arr)){
                    stopwatchLeft.reset();
                    move(LEFT);
                }
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            if (!held_RIGHT){
                stopwatchRight.start();
                move(RIGHT);
                held_RIGHT = true;
            }
            else {
                if (first_RIGHT && (stopwatchRight.elapsed() >= das)){
                    stopwatchRight.restart();
                    move(RIGHT);
                    first_RIGHT = false;
                }
                else if ((!first_RIGHT) && (stopwatchRight.elapsed() >= arr)){
                    stopwatchRight.reset();
                    move(RIGHT);
                }
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN){
            if (!held_DOWN){
                stopwatchDown.start();
                stopwatchFall.restart();
                move(DOWN);
                held_DOWN = true;
            }
            else {
                if (first_DOWN && (stopwatchDown.elapsed() >= das)){
                    stopwatchDown.restart();
                    stopwatchFall.restart();
                    move(DOWN);
                    first_DOWN = false;
                }
                else if ((!first_DOWN) && (stopwatchRight.elapsed() >= arr)){
                    stopwatchDown.reset();
                    stopwatchFall.restart();
                    move(DOWN);
                }
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_C){
            if ((!held_C) && (!hold_pressed)){
                holdPiece();
                held_C = true;
                hold_pressed = true;
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

        if (e.getKeyCode() == KeyEvent.VK_LEFT){
            held_LEFT = false;
            first_LEFT = true;
            stopwatchLeft.reset();
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            held_RIGHT = false;
            first_RIGHT = true;
            stopwatchRight.reset();
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN){
            held_DOWN = false;
            first_DOWN = true;
            stopwatchDown.reset();
        }
    }

    @Override
    public void run(){
        // TODO not sure if this implementation will actually work
        // TODO current code only supports one playthrough
        boolean end = false;

        while (true){


            if (currentPieceLocation[1] == ghostPieceLocation[1]){
                stopwatchLock.start();

                // actions once block is locked
                if (stopwatchLock.elapsed() >= 500){
                    stopwatchLock.reset();
                    hold_pressed = false;
                    placeBlock();
                    nextBlock();

                    for (int x = 0; x < BOARD_WIDTH; x++){
                        if (board[x][END_HEIGHT] != 0){
                            end = true;
                        }
                    }
    
                    if (end){
                        break;
                    }
                }
            }
        }
    }

    public void draw(Graphics g){
        for (int x = 0; x < BOARD_WIDTH; x++){
            for (int y = 0; y < BOARD_HEIGHT; y++){
                // TODO finish drawing board
                if (board[x][y] == 0){
                    // empty
                }
                else if (board[x][y] == 1){
                    // o piece
                }
                else if (board[x][y] == 2){
                    // i piece
                }
                else if (board[x][y] == 3){
                    // t piece
                }
                else if (board[x][y] == 4){
                    // l piece
                }
                else if (board[x][y] == 5){
                    // j piece
                }
                else if (board[x][y] == 6){
                    // s piece
                }
                else if (board[x][y] == 7){
                    // z piece
                }
                else {
                    throw new RuntimeException("invalid value " + board[x][y] + " in board at " + x + " " + y);
                }
            }
        }

        // TODO draw ghost piece
        // TODO draw current piece
        // TODO draw queue
    }

    public void rotate(String direction){
        int[] rotationAnchorOriginal;
        int[] anchorOriginal;
        int counter;
        int testSquareX = 0;
        int testSquareY = 0;
        int[] best = new int[2];
        double bestDistance = 100;
        boolean found = false;

        if (direction.equals("left")){
            rotationAnchorOriginal = currentPiece.block(currentPiece.anchorIndex(LEFT)).clone();
            anchorOriginal = currentPiece.block(currentPiece.anchorIndex(TURN)).clone();
            currentPiece.rotate(LEFT);

            for (int anchorX = rotationAnchorOriginal[0] - 1; anchorX <= rotationAnchorOriginal[0] + 1; anchorX++){
                for (int anchorY = rotationAnchorOriginal[1] - 1; anchorY <= rotationAnchorOriginal[1] + 1; anchorY++){
                    counter = 0;

                    for (int i = 0; i < 4; i++){
                        testSquareX = anchorX - currentPiece.block(currentPiece.anchorIndex(LEFT))[0] + currentPiece.block(i)[0];
                        testSquareY = anchorY - currentPiece.block(currentPiece.anchorIndex(LEFT))[1] + currentPiece.block(i)[1];

                        if ((testSquareX < 0) || (testSquareX >= BOARD_WIDTH)){
                            break;
                        }

                        if ((testSquareY < 0) || (testSquareY >= BOARD_HEIGHT)){
                            break;
                        }

                        if (board[testSquareX][testSquareY] == 0){
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
        }
        else if (direction.equals("right")){
            rotationAnchorOriginal = currentPiece.block(currentPiece.anchorIndex(RIGHT)).clone();
            anchorOriginal = currentPiece.block(currentPiece.anchorIndex(TURN)).clone();
            currentPiece.rotate(RIGHT);

            for (int anchorX = rotationAnchorOriginal[0] - 1; anchorX <= rotationAnchorOriginal[0] + 1; anchorX++){
                for (int anchorY = rotationAnchorOriginal[1] - 1; anchorY <= rotationAnchorOriginal[1] + 1; anchorY++){
                    counter = 0;
                    
                    for (int i = 0; i < 4; i++){
                        testSquareX = anchorX - currentPiece.block(currentPiece.anchorIndex(RIGHT))[0] + currentPiece.block(i)[0];
                        testSquareY = anchorY - currentPiece.block(currentPiece.anchorIndex(RIGHT))[1] + currentPiece.block(i)[1];

                        if ((testSquareX < 0) || (testSquareX >= BOARD_WIDTH)){
                            break;
                        }

                        if ((testSquareY < 0) || (testSquareY >= BOARD_HEIGHT)){
                            break;
                        }

                        if (board[testSquareX][testSquareY] == 0){
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
        }
        else if (direction.equals("turn")){
            rotationAnchorOriginal = currentPiece.block(currentPiece.anchorIndex(TURN)).clone();
            anchorOriginal = currentPiece.block(currentPiece.anchorIndex(TURN)).clone();
            currentPiece.rotate(TURN);

            for (int anchorX = rotationAnchorOriginal[0] - 1; anchorX <= rotationAnchorOriginal[0] + 1; anchorX++){
                for (int anchorY = rotationAnchorOriginal[1] - 1; anchorY <= rotationAnchorOriginal[1] + 1; anchorY++){
                    counter = 0;

                    for (int i = 0; i < 4; i++){
                        testSquareX = anchorX - currentPiece.block(currentPiece.anchorIndex(LEFT))[0] + currentPiece.block(i)[0];
                        testSquareY = anchorY - currentPiece.block(currentPiece.anchorIndex(LEFT))[1] + currentPiece.block(i)[1];

                        if ((testSquareX < 0) || (testSquareX >= BOARD_WIDTH)){
                            break;
                        }

                        if ((testSquareY < 0) || (testSquareY >= BOARD_HEIGHT)){
                            break;
                        }

                        if (board[testSquareX][testSquareY] == 0){
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
        }

        else {
            throw new IllegalArgumentException("Invalid rotation type.");
        }

        if (found){
            currentPieceLocation[0] = best[0] - currentPiece.block(currentPiece.anchorIndex(direction))[0];
            currentPieceLocation[1] = best[1] - currentPiece.block(currentPiece.anchorIndex(direction))[1];
        }
        else {
            for (int i = 0; i < 3; i++){
                currentPiece.rotate(direction);
            }
        }
    }

    public void placeBlock(){
        int x = currentPieceLocation[0];
        int y = currentPieceLocation[1];
        int squareX;
        int squareY;

        for (int i = 0; i < 4; i++){
            squareX = x + currentPiece.block(i)[0];
            squareY = y + currentPiece.block(i)[1];
            board[squareX][squareY] = currentPiece.typeInt();
        }
    }

    public void nextBlock(){
        currentPieceLocation[1] = BOARD_HEIGHT - 2;
        int queuePosition = bagPosition % 7;

        if (bagPosition < 7){
            currentPiece = new Tetrimino(bag1.piece(queuePosition));
        }
        else if (bagPosition < 14){
            currentPiece = new Tetrimino(bag2.piece(queuePosition));
        }
        else {
            throw new IndexOutOfBoundsException();
        }

        currentPieceLocation = new int[] {START_POSITIONS[currentPiece.typeInt()][0], END_HEIGHT + START_POSITIONS[currentPiece.typeInt()][1] - 1};

        bagPosition++;
        if (bagPosition == 7){
            bag1.shuffle();
        }
        else if (bagPosition == 14){
            bag2.shuffle();
            bagPosition = 0;
        }
    }

    // holds the current piece, puts held peice into current piece if held
    public void holdPiece(){
        if (hold == 0){
            hold = currentPiece.typeInt();
            nextBlock();
        }
        else {
            int temp = hold;
            currentPieceLocation[1] = BOARD_HEIGHT - 2;
            hold = currentPiece.typeInt();
            currentPieceLocation = new int[] {START_POSITIONS[currentPiece.typeInt()][0], END_HEIGHT + START_POSITIONS[currentPiece.typeInt()][1] - 1};
            currentPiece = new Tetrimino(temp);
        }
    }

    public void ghostPiece(){
        int counter;
        int x = currentPieceLocation[0];
        int y;
        int testSquareX;
        int testSquareY;

        for (y = currentPieceLocation[1]; y >= 0; y--){
            counter = 0;

            for (int i = 0; i < 4; i++){
                testSquareX = x + currentPiece.block(i)[0];
                testSquareY = y + currentPiece.block(i)[1];

                if ((testSquareX < 0) || (testSquareX >= BOARD_WIDTH)){
                    continue;
                }

                if ((testSquareY < 0) || (testSquareY >= BOARD_HEIGHT)){
                    continue;
                }

                if (board[testSquareX][testSquareY] == 0){
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
        // TODO will probably cause issues if executed at the same time as lockdown timer starts
        stopwatchLock.reset();
        currentPieceLocation[1] = ghostPieceLocation[1];
        placeBlock();
    }

    public void move(String direction){
        int counter = 0;
        int x = currentPieceLocation[0];
        int y = currentPieceLocation[1];
        int testSquareX;
        int testSquareY;

        if (direction.equals("down")){
            y--;

            for (int i = 0; i < 4; i++){
                testSquareX = x + currentPiece.block(i)[0];
                testSquareY = y + currentPiece.block(i)[1];

                if ((testSquareX < 0) || (testSquareX >= BOARD_WIDTH)){
                    continue;
                }

                if ((testSquareY < 0) || (testSquareY >= BOARD_HEIGHT)){
                    continue;
                }

                if (board[testSquareX][testSquareY] == 0){
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
                testSquareX = x + currentPiece.block(i)[0];
                testSquareY = y + currentPiece.block(i)[1];

                if ((testSquareX < 0) || (testSquareX >= BOARD_WIDTH)){
                    continue;
                }

                if ((testSquareY < 0) || (testSquareY >= BOARD_HEIGHT)){
                    continue;
                }

                if (board[testSquareX][testSquareY] == 0){
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
                testSquareX = x + currentPiece.block(i)[0];
                testSquareY = y + currentPiece.block(i)[1];

                if ((testSquareX < 0) || (testSquareX >= BOARD_WIDTH)){
                    continue;
                }

                if ((testSquareY < 0) || (testSquareY >= BOARD_HEIGHT)){
                    continue;
                }

                if (board[testSquareX][testSquareY] == 0){
                    counter++;
                }
            }

            if (counter == 4){
                currentPieceLocation = new int[] {x,y};
            }
        }
        else {
            throw new IllegalArgumentException("Invalid movement direction.");
        }
    }

    public double distance(int[] point1, int[] point2){
        double square1 = Math.pow(point1[0] - point2[0], 2);
        double square2 = Math.pow(point1[1] - point2[1], 2);
        return Math.sqrt(square1 + square2);
    }
}