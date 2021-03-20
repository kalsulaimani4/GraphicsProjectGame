package CatchGame;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
public class CatchGame extends JFrame {
    private static final int FRAME_WIDTH = 800; // frame-width
    private static final int FRAME_HEIGHT = 800; // frame-height
    public CatchGame() {
        setLayout(new BorderLayout()); // layout of frame
        setSize(FRAME_WIDTH, FRAME_HEIGHT);                // set width & height of frame
        setTitle("The Game");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DrawingPanel panel = new DrawingPanel(FRAME_WIDTH, FRAME_HEIGHT);
        add(panel, BorderLayout.CENTER); // add panel in frame
        pack();
        setVisible(true);
    }
    public static void main(String[] args) {
        new CatchGame();
    }
}
class DrawingPanel extends JPanel implements Runnable {
    private static int PANEL_WIDTH; // panel width
    private static int PANEL_HEIGHT; // panel height
    // rectangle hight and width 
    private static final int RW = 50;
    private static final int RH = 50;
    //int x, y;
    Thread mythread;
    int NumOfRect = 5;
    Rectangle rec[] = new Rectangle[NumOfRect];
    int xmovement[] = new int[NumOfRect];
    int ymovement[] = new int[NumOfRect];
    int x[] = new int[NumOfRect];
    int y[] = new int[NumOfRect];
    private JPanel msgPanel = new JPanel();
    private static final int polyW = 60;  //polygon-width
    private static final int polyH = 100; //polygon-height
    // (x,y) coordinate of points to be use in polygon	
    private static int x0 = 150;
    private static int y0 = 600;
    private static int x1 = x0 + polyW;
    private static int y1 = y0;
    private static int x2 = x0 + (x1 - x0) / 2;
    private static int y2 = y0 - polyH;
    private static int xPoly[] = {x0, x1, x2}; // initialize array
    private static int yPoly[] = {y0, y1, y2};
    public int Xline[] = new int[100];
    public int Yline[] = new int[100];
    public int counter = 0;
    private static int tx = 10;     // tranlsation distances along x-axis
    private static int ty = tx;     // tranlsation distances along y-axis
    // constrctor to setup basic configurations of the panal
    public DrawingPanel(int FRAME_WIDTH, int FRAME_HEIGHT) {
        PANEL_WIDTH = FRAME_WIDTH;
        PANEL_HEIGHT = FRAME_HEIGHT;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT)); // set width & height of panel
        this.setBackground(Color.black);
        mythread = new Thread(this);
        mythread.start();
        this.setFocusable(true);
        // register keyboard listener
        KeyPressListener listenerKey = new KeyPressListener();
        addKeyListener(listenerKey);
        // assginginh the intial random corrdiantes for when the recangles will spawn
        for (int i = 0; i < NumOfRect; i++) {         
            x[i] = (int) (Math.random() * (PANEL_WIDTH - RW));// max random so they stay within the panal
            y[i] = (int) (Math.random() * (PANEL_WIDTH - RW));// max random so they stay within the panal
            // giving eatch one random movment so they have diffrent animation speed with max speed 50
            xmovement[i] = (int) (1+Math.random() * 20);
            ymovement[i] = (int) (Math.random() * 20);
        }
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5.0f));
        Polygon poly = new Polygon(xPoly, yPoly, xPoly.length);
        Shape polyShape = poly;
        g2.setColor(Color.blue);
        g2.fill(polyShape);
        for (int i = 0; i < Xline.length; i++) {
            g2.setColor(Color.white);
            g2.drawLine(Xline[i], Yline[i], Xline[i], Yline[i]);          
        }        
                for(int i=0;i<NumOfRect;i++){
            if(xPoly[2]>x[i]&&xPoly[2]<x[i]+RW){
                if(yPoly[2]>y[i]&&yPoly[2]<y[i]+RH){
                   xmovement[i]=0;
                   ymovement[i]=0;
                }                
            }           
        }
        g2.setColor(Color.green);
        for (int i = 0; i < NumOfRect; i++) {
            rec[i] = new Rectangle(x[i], y[i], RW, RH);
            g2.fill(rec[i]);
        }
        g2.setColor(Color.red);
        for (int i = 0; i < NumOfRect; i++) {
            if (xmovement[i]==0&&ymovement[i]==0) {
                g2.fill(rec[i]);    
            }
        }
    }
    @Override
    public void run() {
        // animation loop
        boolean flag = true;
        while (flag) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
            for (int i = 0; i < NumOfRect; i++) {
                x[i] = x[i] + xmovement[i];
                y[i] = y[i] + ymovement[i];
                // if shape his edege of x axis and needs to bounce back
                if (x[i] > PANEL_WIDTH - RW || x[i] < 0) {
                    xmovement[i] = -xmovement[i];
                }
                // if shape hits edege of y axis and needs to bounce back
                if (y[i] > PANEL_HEIGHT - RH || y[i] < 0) {
                    ymovement[i] = -ymovement[i];
                }
            }
            repaint();
        }
    }
    // inner class to handle keyboard events
    private class KeyPressListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                    moveLeft();
                    break;
                case KeyEvent.VK_RIGHT:
                    moveRight();
                    break;
                case KeyEvent.VK_UP:
                    moveUp();
                    break;
                case KeyEvent.VK_DOWN:
                    moveDown();
                    break;
            }
            if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT
                    || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_UP) {
                // using mod to allow only 100 dots and to delete from the oldest dots drawn 
                Xline[(counter) % 100] = xPoly[2];
                Yline[(counter) % 100] = yPoly[2];
                counter++;
            }
            repaint();
        }
    }
    public void moveUp() {
        for (int i = 0; i < yPoly.length; i++) {
            yPoly[i] = yPoly[i] - ty;
        }
    }
    public void moveDown() {
        for (int i = 0; i < yPoly.length; i++) {
            yPoly[i] = yPoly[i] + ty;
        }
    }
    public void moveRight() {
        for (int i = 0; i < xPoly.length; i++) {
            xPoly[i] = xPoly[i] + tx;
        }
    }
    public void moveLeft() {
        for (int i = 0; i < xPoly.length; i++) {
            xPoly[i] = xPoly[i] - tx;
        }
    }
}