import java.awt.*;
import javax.swing.*;

public class Draw extends JFrame{

    private JButton testButton;
    private JPanel panelMain;
    private GamePanel panel;

    public Draw(){
        this.setTitle("Tetris");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(420,90);
        this.setVisible(true);
        // creating main JPanel (white)
        panelMain = new JPanel();
        panelMain.setBackground(Color.WHITE);
        panelMain.setBounds(0, 0, 420, 90);
        panelMain.setPreferredSize(new Dimension(200, 40));
        add(panelMain);

        // creating JButton in the main JPanel (white)
        testButton = new JButton("Button from main class");
        panelMain.add(testButton);

         // creating new JPanelOne object from JPanelOne class containing black JPanel
        panel = new GamePanel();

        // adding black JPanel to main JPanel (white)
        panelMain.add(panel);

        pack();

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