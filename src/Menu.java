package src;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// TODO settings
public class Menu extends JFrame implements ActionListener{
    public static final int BUTTON_WIDTH = 100;
    public static final int BUTTON_HEIGHT = 40;

    public GameFrame frame;
    public JButton play;
    public JButton exit;
    public Image image;
    public ImageIcon icon;
    public JLabel logo;

    public Menu(){
        this.setTitle("Tetris");
        this.setResizable(false);
        this.setBackground(Color.GRAY);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 500);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        Container c = getContentPane();
        c.setLayout(null);

        //create new buttons
        play = new JButton("Play");
        play.addActionListener(this);
        play.setBackground(Color.black);
        play.setForeground(Color.white);
        play.setFocusPainted(false);
        play.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        play.setLocation((600 - BUTTON_WIDTH) / 2, 200);

        // TODO high scores button

        exit = new JButton("Exit");
        exit.addActionListener(this);
        exit.setBackground(Color.black);
        exit.setForeground(Color.white);
        exit.setFocusPainted(false);
        exit.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        exit.setLocation((600 - BUTTON_WIDTH) / 2, 320);

        // create logo image
        icon = new ImageIcon("assets\\Logo.png");
        logo = new JLabel("TEST");
        logo.setIcon(icon);
        logo.setLocation(200, 40);
        logo.setSize(200, 140);

        Font font = new Font("Consolas", Font.BOLD, 20);
        play.setFont(font);
        exit.setFont(font);

        c.add(play);
        c.add(exit);
        c.add(logo);

        repaint();
    }

    public void paint(Graphics g){
        getContentPane().setBackground(Color.black);
        super.paint(g);
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
