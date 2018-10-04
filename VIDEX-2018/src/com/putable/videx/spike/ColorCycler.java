package com.putable.videx.spike;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
// Demo from stackoverflow or somewhere..  
public class ColorCycler {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame jFrame = new JFrame("Color Cycler");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(new MyPanel());
        jFrame.pack();
        jFrame.setVisible(true);
    }

}

class MyPanel extends JPanel implements ActionListener {

    private final IndexColorModel[] colorModels = new IndexColorModel[216];
    private final byte[] imageData = new byte[1000 * 1000];
    private final MemoryImageSource imageSource;
    private final Image image;
    private int currentFrame = 0;

    public MyPanel() {
        generateColorModels();
        generateImageData();
        imageSource = new MemoryImageSource(1000, 1000, colorModels[0], imageData, 0, 1000);
        imageSource.setAnimated(true);
        image = createImage(imageSource);
        (new Timer(100, this)).start();
    }

    // The window size is 1000x1000 pixels.
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1000, 1000);
    }

    // Generate 216 unique colors models, one for each frame.
    private void generateColorModels() {
        byte[] reds = new byte[216];
        byte[] greens = new byte[216];
        byte[] blues = new byte[216];
        int index = 0;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 6; k++, index++) {
                    reds[index] = (byte) (i * 51);
                    greens[index] = (byte) (j * 51);
                    blues[index] = (byte) (k * 51);
                }
            }
        }
        for (int i = 0; i < 216; i++) {
            colorModels[i] = new IndexColorModel(8, 216, reds, greens, blues);
            reds = cycleColors(reds);
            greens = cycleColors(greens);
            blues = cycleColors(blues);
        }
    }

    // Create the image data for the MemoryImageSource.
    // This data is created once and never changed.
    private void generateImageData() {
        for (int i = 0; i < 1000 * 1000; i++) {
            imageData[i] = (byte) ((((i%1000) + (i/1000))/32) % 216);
        }
    }

    // Draw the image.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, 1000, 1000, null);
    }

    // This method is called by the timer every 35 ms.
    // It updates the ImageSource of the image to be drawn.
    @Override
    public void actionPerformed(ActionEvent e) { // Called by Timer.
        currentFrame++;
        if (currentFrame == 216) {
            currentFrame = 0;
        }
        imageSource.newPixels(imageData, colorModels[currentFrame], 0, 1000);
        repaint();
    }

    // Cycle the colors to the right by 1.
    private byte[] cycleColors(byte[] colors) {
        byte[] newColors = new byte[216];
        newColors[0] = colors[215];
        System.arraycopy(colors, 0, newColors, 1, 215);
        return newColors;
    }
}