import java.util.Scanner;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

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
    }
}
