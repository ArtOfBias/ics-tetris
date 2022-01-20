package src;

import java.awt.*;  
import javax.swing.JFrame;  
  
public class TestImage extends Canvas{  
      
    public void paint(Graphics g) {  
  
        Toolkit t=Toolkit.getDefaultToolkit();  
        Image i=t.getImage("Logo.png");  
        g.drawImage(i,120,100,this);  
    }  
        public static void main(String[] args) {  
        TestImage m=new TestImage();  
        JFrame f=new JFrame();  
        f.add(m);  
        f.setSize(400,400);  
        f.setVisible(true);  
    }  
  
}  