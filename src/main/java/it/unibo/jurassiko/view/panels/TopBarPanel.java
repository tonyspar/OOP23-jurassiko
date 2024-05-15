package it.unibo.jurassiko.view.panels;

import it.unibo.jurassiko.controller.api.MainController;
import it.unibo.jurassiko.core.api.GamePhase.Phase;
import it.unibo.jurassiko.model.player.api.Player.GameColor;
import it.unibo.jurassiko.view.gamescreen.impl.ViewImpl;
import it.unibo.jurassiko.view.windows.CardWindow;
import it.unibo.jurassiko.view.windows.ObjectiveWindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Makes the top-bar for the GUI.
 */
public class TopBarPanel extends JPanel {

    private static final long serialVersionUID = 1379037036670658465L;
    private static final double HEIGHT_RATIO = 0.1;
    private static final double WIDTH_RATIO = 0.8;
    private static final int FONT_SIZE = 24;
    private static final int DISTANCE_BUTTON_L_R = 30;
    private static final int BG_PLAYER_RGB = 175;
    private static final String URL_IMAGE = "images/topbar.png";
    private static final String OBJ_BUTTON_NAME = "Obiettivo";
    private static final String PLACE_BUTTON_NAME = "Piazzamento";
    private static final String ATK_BUTTON_NAME = "Attacco";
    private static final String ENDTURN_BUTTON_NAME = "Fine Turno";
    private static final String ENDTURN_DIALOG_QUESTION = "Vuoi effettuare uno spostamento prima di terminare il turno?";

    private final transient MainController controller;
    private final JLabel topLabel;
    private JLabel currentPlayer;
    private final ObjectiveWindow objectiveCard;
    private final CardWindow cardWindow;
    private final JButton objective;
    private final JButton cards;
    private final JButton place;
    private final JButton attack;
    private final JButton endTurn;

    /**
     * Set the top-bar in the relevant label load the buttons in it,
     * and add all to the relevant panel.
     * 
     * @param controller    the MainController
     * @param objectiveCard the ObjectiveCard
     * @param cardWindow    the CardWindow
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
     justification = "MainController and ObjectiveWindow instances are needed on this class by design")
    public TopBarPanel(final MainController controller, final ObjectiveWindow objectiveCard,
            final CardWindow cardWindow) {
        this.objectiveCard = objectiveCard;
        this.controller = controller;
        this.cardWindow = cardWindow;

        BufferedImage imageBar;
        try {
            imageBar = ImageIO.read(ClassLoader.getSystemResource(URL_IMAGE));
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to read the top bar file", e);
        }

        final int width = (int) (WIDTH_RATIO * ViewImpl.getScreenSize().getWidth());
        final int height = (int) (HEIGHT_RATIO * ViewImpl.getScreenSize().getHeight());
        final ImageIcon imageTopBar = ViewImpl.scaleImage(imageBar, width, height);
        this.topLabel = new JLabel(imageTopBar);
        this.topLabel.setLayout(new GridBagLayout());
        this.objective = new JButton(OBJ_BUTTON_NAME);
        this.cards = new JButton("Carte");
        this.place = new JButton(PLACE_BUTTON_NAME);
        this.attack = new JButton(ATK_BUTTON_NAME);
        this.endTurn = new JButton(ENDTURN_BUTTON_NAME);
        this.loadLabel();
        this.topLabel.setBounds(0, 0, width, height);
        this.setLayout(new BorderLayout());

        this.add(topLabel);
        this.setPreferredSize(new Dimension(width, height));
    }

    /**
     * Load the button in the relevant label.
     */
    private void loadLabel() {
        this.objective.addActionListener(e -> this.objectiveCard.showObjectiveCard());
        this.place.addActionListener(e -> this.controller.startGameLoop());
        this.cards.addActionListener(e -> this.cardWindow.showCards());
        this.attack.addActionListener(e -> this.controller.openTerritorySelector());
        this.endTurn.addActionListener(e -> {
            controller.getCurrentPlayer().setAssigned(false);
            final String[] options = { "Si", "No" };
            final int result = JOptionPane.showOptionDialog(this,
                    ENDTURN_DIALOG_QUESTION,
                    ENDTURN_BUTTON_NAME,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    0);
            if (result == 0) {
                controller.setGamePhase(Phase.MOVEMENT_FIRST_PART);
                controller.startGameLoop();
            } else {
                controller.endTurn();
            }
        });
        this.currentPlayer = new JLabel();
        this.currentPlayer.setBackground(new Color(BG_PLAYER_RGB, BG_PLAYER_RGB, BG_PLAYER_RGB));
        this.currentPlayer.setOpaque(true);
        setCurrentPlayer();
        final Font font = new Font("Serif", Font.BOLD, FONT_SIZE);
        this.cards.setFont(font);
        this.objective.setFont(font);
        this.place.setFont(font);
        this.attack.setFont(font);
        this.endTurn.setFont(font);
        this.currentPlayer.setFont(font);
        // CHECKSTYLE: MagicNumber OFF
        // Simple incremental grid x value
        addComponent(currentPlayer, 0, 0);
        addComponent(cards, 1, 0);
        addComponent(objective, 2, 0);
        addComponent(place, 3, 0);
        addComponent(attack, 4, 0);
        addComponent(endTurn, 5, 0);
        // CHECKSTYLE: MagicNumber ON
    }

    private void addComponent(final Component component, final int gridx, final int gridy) {
        final Insets insets = new Insets(8, DISTANCE_BUTTON_L_R, 8, DISTANCE_BUTTON_L_R);
        final var gbc = new GridBagConstraints(gridx, gridy, 1, 1, 0, 0, GridBagConstraints.CENTER,
                GridBagConstraints.CENTER, insets, 0, 0);
        this.topLabel.add(component, gbc);
    }

    private Color getLabelColor(final GameColor color) {
        return switch (color) {
            case RED -> Color.RED;
            case GREEN -> Color.GREEN;
            case BLUE -> Color.BLUE;
            default -> throw new IllegalArgumentException("Invalid color");
        };
    }

    private void setCurrentPlayer() {
        final var currentColor = this.controller.getCurrentPlayer().getColor();
        this.currentPlayer.setForeground(getLabelColor(currentColor));
        this.currentPlayer.setText("Player: " + currentColor.getColorName());
    }

    /**
     * Set the Button Enabled or Disabled based on the Phase of the game.
     */
    private void setActiveButton() {
        final var phase = controller.getGamePhase();
        disableAllJButtons();
        switch (phase) {
            case PLACEMENT -> {
                this.place.setEnabled(true);
            }
            case ATTACK_FIRST_PART -> {
                this.attack.setEnabled(true);
                this.endTurn.setEnabled(true);
            }
            case ATTACK_SECOND_PART -> this.attack.setEnabled(true);
            case MOVEMENT_FIRST_PART, MOVEMENT_SECOND_PART -> this.endTurn.setEnabled(true);
            default -> throw new IllegalArgumentException("Invalid game phase");
        }
    }

    /**
     * Disable all the buttons but the Objective Button.
     */
    private void disableAllJButtons() {
        this.place.setEnabled(false);
        this.attack.setEnabled(false);
        this.endTurn.setEnabled(false);
    }

    /**
     * Update all the Component in the topBarPanel.
     */
    public void updateTopBar() {
        setCurrentPlayer();
        setActiveButton();
    }
}
