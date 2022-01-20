package src;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// TODO settings
public class Menu extends JFrame implements ActionListener{
    GameFrame frame;
    JButton play;
    JButton exit;

    public Menu(){
        this.setTitle("Tetris");
        this.setResizable(false);
        this.setBackground(Color.GRAY);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 500);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        play = new JButton("Play");
        play.addActionListener(this);
        exit = new JButton("Exit");
        exit.addActionListener(this);

        Container c = getContentPane();
        c.setLayout(null);
        play.setSize(100, 50);
        exit.setSize(100, 50);
        play.setLocation(200, 200);
        exit.setLocation(200, 300);
        c.add(play);
        c.add(exit);
    }

    public void paint(Graphics g){
        super.paint(g);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 50)); 
        g.drawString("Tetris", 195, 150);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == play){
            frame = new GameFrame();
            this.dispose();
        }
        else {
            this.dispose();
        }
        repaint();
    }

}
