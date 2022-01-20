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
        
        //create new buttons
        play = new JButton("Play");
        play.addActionListener(this);
        exit = new JButton("Exit");
        exit.addActionListener(this);
        //set button colors
        play.setBackground(Color.black);
        exit.setBackground(Color.black);
        play.setForeground(Color.white);
        exit.setForeground(Color.white);
        play.setFocusPainted(false);
        exit.setFocusPainted(false);

        //add the buttons
        Container c = getContentPane();
        c.setLayout(null);
        play.setSize(100, 50);
        exit.setSize(100, 50);
        play.setLocation(235, 200);
        exit.setLocation(235, 300);
        c.add(play);
        c.add(exit);
    }

    public void paint(Graphics g){
        super.paint(g);
        getContentPane().setBackground(Color.black);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 50)); 
        g.setColor(Color.white);
        g.drawString("Tetris", 235, 150);
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
