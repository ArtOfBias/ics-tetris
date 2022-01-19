import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable, KeyListener{
    public static final int GAME_WIDTH = 600;
    public static final int GAME_HEIGHT = 500;

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
        {5,1},
        {6,1},
        {5,1},
        {5,1},
        {5,1},
        {5,2},
        {5,1}
    };

    public double das = 140; // delayed auto shift
    public double arr = 10; // auto repeat rate

    public double fallDelay = 1000; // time it takes for piece to fall automatically
    // TODO this should be dynamic and change with levels
    // TODO levels

    public int[][] board = new int[BOARD_WIDTH][BOARD_HEIGHT]; // board, where placed pieces are stored
    public Tetrimino currentPiece; // current piece being controlled by player
    public int[] currentPieceLocation = new int[2]; // location of current piece
    public int[] ghostPieceLocation = new int[2]; // location of ghost piece, projection of current piece

    public int hold = 0;

    // variables for handling piece queue
    public Bag bag1 = new Bag();
    public Bag bag2 = new Bag();
    public int bagPosition = 0;

    // the following booleans indicate whether the corresponding key is held down
    public boolean held_Z = false;
    public boolean held_UP = false;
    public boolean held_A = false;
    public boolean held_C = false;
    public boolean held_SPACE = false;
    public boolean held_LEFT = false;
    public boolean held_RIGHT = false;
    public boolean held_DOWN = false;

    public boolean hold_pressed = false;

    // TODO: use list instead?
    // the problem with java is it has no dictionaries, making code that accesses this as a list very hard to read

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

    public GamePanel(){
        this.setFocusable(true);
        this.addKeyListener(this);
        this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        this.addKeyListener(this);

        gameThread = new Thread(this); 
        gameThread.start();
    }

    @Override
    public void keyTyped(KeyEvent e){

    }

    // TODO probably want to use threads for this, rethink how this is done
    // alternatively, function with a bunch of if statements that runs everytime (more resource-heavy?)
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
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            if (!held_RIGHT){
                stopwatchRight.start();
                move(RIGHT);
                held_RIGHT = true;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN){
            if (!held_DOWN){
                stopwatchDown.start();
                stopwatchFall.restart();
                move(DOWN);
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
        // TODO current code only supports one playthrough
        boolean end = false;
        nextBlock();

        while (true){
            processKeys();
            repaint();

            if (currentPieceLocation[1] == ghostPieceLocation[1]){
                if (!stopwatchLock.isRunning()){
                    stopwatchLock.start();
                }

                // actions once block is locked
                if (stopwatchLock.elapsed() >= 2000){
                    stopwatchLock.reset();
                    hold_pressed = false;
                    first_DOWN = true;

                    placeBlock();
                    matchPatterns();
                    nextBlock();

                    for (int x = 0; x < BOARD_WIDTH; x++){
                        if (board[x][END_HEIGHT] != 0){
                            repaint();
                            end = true;
                        }
                    }

                    if (end){
                        break;
                    }
                }
            }
            else {
                // TODO maybe pause instead of reset
                stopwatchLock.reset();

                if (stopwatchFall.elapsed() >= fallDelay){
                    move(DOWN);
                    stopwatchFall.restart();
                    repaint();
                }
            }
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
        int realX;
        int realY;

        for (int x = 0; x < BOARD_WIDTH; x++){
            for (int y = 0; y < END_HEIGHT; y++){
                g.setColor(PIECE_COLOUR[board[x][y]]);
                realX = (GAME_WIDTH - BOARD_WIDTH * SCALE) / 2 + x * SCALE;
                realY = (GAME_HEIGHT - (y + 1) * SCALE);
                g.fillRect(realX, realY, SCALE, SCALE);
            }
        }

        g.setColor(PIECE_COLOUR[currentPiece.typeInt()].darker().darker());

        for (int i = 0; i < 4; i++){
            if (ghostPieceLocation[1] + currentPiece.block(i)[1] >= END_HEIGHT){
                continue;
            }
            realX = (GAME_WIDTH - BOARD_WIDTH * SCALE) / 2 + (ghostPieceLocation[0] + currentPiece.block(i)[0]) * SCALE;
            realY = (GAME_HEIGHT - (ghostPieceLocation[1] + currentPiece.block(i)[1] + 1) * SCALE);
            g.fillRect(realX, realY, SCALE, SCALE);
        }

        g.setColor(PIECE_COLOUR[currentPiece.typeInt()]);

        for (int i = 0; i < 4; i++){
            if (currentPieceLocation[1] + currentPiece.block(i)[1] >= END_HEIGHT){
                continue;
            }
            realX = (GAME_WIDTH - BOARD_WIDTH * SCALE) / 2 + (currentPieceLocation[0] + currentPiece.block(i)[0]) * SCALE;
            realY = (GAME_HEIGHT - (currentPieceLocation[1] + currentPiece.block(i)[1] + 1) * SCALE);
            g.fillRect(realX, realY, SCALE, SCALE);
        }

        // TODO draw queue
        // TODO draw hold
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
            // TODO possible bug here
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

        repaint();
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

        currentPieceLocation = new int[] {START_POSITIONS[currentPiece.typeInt() - 1][0], END_HEIGHT + START_POSITIONS[currentPiece.typeInt() - 1][1] - 1};

        bagPosition++;
        if (bagPosition == 7){
            bag1.shuffle();
        }
        else if (bagPosition == 14){
            bag2.shuffle();
            bagPosition = 0;
        }

        move(DOWN);

        ghostPiece();
        repaint();
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
            currentPieceLocation = new int[] {START_POSITIONS[currentPiece.typeInt() - 1][0], END_HEIGHT + START_POSITIONS[currentPiece.typeInt() - 1][1] - 1};
            currentPiece = new Tetrimino(temp);
        }

        ghostPiece();
        repaint();
    }

    public void matchPatterns(){
        // TODO add t-spin, tetrises, etc
        int counter;
        int x;
        int y;

        for (y = 0; y < END_HEIGHT; y++){
            counter = 0;

            for (x = 0; x < BOARD_WIDTH; x++){
                if (board[x][y] == 0){
                    counter++;
                }
            }

            if (counter == 0){
                for (int i = 0; i < BOARD_WIDTH; i++){
                    for (int j = y; j < END_HEIGHT; j++){
                        board[i][j] = board[i][j + 1];
                    }
                }

                y--;
            }
        }
    }

    public void ghostPiece(){
        // TODO map space key
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

    public void hardDrop(){
        // TODO will probably cause issues if executed at the same time as lockdown timer starts
        stopwatchLock.reset();
        currentPieceLocation[1] = ghostPieceLocation[1];
        stopwatchLock.reset();
        hold_pressed = false;
        first_DOWN = true;
        placeBlock();

        matchPatterns();

        nextBlock();
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
                first_DOWN = false;
            }
            else if ((!first_DOWN) && (stopwatchDown.elapsed() >= arr)){
                stopwatchDown.restart();
                stopwatchFall.restart();
                move(DOWN);
            }
        }
    }

    public double distance(int[] point1, int[] point2){
        double square1 = Math.pow(point1[0] - point2[0], 2);
        double square2 = Math.pow(point1[1] - point2[1], 2);
        return Math.sqrt(square1 + square2);
    }
}