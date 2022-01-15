// import java.util.Scanner;
// import java.awt.*;
// import javax.swing.*;
// import java.awt.event.*;

// TODO javadoc
// TODO leaderboard
// TODO saving leaderboard

class Main {
    public static void main(String[] args){
        Tetrimino a = new Tetrimino("l");
        a.print();
        a.rotate("left");
        a.print();
        a = new Tetrimino("i");
        a.print();
        a.rotate("left");
        a.print();
        a.rotate("turn");
        a.print();

        Draw frame = new Draw();
        frame.setVisible(true);
    }
}