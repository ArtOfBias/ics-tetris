
import java.awt.*;
import java.awt.event.*;

public class Board extends Rectangle {
    private static int BOARD_WIDTH;
    private static int BOARD_HEIGHT;

    private int[][] blocks = new int[BOARD_WIDTH][BOARD_HEIGHT];

    public void draw(Graphics g){
        for (int x = 0; x < BOARD_WIDTH; x++){
            for (int y = 0; y < BOARD_HEIGHT; y++){
                if (blocks[x][y] == 0){
                    // TODO: finish drawing board
                }
            }
        }
    }
    

}
