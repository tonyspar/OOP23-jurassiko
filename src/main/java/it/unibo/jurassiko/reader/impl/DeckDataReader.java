package it.unibo.jurassiko.reader.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.unibo.jurassiko.model.card.api.Card;
import it.unibo.jurassiko.model.card.api.Card.CardType;
import it.unibo.jurassiko.model.card.impl.CardImpl;
import it.unibo.jurassiko.reader.api.JSONFileReader;

/**
 * Abstract class providing a common implementation to read from a JSON file the
 * card data and converting it into a list of cards.
 */
public class DeckDataReader implements JSONFileReader<List<Card>> {

    private final ObjectMapper mapper;

    /**
     * create an AbstractDeckDataReader.
     */
    public DeckDataReader() {
        this.mapper = new ObjectMapper();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Card> readFileData(final String filePath) {
        final List<Card> deck = new ArrayList<>();
        try (InputStream in = Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(filePath))) {
            final JsonNode jsonNode = this.mapper.readTree(in);
            jsonNode.forEach(t -> {
                final CardType type = CardType.valueOf(t.get("type").asText());
                final String territoryName = t.get("territory").asText();
                deck.add(CardImpl.createCard(type, territoryName));
            });
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to read " + filePath + " file", e);
        }

        Collections.shuffle(deck);
        return new ArrayList<>(deck);
    }

}
