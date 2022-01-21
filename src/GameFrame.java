package src;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameFrame extends JFrame implements ActionListener{
    GamePanel panel;
    JButton restartButton;
    JButton menuButton;

    //constructor GameFrame()
    //creates the frame for the game (i.e. size and layout)
    //creates buttons that show at the top of the window
    //post: creates the game frame and window along with background color and buttons display
    public GameFrame(){
        // TODO better code formatting
        this.setSize(600, 500);
        this.setLayout(new BorderLayout());
        Container c = new Container();
        c.setLayout(new GridLayout());
        
        restartButton = new JButton("Restart");
        restartButton.addActionListener(this);
        restartButton.setFont(new Font("Consolas", Font.PLAIN, 20));
        restartButton.setBackground(Color.BLACK);
        restartButton.setForeground(Color.LIGHT_GRAY);
        restartButton.setSize(100, 50);
        restartButton.setLocation((600 - 100) / 2, 0);
        c.add(restartButton);

        menuButton = new JButton("Menu");
        menuButton.addActionListener(this);
        menuButton.setFont(new Font("Consolas", Font.PLAIN, 20));
        menuButton.setBackground(Color.BLACK);
        menuButton.setForeground(Color.LIGHT_GRAY);
        menuButton.setSize(100, 50);
        menuButton.setLocation((600 - 100) / 2, 50);
        c.add(menuButton);

        this.add(c, BorderLayout.NORTH);

        panel = new GamePanel();
        this.add(panel, BorderLayout.CENTER);
        
        this.setTitle("Tetris");
        this.setResizable(false);
        this.setBackground(Color.BLACK);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        panel.requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == restartButton){
            new GameFrame();
            this.dispose();
        }
        else if (e.getSource() == menuButton){
            new Menu();
            this.dispose();
        }
    }
}