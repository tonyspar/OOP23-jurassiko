package it.unibo.jurassiko.view.windows;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.jurassiko.controller.api.MainController;
import it.unibo.jurassiko.model.card.api.Card.CardType;
import it.unibo.jurassiko.model.player.api.Player;
import it.unibo.jurassiko.view.gamescreen.impl.ViewImpl;

/**
 * Represents a popup window used to show the cards to the player.
 */
public class CardWindow extends JPanel {

    private static final long serialVersionUID = -4969570126904018362L;
    private static final String JACK_CARD_IMAGE = "images/cards/JackCard.png";
    private static final String HORSE_CARD_IMAGE = "images/cards/HorseCard.png";
    private static final String CANNON_CARD_IMAGE = "images/cards/CannonCard.png";
    private static final String JOLLY_CARD_IMAGE = "images/cards/JollyCard.png";
    private static final double WIDTH_RATIO = 0.068;
    private static final double HEIGHT_RATIO = 0.152;
    private static final double DISTANCE_RATIO = 0.01;
    private static final String TEXT_FONT = "Arial";
    private static final int TEXT_SIZE = 15;
    private final Map<CardType, ImageIcon> cardsImageMap = new HashMap<>();
    private final Map<CardType, JLabel> cardsLabelMap = new HashMap<>();
    private final transient MainController controller;

    /**
     * Creates the cards window initializing the card and the text for the
     * description.
     * 
     * @param controller the main controller instance
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "MainController instance is needed on this class by design")
    public CardWindow(final MainController controller) {
        final int distance = (int) (DISTANCE_RATIO * ViewImpl.getScreenSize().getWidth());

        this.setLayout(new GridLayout(2, 2, distance, distance));
        this.controller = controller;

        loadCardImage();
        setInternalPanel();
    }

    /**
     * Gets the card sprites map.
     * 
     * @return the map containing card sprites
     */
    public Map<CardType, ImageIcon> getCardSprites() {
        return Map.copyOf(this.cardsImageMap);
    }

    /**
     * Displays the cards as a JOptionPane.
     */
    public void showCards() {
        JOptionPane.showMessageDialog(null, this, "Cards", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Update the cards based on the turn of the Player.
     */
    public void updateCard() {
        setCardDescriptions(this.controller.getCurrentPlayer());
    }

    /**
     * Sets the card descriptions to display alongside the corresponding cards.
     * 
     * @param player the player whose card descriptions are set
     */
    private void setCardDescriptions(final Player player) {
        for (final CardType cardType : CardType.values()) {
            cardsLabelMap.get(cardType)
                    .setText(cardType.toString().toLowerCase(Locale.ROOT)
                            + ": " + player.getTypeMap().getOrDefault(cardType, 0).toString());
        }
    }

    /**
     * Loads and scales the card image according to window size.
     */
    private void loadCardImage() {
        BufferedImage jackCard;
        BufferedImage horseCard;
        BufferedImage cannonCard;
        BufferedImage jollyCard;
        try {
            jackCard = ImageIO.read(ClassLoader.getSystemResource(JACK_CARD_IMAGE));
            horseCard = ImageIO.read(ClassLoader.getSystemResource(HORSE_CARD_IMAGE));
            cannonCard = ImageIO.read(ClassLoader.getSystemResource(CANNON_CARD_IMAGE));
            jollyCard = ImageIO.read(ClassLoader.getSystemResource(JOLLY_CARD_IMAGE));
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to read card image", e);
        }
        final int dinoWidth = (int) (WIDTH_RATIO * ViewImpl.getScreenSize().getWidth());
        final int dinoHeight = (int) (HEIGHT_RATIO * ViewImpl.getScreenSize().getHeight());

        this.cardsImageMap.put(CardType.JACK,
                ViewImpl.scaleImage(jackCard, dinoWidth, dinoHeight));
        this.cardsImageMap.put(
                CardType.HORSE, ViewImpl.scaleImage(horseCard, dinoWidth, dinoHeight));
        this.cardsImageMap.put(
                CardType.CANNON, ViewImpl.scaleImage(cannonCard, dinoWidth, dinoHeight));
        this.cardsImageMap.put(
                CardType.JOLLY, ViewImpl.scaleImage(jollyCard, dinoWidth, dinoHeight));
    }

    /**
     * Sets the internal panel containing the cards and their labels.
     */
    private void setInternalPanel() {
        for (final CardType cardType : CardType.values()) {
            final JPanel panel = new JPanel(new BorderLayout());
            final JLabel textLabel = new JLabel("0");
            setTextLabel(textLabel);
            cardsLabelMap.put(cardType, textLabel);
            panel.add(textLabel, BorderLayout.SOUTH);
            panel.add(new JLabel(cardsImageMap.get(cardType)), BorderLayout.CENTER);
            this.add(panel);
        }
    }

    /**
     * Sets style and layout of the label containing the description.
     * 
     * @param textLabel the label containing the description
     */
    private void setTextLabel(final JLabel textLabel) {
        textLabel.setFont(new Font(TEXT_FONT, Font.BOLD, TEXT_SIZE));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setOpaque(false);
    }

}
