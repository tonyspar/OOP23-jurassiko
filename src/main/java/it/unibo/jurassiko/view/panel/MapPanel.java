package it.unibo.jurassiko.view.panel;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

import it.unibo.jurassiko.view.gamescreen.impl.ViewImpl;

/**
 * Set the map for the GUI.
 */
public class MapPanel extends JPanel {

    private static final long serialVersionUID = 3546632756867582508L;

    private static final double HEIGHT_RATIO = 0.8;
    private static final double WIDTH_RATIO = 0.8;
    private static final String URL_IMAGE = "images/mappa.png";

    /**
     * Set the map in the relevant label and add it to the LayeredPane.
     */
    public MapPanel() {
        BufferedImage imageMap;
        try {
            imageMap = ImageIO.read(ClassLoader.getSystemResource(URL_IMAGE));
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to read the map file", e);
        }
        final int width = (int) (WIDTH_RATIO * ViewImpl.getScreenSize().getWidth());
        final int height = (int) (HEIGHT_RATIO * ViewImpl.getScreenSize().getHeight());
        final ImageIcon map = ViewImpl.scaleImage(imageMap, width, height);
        final JLabel mapLabel = new JLabel(map);
        mapLabel.setBounds(0, 0, width, height);
        final JLayeredPane layPane = new JLayeredPane();
        layPane.add(mapLabel, JLayeredPane.DEFAULT_LAYER);
        layPane.setPreferredSize(new Dimension(width, height));
        this.setLayout(new BorderLayout());
        this.add(layPane);
    }
}
