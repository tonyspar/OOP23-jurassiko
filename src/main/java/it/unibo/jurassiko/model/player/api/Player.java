package it.unibo.jurassiko.model.player.api;

import java.util.Map;
import java.util.Set;

import it.unibo.jurassiko.controller.api.MainController;
import it.unibo.jurassiko.model.card.api.Card;
import it.unibo.jurassiko.model.card.api.Card.CardType;
import it.unibo.jurassiko.model.objective.api.Objective;
import it.unibo.jurassiko.model.territory.api.Territory;

/**
 * Interface for the Player.
 */
public interface Player {
    /**
     * Enum for the colors.
     */
    enum GameColor {
        /**
         * Default color represented by Black.
         */
        DEFAULT("Black"),
        /**
         * Color Red.
         */
        RED("Red"),
        /**
         * Color Green.
         */
        GREEN("Green"),
        /**
         * Color Blue.
         */
        BLUE("Blue");

        private final String color;

        /**
         * Constructor for the Colors.
         * 
         * @param color color name
         * 
         */
        GameColor(final String color) {
            this.color = color;
        }

        /**
         * Get the color name.
         * 
         * @return color name
         */
        public String getColorName() {
            return color;
        }
    }

    /**
     * Get the player color.
     * 
     * @return player color
     */
    GameColor getColor();

    /**
     * Get the player Objective.
     * 
     * @return player objective
     */
    Objective getObjective();

    /**
     * Add a {@code Territory} to the player.
     * 
     * @param territory the territory to add
     */
    void addPlayerTerritory(Territory territory);

    /**
     * Remove a {@code Territory} to the player.
     * 
     * @param territory the territory to remove
     */
    void removePlayerTerritory(Territory territory);

    /**
     * Get a Set of {@code Territory} owned by the player.
     * 
     * @return set of player's territories
     */
    Set<Territory> getOwnedTerritories();

    /**
     * Get the groundDino amount to add in each turn.
     * 
     * @return the player Ground Dino amount to add each turn
     */
    int getBonusGroundDino();

    /**
     * Get the waterDino amount to add in each turn.
     * 
     * @return the player Water Dino amount to add each turn
     */
    int getBonusWaterDino();

    /**
     * Get a copy of the player.
     * 
     * @return the copy of the player
     * @throws CloneNotSupportedException
     */
    Player getPlayer() throws CloneNotSupportedException;

    /**
     * Calculates the combination score with a destination deck based on a set of
     * territories.
     * 
     * @param destinationDeck the destination deck where to move the cards of the
     *                        combination
     * @param territories     the set of territories to consider for the bonus
     * @return the combination score
     */
    int combination(MainController destinationDeck, Set<Territory> territories);

        /**
     * @return a map containing the count of each card type in the deck
     */
    Map<CardType, Integer> getTypeMap();

    /**
     * @return true if a card has been assigned to the player during their turn,
     *         false otherwise.
     */
    boolean isAssigned();

    /**
     * Sets whether a card has been assigned to the player during their turn.
     *
     * @param b true to indicate that a card has been assigned to the player during
     *          their turn, false otherwise.
     */
    void setAssigned(boolean b);

       /**
     * Retrieves the card at the specified index in the deck, if present.
     * 
     * @param index the index of the card to retrieve
     * @return an Optional containing the card at the specified index, or an empty
     *         Optional if the index is out of range
     */
    //Optional<Card> getCard(int index);

    /**
     * Adds a card to the deck.
     * 
     * @param card the card to add
     */
    void addCard(Card card);

}
