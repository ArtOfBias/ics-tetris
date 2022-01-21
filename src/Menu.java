package src;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// TODO settings
// class Menu
// Displays the Main Menu for the game with buttons to redirect to gameplay and to exit
public class Menu extends JFrame implements ActionListener{
    //state the dimensions of the buttons
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 40;

    private JButton play, exit, highscores;
    private ImageIcon icon;
    private JLabel logo;

    //method Menu()
    //creates the frame for the menu window 
    //displays the tetris image and also displays the buttons to play, quit, and view highscore
    public Menu(){
        this.setTitle("Tetris - Menu");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 500);
        this.setLocationRelativeTo(null);
        
        Container c = getContentPane();
        c.setLayout(null);
        
        Font font = new Font("Consolas", Font.BOLD, 20);
        
        //create new buttons
        play = new JButton("Play");
        play.addActionListener(this);
        play.setBackground(Color.black);
        play.setForeground(Color.white);
        play.setFocusPainted(false);
        play.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        play.setLocation((600 - BUTTON_WIDTH) / 2, 200);
        play.setFont(font);
        c.add(play);

        highscores = new JButton("High Scores");
        highscores.addActionListener(this);
        highscores.setBackground(Color.black);
        highscores.setForeground(Color.white);
        highscores.setFocusPainted(false);
        highscores.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        highscores.setLocation((600 - BUTTON_WIDTH) / 2, 260);
        highscores.setFont(font);
        c.add(highscores);
        
        exit = new JButton("Exit");
        exit.addActionListener(this);
        exit.setBackground(Color.black);
        exit.setForeground(Color.white);
        exit.setFocusPainted(false);
        exit.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        exit.setLocation((600 - BUTTON_WIDTH) / 2, 320);
        exit.setFont(font);
        c.add(exit);
        
        // create logo image
        icon = new ImageIcon("assets\\Logo.png");
        logo = new JLabel("TEST");
        logo.setIcon(icon);
        logo.setLocation(200, 40);
        logo.setSize(200, 140);
        c.add(logo);

        this.setVisible(true);
        repaint();
    }

    //method paint
    //sets the background color to black
    public void paint(Graphics g){
        getContentPane().setBackground(Color.black);
        super.paint(g);
    }

    //method actionPerformed
    //redirects to other windows when the buttons are clicked
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == play){
            new GameFrame();
            this.dispose();
        }
        else if (e.getSource() == highscores){
            new HighScores();
        }
        else if (e.getSource() == exit){
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }

        repaint();
    }
}
