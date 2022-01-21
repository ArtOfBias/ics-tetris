// class Highscores
// stores the highscores of previous plays for the play (max 5)

package src;

import java.awt.*;
import javax.swing.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

public class HighScores extends JFrame{
    private int[] highscores = new int[] {0,0,0,0,0};

    // constructor HighScores()
    // creates the frame for the highscores window which displays on click
    public HighScores(){
        this.setTitle("Tetris - High Scores");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(200, 200);
        this.setLocationRelativeTo(null);
        this.setBackground(Color.BLACK);
        this.setVisible(true);

        getHighScores();
        repaint();
    }
    
    // method paint
    // paints the background so that the text shows
    public void paint(Graphics g){
        Image image = createImage(200, 200);
        Graphics graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0, 0, this);
    }

    // method draw
    // displays the text for the highscores
    public void draw(Graphics g){
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 200, 200);
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Consolas", Font.PLAIN, 20));

        for (int i = 0; i < 5; i++){
            g.drawString(String.valueOf(highscores[i]), 20, 60 + i * 30);
        }
    }

    // method getHighScores()
    // stores the highscores in a file (creates a new one and fills with 0 if not already present)
    // retrieve high scores from file
    public void getHighScores(){
        boolean newFile = true;
        File dataFolder = new File("data");
        File scoreFile = new File("data\\highscores.txt");
        BufferedReader scoreReader = null;

        try {
            dataFolder.mkdirs();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        try {
            newFile = scoreFile.createNewFile();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        if (newFile){
            BufferedWriter scoreWriter = null;

            try {
                scoreWriter = new BufferedWriter(new FileWriter(scoreFile, false));
            }
            catch (IOException e){
                e.printStackTrace();
            }

            for (int i = 0; i < 5; i++){
                try {
                    scoreWriter.write(String.valueOf(highscores[i]));
                    scoreWriter.newLine();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }

            try {
                scoreWriter.flush();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        else {
            int i = 0;
            String currentLine = null;

            try {
                scoreReader = new BufferedReader(new FileReader(scoreFile));
            }
            catch (IOException e){
                e.printStackTrace();
            }

            while (true){
                try {
                    currentLine = scoreReader.readLine();
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                if (currentLine == null){
                    break;
                }

                highscores[i] = Integer.parseInt(currentLine);
                i++;
            }

            try {
                if (scoreReader != null){
                    scoreReader.close();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
