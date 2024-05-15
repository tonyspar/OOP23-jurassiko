package it.unibo.jurassiko;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.jurassiko.model.card.api.Card;
import it.unibo.jurassiko.reader.impl.DeckDataReader;

/**
 * Test for the DeckDataReader Class.
 */
class TestDeckDataReader {

    private static final int NUM_CARDS = 23;
    private List<Card> deck;
    private static final String DECK_PATH = "config/deck.json";

    /**
     * Before each test.
     */
    @BeforeEach
    void init() {
        this.deck = new DeckDataReader().readFileData(DECK_PATH);
    }

    @Test
    void testDeckReader() {
        assertNotNull(deck);
        assertFalse(deck.isEmpty());
        assertEquals(NUM_CARDS, deck.size());
    }
}
