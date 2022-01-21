// class GamePanel
// main JPanel where the game runs and is displayed

package src;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable, KeyListener{
    // dimensions for the game panel
    private static final int GAME_WIDTH = 600;
    private static final int GAME_HEIGHT = 420;

    // constants, used as arguments
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String TURN = "turn";
    private static final String DOWN = "down";

    // state the dimensions of the board (in blocks not pixels)
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 25;

    // the game ends if a piece is placed and at least one of its blocks exceeds this height
    private static final int END_HEIGHT = 20;

    // size of a block, in pixels
    private static final int SCALE = 20;

    // time it takes before a piece locks
    private static final int LOCK_TIME = 500; 

    // colors of each tetrimino, access with typeInt()
    private static final Color[] PIECE_COLOUR = new Color[] {
        Color.BLACK,
        Color.YELLOW,
        Color.CYAN,
        Color.MAGENTA,
        new Color(255, 127, 0), // orange
        Color.BLUE,
        Color.GREEN,
        Color.RED
    };

    // relative positions of where each tetrimino spawns, access with typeInt() - 1
    private static final int[][] START_POSITIONS = new int[][] {
        {6,1},
        {6,1},
        {5,1},
        {5,1},
        {5,1},
        {5,2},
        {5,2}
    };

    // the following booleans indicate whether the corresponding key is held down
    private boolean held_Z = false;
    private boolean held_UP = false;
    private boolean held_A = false;
    private boolean held_C = false;
    private boolean held_SPACE = false;
    private boolean held_LEFT = false;
    private boolean held_RIGHT = false;
    private boolean held_DOWN = false;

    // whether hold has been pressed this turn, resets once a piece has been placed
    private boolean hold_pressed = false;

    // the following booleans indicate whether this is the first time the corresponding key is being pressed
    private boolean first_LEFT  = true;
    private boolean first_RIGHT = true;
    private boolean first_DOWN  = true;

    // the following help time the automatic repetition mechanic
    private Stopwatch stopwatchLeft  = new Stopwatch();
    private Stopwatch stopwatchRight = new Stopwatch();
    private Stopwatch stopwatchDown  = new Stopwatch();

    private Stopwatch stopwatchFall = new Stopwatch(); // stopwatch for automatic falling
    private Stopwatch stopwatchLock = new Stopwatch(); // stopwatch for lockdown timer

    // gui objects
    private Thread gameThread;
    private Graphics graphics;
    private Image image;

    private int[][] board = new int[BOARD_WIDTH][BOARD_HEIGHT]; // board, where placed pieces are stored
    private Tetrimino currentPiece; // current piece being controlled by player
    private int[] currentPieceLocation = new int[2]; // location of current piece
    private int[] ghostPieceLocation = new int[2]; // location of ghost piece, projection of current piece

    private double das = 140; // delayed auto shift
    private double arr = 10; // auto repeat rate

    private double fallDelay = 1000; // time it takes for piece to fall automatically

    private int hold = 0; // the tetrimino currently held, 0 for nothing

    // variables for handling piece queue
    private Bag bag1 = new Bag();
    private Bag bag2 = new Bag();
    private int bagPosition = 0;

    private int lines = 0; // stores number of lines cleared
    private int level = 1; // stores current level
    private int score = 0; // stores the score

    // stores whether a back-to-back bonus will be applied when calculating score
    private boolean btb = false;

    // whether the current game has ended
    private boolean end = false;

    // constructor, creates the panel and starts run() as a thread
    public GamePanel(){
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        this.addKeyListener(this);

        gameThread = new Thread(this); 
        gameThread.start();
    }

    @Override
    // required as KeyListener is implemented
    public void keyTyped(KeyEvent e){

    }

    @Override
    // processes key strokes
    // "if (!held_KEY)" statements ensure code only runs once per key press, even if the key is held down
    public void keyPressed(KeyEvent e){
        if (!end){
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
                    stopwatchLeft.restart();
                    move(LEFT);
                    held_LEFT = true;
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_RIGHT){
                if (!held_RIGHT){
                    stopwatchRight.restart();
                    move(RIGHT);
                    held_RIGHT = true;
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_DOWN){
                if (!held_DOWN){
                    stopwatchDown.restart();
                    stopwatchFall.restart();
                    move(DOWN);
                    score++; // softdrop score
                    held_DOWN = true;
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_C){
                if ((!held_C) && (!hold_pressed)){
                    holdPiece();
                    held_C = true;
                    hold_pressed = true;
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_SPACE){
                if (!held_SPACE){
                    hardDrop();
                    held_SPACE = true;
                }
            }
        }
    }

    @Override
    // resets the held_KEY booleans to false and resets stopwatches related to key repeats
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

        if (e.getKeyCode() == KeyEvent.VK_C){
            held_C = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            held_SPACE = false;
        }
    }

    @Override
    // main method where code is run
    public void run(){
        nextPiece();

        while (!end){
            processKeys();
            repaint();

            if (currentPieceLocation[1] == ghostPieceLocation[1]){ // this checks if the bottom of the current piece is in contact
                if (!stopwatchLock.isRunning()){
                    stopwatchLock.restart(); // starts the lockdown stopwatch if it has not been started
                }

                // actions once piece is locked
                if (stopwatchLock.elapsed() >= LOCK_TIME){
                    stopwatchLock.reset();
                    hold_pressed = false;
                    first_DOWN = true;

                    placePiece();
                    nextPiece();
                }
            }
            else {
                stopwatchLock.reset(); // stops the lockdown stopwatch

                // automatically moves the piece down
                if (stopwatchFall.elapsed() >= fallDelay){
                    move(DOWN);
                    stopwatchFall.restart();
                    repaint();
                }
            }
        }

        // game has ended, write high score to file if greater than the high scores on the file
        boolean newFile = true;
        int[] highscores = new int[] {0,0,0,0,0};
        File dataFolder = new File("data");
        File scoreFile = new File("data\\highscores.txt");
        BufferedReader scoreReader = null;

        // makes folder, does nothing if it exists
        try {
            dataFolder.mkdirs();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // makes file, does nothing if it exists
        try {
            newFile = scoreFile.createNewFile();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        // if the file is newly created, write 0s to it
        if (newFile){
            BufferedWriter scoreWriter = null;

            try {
                scoreWriter = new BufferedWriter(new FileWriter(scoreFile, false));
            }
            catch (IOException e){
                e.printStackTrace();
            }

            for (int i = 0; i < 5; i++){
                try {
                    scoreWriter.write(String.valueOf(highscores[i]));
                    scoreWriter.newLine();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }

            try {
                scoreWriter.flush();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        // file already exists, reads data
        else {
            int i = 0;
            String currentLine = null;

            try {
                scoreReader = new BufferedReader(new FileReader(scoreFile));
            }
            catch (IOException e){
                e.printStackTrace();
            }

            while (true){
                try {
                    currentLine = scoreReader.readLine();
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                // stops reading once end of file has been reached
                if (currentLine == null){
                    break;
                }

                highscores[i] = Integer.parseInt(currentLine);
                i++;
            }

            try {
                if (scoreReader != null){
                    scoreReader.close();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 5; i++){
            // checks if current score is higher than any of the high scores
            if (score > highscores[i]){
                // pushes high scores lower than current score back
                for (int j = 4; j > i; j--){
                    highscores[j] = highscores[j - 1];
                }

                // stores current score to high score
                highscores[i] = score;
                break;
            }
        }

        // updates file with new data
        BufferedWriter scoreWriter = null;

        try {
            scoreWriter = new BufferedWriter(new FileWriter(scoreFile, false));
        }
        catch (IOException e){
            e.printStackTrace();
        }

        for (int i = 0; i < 5; i++){
            try {
                scoreWriter.write(String.valueOf(highscores[i]));
                scoreWriter.newLine();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        try {
            scoreWriter.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    // calls draw() and paints the graphics
    public void paint(Graphics g) {
        image = createImage(GAME_WIDTH, GAME_HEIGHT);
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0, 0, this);
    }

    // draws the various parts of the panel
    public void draw(Graphics g){
        // used for real coordinates of blocks and pieces
        int realX;
        int realY;
        
        // draws board outline
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect((GAME_WIDTH - BOARD_WIDTH * SCALE) / 2 - 1, GAME_HEIGHT - END_HEIGHT * SCALE - 2, BOARD_WIDTH * SCALE + 2, (END_HEIGHT + 1) * SCALE + 2);

        // draws placed blocks on the board
        for (int x = 0; x < BOARD_WIDTH; x++){
            for (int y = 0; y < END_HEIGHT; y++){
                g.setColor(PIECE_COLOUR[board[x][y]]);
                realX = (GAME_WIDTH - BOARD_WIDTH * SCALE) / 2 + x * SCALE;
                realY = (GAME_HEIGHT - (y + 1) * SCALE) - 1;
                g.fillRect(realX, realY, SCALE, SCALE);
            }
        }

        // draws ghost piece
        g.setColor(PIECE_COLOUR[currentPiece.typeInt()].darker().darker());

        for (int i = 0; i < 4; i++){
            if (ghostPieceLocation[1] + currentPiece.block(i)[1] >= END_HEIGHT){
                // does not draw blocks of ghost piece if it is above END_HEIGHT
                continue;
            }

            realX = (GAME_WIDTH - BOARD_WIDTH * SCALE) / 2 + (ghostPieceLocation[0] + currentPiece.block(i)[0]) * SCALE;
            realY = (GAME_HEIGHT - (ghostPieceLocation[1] + currentPiece.block(i)[1] + 1) * SCALE) - 1;
            g.fillRect(realX, realY, SCALE, SCALE);
        }

        // draws current piece, this come after ghost piece in case of overlap
        g.setColor(PIECE_COLOUR[currentPiece.typeInt()]);

        for (int i = 0; i < 4; i++){
            if (currentPieceLocation[1] + currentPiece.block(i)[1] >= END_HEIGHT){
                // does not draw blocks of current piece if it is above END_HEIGHT
                continue;
            }

            realX = (GAME_WIDTH - BOARD_WIDTH * SCALE) / 2 + (currentPieceLocation[0] + currentPiece.block(i)[0]) * SCALE;
            realY = (GAME_HEIGHT - (currentPieceLocation[1] + currentPiece.block(i)[1] + 1) * SCALE) - 1;
            g.fillRect(realX, realY, SCALE, SCALE);
        }

        // draws held piece
        if (hold != 0){
            Tetrimino holdTetrimino = new Tetrimino(hold);

            g.setColor(PIECE_COLOUR[hold]);
            
            for (int i = 0; i < 4; i++){
                // draws block
                realX = (GAME_WIDTH - BOARD_WIDTH * SCALE) / 2 - 80 + (holdTetrimino.block(i)[0] + START_POSITIONS[hold - 1][0] - 5) * SCALE;
                realY = (40 - (holdTetrimino.block(i)[1] + START_POSITIONS[hold - 1][1] - 1) * SCALE);
                g.fillRect(realX, realY, SCALE, SCALE);
            }
        }

        // draws queue
        Tetrimino queueTetrimino;
        int queuePosition;

        for (int i = 0; i < 5; i++){
            queuePosition = (bagPosition + i) % 14;

            // calculates which queue bag to use
            if (queuePosition < 7){
                queueTetrimino = new Tetrimino(bag1.piece(queuePosition));
            }
            else if (queuePosition < 14){
                queueTetrimino = new Tetrimino(bag2.piece(queuePosition % 7));
            }
            else {
                throw new IndexOutOfBoundsException();
            }

            g.setColor(PIECE_COLOUR[queueTetrimino.typeInt()]);
            
            for (int j = 0; j < 4; j++){
                realX = (GAME_WIDTH + BOARD_WIDTH * SCALE) / 2 + 40 + (queueTetrimino.block(j)[0] + START_POSITIONS[queueTetrimino.typeInt() - 1][0] - 5)* SCALE;
                realY = (- 30 + 70 * (i + 1) - (queueTetrimino.block(j)[1] + START_POSITIONS[queueTetrimino.typeInt() - 1][1] - 1) * SCALE);
                g.fillRect(realX, realY, SCALE, SCALE);
            }
        }

        // text
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Consolas", Font.PLAIN, 20));

        // draws level
        g.drawString("LEVEL", (GAME_WIDTH - BOARD_WIDTH * SCALE) / 4, GAME_HEIGHT - 140);
        g.drawString(String.valueOf(level), (GAME_WIDTH - BOARD_WIDTH * SCALE) / 4, GAME_HEIGHT - 120);

        // draws score
        String scoreString = "";

        // fills up score with leading zeroes
        // if anyone gets above 9999999 i will be impressed
        for (int i = 0; i < 7 - String.valueOf(score).length(); i++){
            scoreString += "0";
        }

        scoreString += String.valueOf(score);

        g.drawString("SCORE", (GAME_WIDTH - BOARD_WIDTH * SCALE) / 4, GAME_HEIGHT - 90);
        g.drawString(scoreString, (GAME_WIDTH - BOARD_WIDTH * SCALE) / 4, GAME_HEIGHT - 70);

        // draws cleared lines
        g.drawString("LINES", (GAME_WIDTH - BOARD_WIDTH * SCALE) / 4, GAME_HEIGHT - 40);
        g.drawString(String.valueOf(lines), (GAME_WIDTH - BOARD_WIDTH * SCALE) / 4, GAME_HEIGHT - 20);

        if (end){
            // what is drawn when game ends
            g.setColor(Color.WHITE);
            g.setFont(new Font("Consolas", Font.PLAIN, 100));
            g.drawString("GAME", 193, GAME_HEIGHT / 2);
            g.drawString("END", 213, GAME_HEIGHT / 2 + 100);
        }
    }

    // rotates and updates position if possible
    private void rotate(String direction){
        int[] rotationAnchorOriginal = new int[2];
        int[] anchorOriginal = new int[2];
        int counter;
        int testSquareX = 0;
        int testSquareY = 0;
        int[] best = new int[2];
        double bestDistance = 100;
        boolean found = false;

        if (direction.equals("left")){
            rotationAnchorOriginal[0] = currentPiece.block(currentPiece.anchorIndex(LEFT))[0] + currentPieceLocation[0];
            rotationAnchorOriginal[1] = currentPiece.block(currentPiece.anchorIndex(LEFT))[1] + currentPieceLocation[1];
            anchorOriginal[0] = currentPiece.block(currentPiece.anchorIndex(TURN))[0] + currentPieceLocation[0];
            anchorOriginal[1] = currentPiece.block(currentPiece.anchorIndex(TURN))[1] + currentPieceLocation[1];
            currentPiece.rotate(LEFT);

            // searches 5x5 area around the original rotation anchor position for a valid location to place the new rotation anchor
            for (int anchorX = rotationAnchorOriginal[0] - 2; anchorX <= rotationAnchorOriginal[0] + 2; anchorX++){
                for (int anchorY = rotationAnchorOriginal[1] - 2; anchorY <= rotationAnchorOriginal[1] + 2; anchorY++){
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
                        // all 4 blocks are valid, a possible position is found
                        if (!found){
                            found = true;
                            best[0] = anchorX;
                            best[1] = anchorY;
                            bestDistance = distance(best, anchorOriginal);
                        }
                        else {
                            if (distance(new int[] {anchorX,anchorY}, anchorOriginal) < bestDistance){
                                // update best if the distance between anchors is less than the stored best distance
                                best[0] = anchorX;
                                best[1] = anchorY;
                                bestDistance = distance(best, anchorOriginal);
                            }
                            else if (distance(new int[] {anchorX,anchorY}, anchorOriginal) == bestDistance){
                                // if equal distances
                                if (anchorY < best[1]){
                                    // update best if new anchor location is lower than stored best anchor location
                                    best[0] = anchorX;
                                    best[1] = anchorY;
                                    bestDistance = distance(best, anchorOriginal);
                                }
                                else if (anchorY == best[1]){
                                    // if equal height
                                    if (anchorX > best[0]){
                                        // update best if new anchor location is to the right of stored best anchor location
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
            rotationAnchorOriginal[0] = currentPiece.block(currentPiece.anchorIndex(RIGHT))[0] + currentPieceLocation[0];
            rotationAnchorOriginal[1] = currentPiece.block(currentPiece.anchorIndex(RIGHT))[1] + currentPieceLocation[1];
            anchorOriginal[0] = currentPiece.block(currentPiece.anchorIndex(TURN))[0] + currentPieceLocation[0];
            anchorOriginal[1] = currentPiece.block(currentPiece.anchorIndex(TURN))[1] + currentPieceLocation[1];
            currentPiece.rotate(RIGHT);

            for (int anchorX = rotationAnchorOriginal[0] - 2; anchorX <= rotationAnchorOriginal[0] + 2; anchorX++){
                for (int anchorY = rotationAnchorOriginal[1] - 2; anchorY <= rotationAnchorOriginal[1] + 2; anchorY++){
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
                            if (distance(new int[] {anchorX,anchorY}, anchorOriginal) < bestDistance){
                                best[0] = anchorX;
                                best[1] = anchorY;
                                bestDistance = distance(best, anchorOriginal);
                            }
                            else if (distance(new int[] {anchorX,anchorY}, anchorOriginal) == bestDistance){
                                if (anchorY < best[1]){
                                    best[0] = anchorX;
                                    best[1] = anchorY;
                                    bestDistance = distance(best, anchorOriginal);
                                }
                                else if (anchorY == best[1]){
                                    if (anchorX < best[0]){
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
            rotationAnchorOriginal[0] = currentPiece.block(currentPiece.anchorIndex(TURN))[0] + currentPieceLocation[0];
            rotationAnchorOriginal[1] = currentPiece.block(currentPiece.anchorIndex(TURN))[1] + currentPieceLocation[1];
            anchorOriginal[0] = currentPiece.block(currentPiece.anchorIndex(TURN))[0] + currentPieceLocation[0];
            anchorOriginal[1] = currentPiece.block(currentPiece.anchorIndex(TURN))[1] + currentPieceLocation[1];
            currentPiece.rotate(TURN);

            for (int anchorX = rotationAnchorOriginal[0] - 2; anchorX <= rotationAnchorOriginal[0] + 2; anchorX++){
                for (int anchorY = rotationAnchorOriginal[1] - 2; anchorY <= rotationAnchorOriginal[1] + 2; anchorY++){
                    counter = 0;

                    for (int i = 0; i < 4; i++){
                        testSquareX = anchorX - currentPiece.block(currentPiece.anchorIndex(TURN))[0] + currentPiece.block(i)[0];
                        testSquareY = anchorY - currentPiece.block(currentPiece.anchorIndex(TURN))[1] + currentPiece.block(i)[1];

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
                            if (distance(new int[] {anchorX,anchorY}, anchorOriginal) < bestDistance){
                                best[0] = anchorX;
                                best[1] = anchorY;
                                bestDistance = distance(best, anchorOriginal);
                            }
                            else if (distance(new int[] {anchorX,anchorY}, anchorOriginal) == bestDistance){
                                if (anchorY < best[1]){
                                    best[0] = anchorX;
                                    best[1] = anchorY;
                                    bestDistance = distance(best, anchorOriginal);
                                }
                                else if (anchorY == best[1]){
                                    if (anchorX > best[0]){
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
            // updates position
            currentPieceLocation[0] = best[0] - currentPiece.block(currentPiece.anchorIndex(direction))[0];
            currentPieceLocation[1] = best[1] - currentPiece.block(currentPiece.anchorIndex(direction))[1];
            stopwatchLock.reset();
            ghostPiece();
            repaint();
        }
        else {
            // rotates current piece back to original if no rotation found
            for (int i = 0; i < 3; i++){
                currentPiece.rotate(direction);
            }
        }
    }

    // calculates and updates the location of the ghost piece
    private void ghostPiece(){
        int counter;
        int x = currentPieceLocation[0];
        int y;
        int testSquareX;
        int testSquareY;

        // starts searching from the current location and moves down
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

        ghostPieceLocation = new int[] {x,y + 1};
    }

    // holds the current piece, puts held piece into current piece if held
    private void holdPiece(){
        if (hold == 0){
            hold = currentPiece.typeInt();
            nextPiece();
        }
        else {
            currentPieceLocation[1] = BOARD_HEIGHT - 2; // moves current piece out of visible area for processing

            // swaps hold and current
            int temp = hold;
            hold = currentPiece.typeInt();
            currentPieceLocation = new int[] {START_POSITIONS[currentPiece.typeInt() - 1][0], END_HEIGHT + START_POSITIONS[currentPiece.typeInt() - 1][1] - 1};
            currentPiece = new Tetrimino(temp);
        }

        ghostPiece();
        repaint();
    }

        // stores the piece's blocks to the board, calls matchPatterns() and updateLevel()
        private void placePiece(){
            int x = currentPieceLocation[0];
            int y = currentPieceLocation[1];
            int squareX;
            int squareY;
    
            for (int i = 0; i < 4; i++){
                squareX = x + currentPiece.block(i)[0];
                squareY = y + currentPiece.block(i)[1];
    
                if (squareY >= END_HEIGHT){
                    // ends the game if the placed piece has blocks greater than the end height
                    end = true;
                }
    
                board[squareX][squareY] = currentPiece.typeInt();
            }
    
            matchPatterns();
            updateLevel();
            repaint();
        }
    
        // sets current piece to the next piece in the queue
        private void nextPiece(){
            currentPieceLocation[1] = BOARD_HEIGHT - 2; // moves current piece out of visible area for processing
            int queuePosition = bagPosition % 7;
    
            // determines which bag to use
            if (bagPosition < 7){
                currentPiece = new Tetrimino(bag1.piece(queuePosition));
            }
            else if (bagPosition < 14){
                currentPiece = new Tetrimino(bag2.piece(queuePosition));
            }
            else {
                throw new IndexOutOfBoundsException();
            }
    
            // sets piece location to spawn location
            currentPieceLocation = new int[] {START_POSITIONS[currentPiece.typeInt() - 1][0] - 1, END_HEIGHT + START_POSITIONS[currentPiece.typeInt() - 1][1] - 1};
    
            bagPosition++;
    
            // reshuffles bag if end of bag has been reached
            if (bagPosition == 7){
                bag1.shuffle();
            }
            else if (bagPosition == 14){
                bag2.shuffle();
                bagPosition = 0;
            }
    
            move(DOWN);
            stopwatchFall.restart();
    
            ghostPiece();
            repaint();
        }

    // matches patterns for t-spins, tetrises, and multi-line clears
    // also clears the lines in board
    private void matchPatterns(){
        int squareCounter;
        int lineCounter = 0;
        int x;
        int y;
        int tSpinCounter = 0;
        int tSpinMiniCounter = 0;
        boolean tSpin;
        boolean temp = btb; // stores if there is an ongoing btb combo
        int dropScore = 0;

        if (currentPiece.typeString().equals("t")){ // if current piece is t-piece, check for t-spins
            x = currentPieceLocation[0];
            y = currentPieceLocation[1];

            // checks the four corners of the 3x3 area around the t-piece to see if t-spin condition is satisfied
            if (currentPiece.facing() == 0){
                if ((x + 1 >= BOARD_WIDTH) || (y + 1 >= BOARD_HEIGHT)){
                    tSpinCounter++;
                }
                else if (board[x + 1][y + 1] != 0){
                    tSpinCounter++;
                }

                if ((x - 1 < 0) || (y + 1 >= BOARD_HEIGHT)){
                    tSpinCounter++;
                }
                else if (board[x - 1][y + 1] != 0){
                    tSpinCounter++;
                }

                if ((x + 1 >= BOARD_WIDTH) || (y - 1 < 0)){
                    tSpinMiniCounter++;
                }
                else if (board[x + 1][y - 1] != 0){
                    tSpinMiniCounter++;
                }

                if ((x - 1 < 0) || (y - 1 < 0)){
                    tSpinMiniCounter++;
                }
                else if (board[x - 1][y - 1] != 0){
                    tSpinMiniCounter++;
                }
            }
            else if (currentPiece.facing() == 1){
                if ((x + 1 >= BOARD_WIDTH) || (y + 1 >= BOARD_HEIGHT)){
                    tSpinCounter++;
                }
                else if (board[x + 1][y + 1] != 0){
                    tSpinCounter++;
                }

                if ((x - 1 < 0) || (y + 1 >= BOARD_HEIGHT)){
                    tSpinMiniCounter++;
                }
                else if (board[x - 1][y + 1] != 0){
                    tSpinMiniCounter++;
                }

                if ((x + 1 >= BOARD_WIDTH) || (y - 1 < 0)){
                    tSpinCounter++;
                }
                else if (board[x + 1][y - 1] != 0){
                    tSpinCounter++;
                }

                if ((x - 1 < 0) || (y - 1 < 0)){
                    tSpinMiniCounter++;
                }
                else if (board[x - 1][y - 1] != 0){
                    tSpinMiniCounter++;
                }
            }
            else if (currentPiece.facing() == 2){
                if ((x + 1 >= BOARD_WIDTH) || (y + 1 >= BOARD_HEIGHT)){
                    tSpinMiniCounter++;
                }
                else if (board[x + 1][y + 1] != 0){
                    tSpinMiniCounter++;
                }

                if ((x - 1 < 0) || (y + 1 >= BOARD_HEIGHT)){
                    tSpinMiniCounter++;
                }
                else if (board[x - 1][y + 1] != 0){
                    tSpinMiniCounter++;
                }

                if ((x + 1 >= BOARD_WIDTH) || (y - 1 < 0)){
                    tSpinCounter++;
                }
                else if (board[x + 1][y - 1] != 0){
                    tSpinCounter++;
                }

                if ((x - 1 < 0) || (y - 1 < 0)){
                    tSpinCounter++;
                }
                else if (board[x - 1][y - 1] != 0){
                    tSpinCounter++;
                }
            }
            else if (currentPiece.facing() == 3){
                if ((x + 1 >= BOARD_WIDTH) || (y + 1 >= BOARD_HEIGHT)){
                    tSpinMiniCounter++;
                }
                else if (board[x + 1][y + 1] != 0){
                    tSpinMiniCounter++;
                }

                if ((x - 1 < 0) || (y + 1 >= BOARD_HEIGHT)){
                    tSpinCounter++;
                }
                else if (board[x - 1][y + 1] != 0){
                    tSpinCounter++;
                }

                if ((x + 1 >= BOARD_WIDTH) || (y - 1 < 0)){
                    tSpinMiniCounter++;
                }
                else if (board[x + 1][y - 1] != 0){
                    tSpinMiniCounter++;
                }

                if ((x - 1 < 0) || (y - 1 < 0)){
                    tSpinCounter++;
                }
                else if (board[x - 1][y - 1] != 0){
                    tSpinCounter++;
                }
            }
        }

        // clear lines if possible
        for (y = 0; y < END_HEIGHT; y++){
            squareCounter = 0; // counts empty squares in a row

            for (x = 0; x < BOARD_WIDTH; x++){
                if (board[x][y] == 0){
                    squareCounter++;
                }
            }

            if (squareCounter == 0){
                // clear the line, shift the rest of the board down
                lineCounter++;

                for (int i = 0; i < BOARD_WIDTH; i++){
                    for (int j = y; j < END_HEIGHT; j++){
                        board[i][j] = board[i][j + 1];
                    }
                }

                y--;
            }
        }

        // boolean for if t-spin was achieved
        tSpin = (tSpinCounter == 2) && (tSpinMiniCounter >= 1);

        if (tSpin){
            dropScore = (lineCounter + 1) * 400; // t-spin score calculation
            btb = true; // begins/continues btb combo
        }
        else {
            if (lineCounter == 1){
                dropScore = 100;
                btb = false; // breaks btb combo
            }
            else if (lineCounter == 2){
                dropScore = 300;
                btb = false; // breaks btb combo
            }
            else if (lineCounter == 3){
                dropScore = 500;
                btb = false; // breaks btb combo
            }
            else if (lineCounter == 4){
                dropScore = 800;
                btb = true; // begins/continues btb combo
            }
        }

        dropScore *= level; // apply level multiplier

        if (temp && btb){
            // award back-to-back bonus
            dropScore = (int)(dropScore * 3 / 2);
        }

        score += dropScore;
        lines += lineCounter;
    }

    // updates the level and the falling speed
    private void updateLevel(){
        if (level * 10 <= lines){
            level = (int)(lines / 10);
        }

        fallDelay = (Math.pow((0.8 - (level -  1) * 0.007), level - 1)) * 1000;
    }

    // instantly drops the piece down
    private void hardDrop(){
        stopwatchLock.reset();
        score += (currentPieceLocation[1] - ghostPieceLocation[1]) * 2; // hard drop score bonus

        currentPieceLocation[1] = ghostPieceLocation[1];
        stopwatchLock.reset();
        hold_pressed = false;
        first_DOWN = true;

        placePiece();
        nextPiece();
    }

    // moves piece left, right, or down, if possible
    private void move(String direction){
        int counter = 0;
        int x = currentPieceLocation[0];
        int y = currentPieceLocation[1];
        int testSquareX;
        int testSquareY;

        if (direction.equals("down")){
            y--;

            // tests each of the four blocks to see if movement is possible
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
        }
        else {
            throw new IllegalArgumentException("Invalid movement direction.");
        }

        // updates location if moved
        if (counter == 4){
            stopwatchLock.reset();
            currentPieceLocation = new int[] {x,y};
        }

        ghostPiece(); // recalculates ghost piece position
    }

    // processes keys, if they are held for some time, repeat the actions
    private void processKeys(){
        if (held_LEFT){
            if (first_LEFT && (stopwatchLeft.elapsed() >= das)){
                // initial delay
                stopwatchLeft.restart();
                move(LEFT);
                first_LEFT = false;
            }
            else if ((!first_LEFT) && (stopwatchLeft.elapsed() >= arr)){
                // automatic repetition
                stopwatchLeft.restart();
                move(LEFT);
            }
        }

        if (held_RIGHT){
            if (first_RIGHT && (stopwatchRight.elapsed() >= das)){
                stopwatchRight.restart();
                move(RIGHT);
                first_RIGHT = false;
            }
            else if ((!first_RIGHT) && (stopwatchRight.elapsed() >= arr)){
                stopwatchRight.restart();
                move(RIGHT);
            }
        }

        if (held_DOWN){
            if (first_DOWN && (stopwatchDown.elapsed() >= das)){
                stopwatchDown.restart();
                stopwatchFall.restart();
                move(DOWN);
                score++;
                first_DOWN = false;
            }
            else if ((!first_DOWN) && (stopwatchDown.elapsed() >= arr)){
                stopwatchDown.restart();
                stopwatchFall.restart();
                move(DOWN);
                score++;
            }
        }
    }

    // calculates the distance between two coordinate points
    private double distance(int[] point1, int[] point2){
        double square1 = Math.pow(point1[0] - point2[0], 2);
        double square2 = Math.pow(point1[1] - point2[1], 2);
        return Math.sqrt(square1 + square2);
    }
}