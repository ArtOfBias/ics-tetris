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
    public static final int GAME_WIDTH = 600;
    public static final int GAME_HEIGHT = 420;

    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String TURN = "turn";
    public static final String DOWN = "down";

    public static final int BOARD_WIDTH = 10;
    public static final int END_HEIGHT = 20;
    public static final int BOARD_HEIGHT = 25;

    public static final int SCALE = 20;

    public static final int LOCK_TIME = 500;

    public static final Color[] PIECE_COLOUR = new Color[] {
        Color.BLACK,
        Color.YELLOW,
        Color.CYAN,
        Color.MAGENTA,
        new Color(255, 127, 0), // orange
        Color.BLUE,
        Color.GREEN,
        Color.RED
    };

    public static final int[][] START_POSITIONS = new int[][] {
        {6,1},
        {6,1},
        {5,1},
        {5,1},
        {5,1},
        {5,2},
        {5,2}
    };

    // the following booleans indicate whether the corresponding key is held down
    public boolean held_Z = false;
    public boolean held_UP = false;
    public boolean held_A = false;
    public boolean held_C = false;
    public boolean held_SPACE = false;
    public boolean held_LEFT = false;
    public boolean held_RIGHT = false;
    public boolean held_DOWN = false;

    // whether hold has been pressed this turn, resets once a peice has been placed
    public boolean hold_pressed = false;

    // the following booleans indicate whether this is the first time the corresponding is being pressed
    public boolean first_LEFT  = true;
    public boolean first_RIGHT = true;
    public boolean first_DOWN  = true;

    // the following help time the automatic repetition mechanic
    public Stopwatch stopwatchLeft  = new Stopwatch();
    public Stopwatch stopwatchRight = new Stopwatch();
    public Stopwatch stopwatchDown  = new Stopwatch();

    public Stopwatch stopwatchFall = new Stopwatch(); // stopwatch for automatic falling
    public Stopwatch stopwatchLock = new Stopwatch(); // stopwatch for lockdown timer

    public Thread gameThread;
    public Graphics graphics;
    public Image image;

    public int[][] board = new int[BOARD_WIDTH][BOARD_HEIGHT]; // board, where placed pieces are stored
    public Tetrimino currentPiece; // current piece being controlled by player
    public int[] currentPieceLocation = new int[2]; // location of current piece
    public int[] ghostPieceLocation = new int[2]; // location of ghost piece, projection of current piece

    public double das = 140; // delayed auto shift
    public double arr = 10; // auto repeat rate

    public double fallDelay = 1000; // time it takes for piece to fall automatically

    public int hold = 0;

    // variables for handling piece queue
    public Bag bag1 = new Bag();
    public Bag bag2 = new Bag();
    public int bagPosition = 0;

    // stores number of lines cleared
    public int lines = 0;

    // stores current level
    public int level = 1;

    // stores the score
    public int score = 0;

    // stores whether a back-to-back bonus will be applied
    public boolean btb = false;

    // TODO unfinished
    public boolean end = false;

    public GamePanel(){
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        this.addKeyListener(this);

        gameThread = new Thread(this); 
        gameThread.start();

        /*try {
            Robot r = new Robot();
            r.keyPress(KeyEvent.VK_DOWN);
            //r.keyRelease(KeyEvent.VK_DOWN);
        }
        catch (AWTException e) {
            e.printStackTrace();
        }*/
        //^ code didn't work, but may be useful
    }

    @Override
    public void keyTyped(KeyEvent e){

    }

    @Override
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
                    score++;
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
    public void run(){
        nextPiece();

        while (!end){
            processKeys();
            repaint();

            if (currentPieceLocation[1] == ghostPieceLocation[1]){
                if (!stopwatchLock.isRunning()){
                    stopwatchLock.restart();
                }

                // actions once block is locked
                if (stopwatchLock.elapsed() >= LOCK_TIME){
                    stopwatchLock.reset();
                    hold_pressed = false;
                    first_DOWN = true;

                    placePiece();
                    nextPiece();
                }
            }
            else {
                stopwatchLock.reset();

                if (stopwatchFall.elapsed() >= fallDelay){
                    move(DOWN);
                    stopwatchFall.restart();
                    repaint();
                }
            }
        }

        boolean newFile = true;
        int[] highscores = new int[] {0,0,0,0,0};
        File dataFolder = new File("data");
        File scoreFile = new File("data\\highscores.txt");
        BufferedReader scoreReader = null;

        try {
            dataFolder.mkdirs();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        try {
            newFile = scoreFile.createNewFile();
        }
        catch (IOException e){
            e.printStackTrace();
        }

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
            if (score > highscores[i]){
                for (int j = 4; j > i; j--){
                    highscores[j] = highscores[j - 1];
                }

                highscores[i] = score;
                break;
            }
        }

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
    public void paint(Graphics g) {
        image = createImage(GAME_WIDTH, GAME_HEIGHT);
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0, 0, this);
    }

    public void draw(Graphics g){
        // draws board outline
        int realX;
        int realY;

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

        // draws ghost peice
        g.setColor(PIECE_COLOUR[currentPiece.typeInt()].darker().darker());

        for (int i = 0; i < 4; i++){
            if (ghostPieceLocation[1] + currentPiece.block(i)[1] >= END_HEIGHT){
                continue;
            }
            realX = (GAME_WIDTH - BOARD_WIDTH * SCALE) / 2 + (ghostPieceLocation[0] + currentPiece.block(i)[0]) * SCALE;
            realY = (GAME_HEIGHT - (ghostPieceLocation[1] + currentPiece.block(i)[1] + 1) * SCALE) - 1;
            g.fillRect(realX, realY, SCALE, SCALE);
        }

        // drawscurrent piece, this come after ghost piece in case of overlap
        g.setColor(PIECE_COLOUR[currentPiece.typeInt()]);

        for (int i = 0; i < 4; i++){
            if (currentPieceLocation[1] + currentPiece.block(i)[1] >= END_HEIGHT){
                continue;
            }
            realX = (GAME_WIDTH - BOARD_WIDTH * SCALE) / 2 + (currentPieceLocation[0] + currentPiece.block(i)[0]) * SCALE;
            realY = (GAME_HEIGHT - (currentPieceLocation[1] + currentPiece.block(i)[1] + 1) * SCALE) - 1;
            g.fillRect(realX, realY, SCALE, SCALE);
        }

        // drawsheld piece
        if (hold != 0){
            Tetrimino holdTetrimino = new Tetrimino(hold);

            g.setColor(PIECE_COLOUR[hold]);
            
            for (int i = 0; i < 4; i++){
                realX = (GAME_WIDTH - BOARD_WIDTH * SCALE) / 2 - 80 + (holdTetrimino.block(i)[0] + START_POSITIONS[hold - 1][0] - 5)* SCALE;
                realY = (40 - (holdTetrimino.block(i)[1] + START_POSITIONS[hold - 1][1] - 1) * SCALE);
                g.fillRect(realX, realY, SCALE, SCALE);
            }
        }

        // draws queue
        Tetrimino queueTetrimino;
        int queuePosition;

        for (int i = 0; i < 5; i++){
            queuePosition = (bagPosition + i) % 14;

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
            g.setColor(Color.WHITE);
            g.setFont(new Font("Consolas", Font.PLAIN, 100));
            g.drawString("GAME", 193, GAME_HEIGHT / 2);
            g.drawString("END", 213, GAME_HEIGHT / 2 + 100);
        }
    }

    public void rotate(String direction){
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
            currentPieceLocation[0] = best[0] - currentPiece.block(currentPiece.anchorIndex(direction))[0];
            currentPieceLocation[1] = best[1] - currentPiece.block(currentPiece.anchorIndex(direction))[1];
            stopwatchLock.reset();
            ghostPiece();
            repaint();
        }
        else if (direction.equals("turn")){
            currentPiece.rotate(direction);
        }
        else {
            for (int i = 0; i < 3; i++){
                // turn bug here?
                currentPiece.rotate(direction);
            }
        }
    }

    public void placePiece(){
        int x = currentPieceLocation[0];
        int y = currentPieceLocation[1];
        int squareX;
        int squareY;

        for (int i = 0; i < 4; i++){
            squareX = x + currentPiece.block(i)[0];
            squareY = y + currentPiece.block(i)[1];

            if (squareY >= END_HEIGHT){
                end = true;
            }

            board[squareX][squareY] = currentPiece.typeInt();
        }

        matchPatterns();
        updateLevel();
        repaint();
    }

    public void nextPiece(){
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

        currentPieceLocation = new int[] {START_POSITIONS[currentPiece.typeInt() - 1][0] - 1, END_HEIGHT + START_POSITIONS[currentPiece.typeInt() - 1][1] - 1};

        bagPosition++;
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

    // holds the current piece, puts held peice into current piece if held
    public void holdPiece(){
        if (hold == 0){
            hold = currentPiece.typeInt();
            nextPiece();
        }
        else {
            int temp = hold;
            currentPieceLocation[1] = BOARD_HEIGHT - 2;
            hold = currentPiece.typeInt();
            currentPieceLocation = new int[] {START_POSITIONS[currentPiece.typeInt() - 1][0], END_HEIGHT + START_POSITIONS[currentPiece.typeInt() - 1][1] - 1};
            currentPiece = new Tetrimino(temp);
        }

        ghostPiece();
        repaint();
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

        ghostPieceLocation = new int[] {x,y + 1};
    }

    public void matchPatterns(){
        int squareCounter;
        int lineCounter = 0;
        int x;
        int y;
        int tSpinCounter = 0;
        int tSpinMiniCounter = 0;
        boolean tSpin;
        boolean temp = btb;
        int dropScore = 0;

        if (currentPiece.typeString().equals("t")){
            x = currentPieceLocation[0];
            y = currentPieceLocation[1];

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

        for (y = 0; y < END_HEIGHT; y++){
            squareCounter = 0;
            
            for (x = 0; x < BOARD_WIDTH; x++){
                if (board[x][y] == 0){
                    squareCounter++;
                }
            }
            
            if (squareCounter == 0){
                lineCounter++;
                
                for (int i = 0; i < BOARD_WIDTH; i++){
                    for (int j = y; j < END_HEIGHT; j++){
                        board[i][j] = board[i][j + 1];
                    }
                }
                
                y--;
            }
        }

        tSpin = (tSpinCounter == 2) && (tSpinMiniCounter >= 1);

        if (tSpin){
            dropScore = (lineCounter + 1) * 400;
            btb = true;
        }
        else {
            if (lineCounter == 1){
                dropScore = 100;
                btb = false;
            }
            else if (lineCounter == 2){
                dropScore = 300;
                btb = false;
            }
            else if (lineCounter == 3){
                dropScore = 500;
                btb = false;
            }
            else if (lineCounter == 4){
                dropScore = 800;
                btb = true;
            }
        }

        dropScore *= level;

        if (temp && btb){
            dropScore = (int)(dropScore * 3 / 2);
        }

        score += dropScore;
        lines += lineCounter;
    }

    public void updateLevel(){
        if (level * 10 <= lines){
            level = (int)(lines / 10);
        }

        fallDelay = (Math.pow((0.8 - (level -  1) * 0.007), level - 1)) * 1000;
    }

    public void hardDrop(){
        // TODO will probably cause issues if executed at the same time as lockdown timer starts
        // no bugs so far
        stopwatchLock.reset();
        score += (currentPieceLocation[1] - ghostPieceLocation[1]) * 2;

        currentPieceLocation[1] = ghostPieceLocation[1];
        stopwatchLock.reset();
        hold_pressed = false;
        first_DOWN = true;

        placePiece();
        nextPiece();
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
                stopwatchLock.reset();
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
                stopwatchLock.reset();
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
                stopwatchLock.reset();
                currentPieceLocation = new int[] {x,y};
            }
        }
        else {
            throw new IllegalArgumentException("Invalid movement direction.");
        }

        ghostPiece();
    }

    public void processKeys(){
        if (held_LEFT){
            if (first_LEFT && (stopwatchLeft.elapsed() >= das)){
                stopwatchLeft.restart();
                move(LEFT);
                first_LEFT = false;
            }
            else if ((!first_LEFT) && (stopwatchLeft.elapsed() >= arr)){
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

    // TODO organise functions

    public double distance(int[] point1, int[] point2){
        double square1 = Math.pow(point1[0] - point2[0], 2);
        double square2 = Math.pow(point1[1] - point2[1], 2);
        return Math.sqrt(square1 + square2);
    }
}