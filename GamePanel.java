import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable, KeyListener{
    public int BOARD_WIDTH;
    public int BOARD_HEIGHT;
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
        if (rotationDirection.equals("left")){

        }
        else if (rotationDirection.equals("right")){

        }
        else if (rotationDirection.equals("turn")){

        }

    }
}