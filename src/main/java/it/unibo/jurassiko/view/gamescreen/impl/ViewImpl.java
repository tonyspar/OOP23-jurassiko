package it.unibo.jurassiko.view.gamescreen.impl;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import it.unibo.jurassiko.view.gamescreen.api.View;
import it.unibo.jurassiko.view.panel.TopBarPanel;
import it.unibo.jurassiko.view.panel.MapPanel;

/**
 * Implementation of the View for the GUI.
 */
public class ViewImpl extends JFrame implements View {

    private final MapPanel panel = new MapPanel();
    private final TopBarPanel buttons = new TopBarPanel();
    private static final String TITLE = "Jurassiko";

    /**
     * Set up the relevant panels and show everything in the GUI.
     */
    public ViewImpl() {
        this.setTitle(TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.add(panel, BorderLayout.CENTER);
        this.add(buttons, BorderLayout.NORTH);
        this.display();
    }

    /**
     * 
     * Scale the image that we want to set.
     * 
     * @param image the image that we want to set
     * @param width width of the new image
     * @param height height of the new image
     * @return return the scaled image
     */
    public static final ImageIcon scaleImage(final BufferedImage image, final int width, final int height) {
        final ImageIcon icon = new ImageIcon(image);
        final Image temp = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(temp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void display() {
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
