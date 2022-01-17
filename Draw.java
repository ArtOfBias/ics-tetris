import java.awt.*;
import javax.swing.*;

public class Draw extends JFrame{
    private static int WINDOW_WIDTH = 100;
    private static int WINDOW_HEIGHT = 250;
    private static int BOARD_WIDTH = WINDOW_WIDTH/2;
    private static int BOARD_HEIGHT = WINDOW_HEIGHT/2;
    private static int BLOCKS_WIDTH = 10;
    private static int BLOCKS_HEIGHT = 25;
    private int rect_coordinateX = BOARD_WIDTH/2;
    private int rect_coordinateY = 0;

    //private JButton testButton;
    private JPanel panelMain;
    //private GamePanel panel; 

    public Draw(){
        this.setTitle("Tetris");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
        this.setVisible(true);
        // creating main JPanel (white)
        panelMain = new JPanel();
        panelMain.setBackground(Color.WHITE);
        panelMain.setBounds(BOARD_WIDTH/2, 0, BOARD_WIDTH, BOARD_HEIGHT);
        //panelMain.setPreferredSize(new Dimension(200, 40));
        add(panelMain);
      
        // creating JButton in the main JPanel (white)
        //testButton = new JButton("Button from main class");
        //panelMain.add(testButton);

         // creating new JPanelOne object from JPanelOne class containing black JPanel
        //panel = new GamePanel();

        // adding black JPanel to main JPanel (white)
        //panelMain.add(panel);

        //pack();

    }

    public void paint(Graphics g){
        super.paint(g);
        int k;
        int l;
        
        int htOfRow = BOARD_HEIGHT / (BLOCKS_HEIGHT);
        int whOfCol = BOARD_WIDTH / (BLOCKS_WIDTH);
        for(l = 0; l < BLOCKS_HEIGHT; l++){
            for (k = 0; k < BLOCKS_WIDTH; k++) {
                g.drawRect(rect_coordinateX, rect_coordinateY, whOfCol, htOfRow);
                rect_coordinateX += whOfCol;
            }
            rect_coordinateY += htOfRow;
        }
        
    }
    
    /*public void paint(Graphics g){
        super.paint(g);
        g.drawString("Tetris woo", 10, 50);
    }*/

    /*private final Action exitAction = new AbstractAction("Exit"){
        @Override
        public void actionPerformed(ActionEvent e){
            System.exit(0);
        }
    };*/

}
