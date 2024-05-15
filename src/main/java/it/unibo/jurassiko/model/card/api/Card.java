package it.unibo.jurassiko.model.card.api;

/**
 * Represents a card in the game.
 * 
 * A card has a type and may be assigned to a territory.
 */
public interface Card {

    /**
     * Enumeration representing types of cards used in the game.
     */
    enum CardType {
        /**
         * Jack card type.
         */
        JACK,
        /**
         * Horse card type.
         */
        HORSE,
        /**
         * Cannon card type.
         */
        CANNON,
        /**
         * Jolly card type.
         */
        JOLLY;
    }

    /**
     * @return the type of the card
     */
    CardType getType();

    /**
     * @return the territory assigned to the card
     */
    String getTerritory();
}
