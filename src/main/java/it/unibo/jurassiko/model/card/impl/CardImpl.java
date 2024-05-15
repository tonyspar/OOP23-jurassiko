package it.unibo.jurassiko.model.card.impl;

import it.unibo.jurassiko.model.card.api.Card;

/**
 * Represents a card in the game.
 * 
 * A card has a type and may be assigned to a territory.
 */
public final class CardImpl implements Card {

    private String cardTerritory;
    private CardType cardType;

    private CardImpl() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardType getType() {
        return cardType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTerritory() {
        return cardTerritory;
    }

    /**
     * Creates a new card with the specified type and territory.
     * 
     * @param cardType      the type of the card to create
     * @param cardTerritory the territory assigned to the card
     * @return a new card instance
     */
    public static CardImpl createCard(final CardType cardType, final String cardTerritory) {
        final var card = new CardImpl();
        card.cardType = cardType;
        card.cardTerritory = cardTerritory;
        return card;
    }

}
