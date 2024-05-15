package it.unibo.jurassiko.view.gamescreen.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import it.unibo.jurassiko.controller.api.MainController;
import it.unibo.jurassiko.view.gamescreen.api.View;
import it.unibo.jurassiko.view.panels.MapPanel;
import it.unibo.jurassiko.view.panels.TopBarPanel;
import it.unibo.jurassiko.view.windows.CardWindow;
import it.unibo.jurassiko.view.windows.ObjectiveWindow;

/**
 * Implementation of the View for the GUI.
 */
public class ViewImpl extends JFrame implements View {

    private static final long serialVersionUID = 4546011807046339073L;

    private final MapPanel panel;
    private final TopBarPanel buttons;
    private final ObjectiveWindow objectiveCard;
    private final CardWindow cardWindow;
    private static final String TITLE = "Jurassiko";

    /**
     * Set up the relevant panels and show everything in the GUI.
     * 
     * @param mainContr the MainController
     */
    public ViewImpl(final MainController mainContr) {
        this.panel = new MapPanel(mainContr);
        this.objectiveCard = new ObjectiveWindow(mainContr);
        this.cardWindow = new CardWindow(mainContr);
        this.buttons = new TopBarPanel(mainContr, this.objectiveCard, this.cardWindow);
        this.setTitle(TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.add(panel, BorderLayout.CENTER);
        this.add(buttons, BorderLayout.NORTH);
    }

    /**
     * 
     * Scale the image that we want to set.
     * 
     * @param image  the image that we want to set
     * @param width  width of the new image
     * @param height height of the new image
     * @return return the scaled image
     */
    public static ImageIcon scaleImage(final BufferedImage image, final int width, final int height) {
        final ImageIcon icon = new ImageIcon(image);
        final Image temp = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(temp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void display() {
        super.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Returns screen size.
     * 
     * @return dimension of the screen
     */
    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * Update all the Panel, Label in the frame.
     */
    public void updatePanel() {
        this.panel.updateBoard();
        this.buttons.updateTopBar();
        this.objectiveCard.updateObjective();
        this.cardWindow.updateCard();
    }

    /**
     * Show the Objective.
     */
    public void displayObjective() {
        this.objectiveCard.showObjectiveCard();
    }
}
