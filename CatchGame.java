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
        setSize(FRAME_WIDTH, FRAME_HEIGHT);// set width & height of frame
        setTitle("The Game");//title of the project
        setLocationRelativeTo(null);//location of the application
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//to exit the application
        DrawingPanel panel = new DrawingPanel(FRAME_WIDTH, FRAME_HEIGHT);//the frame of the application
        add(panel, BorderLayout.CENTER); // add panel in frame
        pack();//to remove the spaces
        setVisible(true);//to make it appear
    }

    public static void main(String[] args) {
        new CatchGame();
    }
}

class DrawingPanel extends JPanel implements Runnable {

    private static int PANEL_WIDTH; // panel width
    private static int PANEL_HEIGHT; // panel height
    private static final int SW = 50;// square width
    private static final int SH = 50;// square hight

    Thread mythread;//

    int NumOfSqr = 5;//number of square
    Rectangle sqr[] = new Rectangle[NumOfSqr];//to make a square
    int xmovement[] = new int[NumOfSqr];//to make it move along x axis
    int ymovement[] = new int[NumOfSqr];//to make it move along y axis
    int x[] = new int[NumOfSqr];//to make it move randomly along x axis
    int y[] = new int[NumOfSqr];//to make it move randomly along y axis

    private static final int polyW = 60;  //polygon-width
    private static final int polyH = 100; //polygon-height

    //(x,y) coordinate of points to be use in polygon	
    private static int x0 = 150;
    private static int y0 = 600;
    private static int x1 = x0 + polyW;
    private static int y1 = y0;
    private static int x2 = x0 + (x1 - x0) / 2;
    private static int y2 = y0 - polyH;
    private static int xPoly[] = {x0, x1, x2};//initialize array
    private static int yPoly[] = {y0, y1, y2};//initialize array

    public int Xline[] = new int[100];//to draw a line in x axis
    public int Yline[] = new int[100];//to draw a line in y axis
    public int counter = 0;//to count the dot in the panal

    private static int tx = 10;// tranlsation distances along x-axis
    private static int ty = tx;// tranlsation distances along y-axis
    // constrctor to setup basic configurations of the panal

    public DrawingPanel(int FRAME_WIDTH, int FRAME_HEIGHT) {

        PANEL_WIDTH = FRAME_WIDTH;//frame width
        PANEL_HEIGHT = FRAME_HEIGHT;//frame height
        setLayout(new BorderLayout());//
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT)); // set width & height of panel
        this.setBackground(Color.black);//set the color of the panal
        mythread = new Thread(this);
        mythread.start();
        this.setFocusable(true);//if there is multi task with same function

        // register keyboard listener
        KeyPressListener listenerKey = new KeyPressListener();
        addKeyListener(listenerKey);

        // assgine the intial random corrdiantes for when the square will spawn
        for (int i = 0; i < NumOfSqr; i++) {
            x[i] = (int) (Math.random() * (PANEL_WIDTH - SW));// max random so they stay within the panal
            y[i] = (int) (Math.random() * (PANEL_WIDTH - SW));// max random so they stay within the panal

            // giving eatch one random movment so they have diffrent animation speed with max speed 20 
            // and min speed 1 so we dont make the shape stop from the start 
            xmovement[i] = (int) (Math.random() * 20) + 1;
            ymovement[i] = (int) (Math.random() * 20) + 1;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;//convert the graphics into 2d graphics
        g2.setStroke(new BasicStroke(5.0f));
        Polygon poly = new Polygon(xPoly, yPoly, xPoly.length);//to make a polygon
        Shape polyShape = poly;//to shape the polygone
        g2.setColor(Color.blue);//to set the color of polygon
        g2.fill(polyShape);//to fill the shape with color
        //to draw a point
        for (int i = 0; i < Xline.length; i++) {
            g2.setColor(Color.white);
            g2.drawLine(Xline[i], Yline[i], Xline[i], Yline[i]);
        }
        //to make the square stop when touch the polygon
        for (int i = 0; i < NumOfSqr; i++) {
            if (xPoly[2] > x[i] && xPoly[2] < x[i] + SW) {
                if (yPoly[2] > y[i] && yPoly[2] < y[i] + SH) {
                    xmovement[i] = 0;
                    ymovement[i] = 0;
                }
            }
        }
        g2.setColor(Color.green);//to set the color of square
        for (int i = 0; i < NumOfSqr; i++) {
            sqr[i] = new Rectangle(x[i], y[i], SW, SH);//to create a square
            g2.fill(sqr[i]);//to fill the shape
        }
        g2.setColor(Color.red);//to set the color of square after touch the polygon
        for (int i = 0; i < NumOfSqr; i++) {
            if (xmovement[i] == 0 && ymovement[i] == 0) {// if movment is 0 means we have to chanenge its color to red becuse it stopped 
                g2.fill(sqr[i]);//to fill the square with color
            }
        }
    }

    @Override
    public void run() {
        // animation loop
        boolean flag = true;
        while (flag) {
            try {
                Thread.sleep(50);//control the speed of square
            } catch (InterruptedException e) {
            }
            for (int i = 0; i < NumOfSqr; i++) {
                x[i] = x[i] + xmovement[i];
                y[i] = y[i] + ymovement[i];
                // if shape hits edege of x axis and needs to bounce back
                if (x[i] > PANEL_WIDTH - SW || x[i] < 0) {
                    xmovement[i] = -xmovement[i];
                }
                // if shape hits edege of y axis and needs to bounce back
                if (y[i] > PANEL_HEIGHT - SH || y[i] < 0) {
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
                case KeyEvent.VK_LEFT://to move polygon to left
                    moveLeft();
                    break;
                case KeyEvent.VK_RIGHT://to move polygon to right
                    moveRight();
                    break;
                case KeyEvent.VK_UP://to move polygon to up
                    moveUp();
                    break;
                case KeyEvent.VK_DOWN://to move polygon to down
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
            repaint();// repainting shapes after changes 
        }
    }

    public void moveUp() {
        for (int i = 0; i < yPoly.length; i++) {
            yPoly[i] = yPoly[i] - ty;//if polygon move up the y axis will decreass
        }
    }

    public void moveDown() {
        for (int i = 0; i < yPoly.length; i++) {
            yPoly[i] = yPoly[i] + ty;//if polygon move up the y axis will increase
        }
    }

    public void moveRight() {
        for (int i = 0; i < xPoly.length; i++) {
            xPoly[i] = xPoly[i] + tx;//if polygon move up the x axis will increase
        }
    }

    public void moveLeft() {
        for (int i = 0; i < xPoly.length; i++) {
            xPoly[i] = xPoly[i] - tx;//if polygon move up the x axis will decreass
        }
    }
}
