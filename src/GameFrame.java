// class GameFrame
// JFrame where the game is run, has buttons to restart the game or to return to main menu

package src;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameFrame extends JFrame implements ActionListener{
    GamePanel panel;
    JButton restartButton, menuButton;

    // constructor GameFrame()
    // creates the frame for the game (i.e. size and layout)
    // creates buttons that show at the top of the window
    public GameFrame(){
        this.setTitle("Tetris");
        this.setResizable(false);
        this.setBackground(Color.BLACK);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 500);

        this.setLayout(new BorderLayout());

        // container for buttons in top bar
        Container c = new Container();
        c.setLayout(new GridLayout());

        // adding buttons
        restartButton = new JButton("Restart");
        restartButton.addActionListener(this);
        restartButton.setFont(new Font("Consolas", Font.PLAIN, 20));
        restartButton.setBackground(Color.BLACK);
        restartButton.setForeground(Color.LIGHT_GRAY);
        c.add(restartButton);

        menuButton = new JButton("Menu");
        menuButton.addActionListener(this);
        menuButton.setFont(new Font("Consolas", Font.PLAIN, 20));
        menuButton.setBackground(Color.BLACK);
        menuButton.setForeground(Color.LIGHT_GRAY);
        c.add(menuButton);

        this.add(c, BorderLayout.NORTH);

        panel = new GamePanel();
        this.add(panel, BorderLayout.CENTER);

        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        panel.requestFocusInWindow(); // allows key inputs to be passed to panel
    }

    // method actionPerformed
    // redirects to new Gameframe or menu depending on which button was clicked
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