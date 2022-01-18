
import java.awt.*;
import java.awt.event.*;

public class Board extends Rectangle {
    
    int width, height, rows, columns;

    private int rect_coordinateX = width/2;
    private int rect_coordinateY = 0;

    //private int[][] blocks = new int[BOARD_WIDTH][BOARD_HEIGHT];
    public void draw(Graphics g){
        /*for (int x = 0; x < BOARD_WIDTH; x++){
            for (int y = 0; y < BOARD_HEIGHT; y++){
                if (blocks[x][y] == 0){
                    
                }
            }
        }*/
        int k;
        int l;
        width = getSize().width;
        height = getSize().height;

        int htOfRow = height / (rows);
        int whOfCol = width / (columns);
        for(l = 0; l < columns; l++){
            for (k = 0; k < rows; k++) {
                g.drawRect(rect_coordinateX, rect_coordinateY, width, height);
                rect_coordinateX += whOfCol;
            }
            rect_coordinateY += htOfRow;
        }
        
    }
    

}
