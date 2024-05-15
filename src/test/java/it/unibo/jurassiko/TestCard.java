package it.unibo.jurassiko;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.jurassiko.model.card.api.Card;
import it.unibo.jurassiko.model.card.api.Card.CardType;
import it.unibo.jurassiko.model.card.impl.CardImpl;

/**
 * Test for the Card Class.
 */
class TestCard {

    private Card card;

    /**
     * before each test.
     */
    @BeforeEach
    void init() {
        this.card = CardImpl.createCard(CardType.JACK, "Messico");
    }

    @Test
    void testGetTerritory() {
        assertEquals(CardType.JACK, card.getType());
    }

    @Test
    void testGetType() {
        assertEquals("Messico", card.getTerritory());
    }
}
