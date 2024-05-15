package it.unibo.jurassiko.model.player.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.jurassiko.common.Pair;
import it.unibo.jurassiko.controller.api.MainController;
import it.unibo.jurassiko.model.card.api.Card;
import it.unibo.jurassiko.model.card.api.Card.CardType;
import it.unibo.jurassiko.model.objective.api.Objective;
import it.unibo.jurassiko.model.player.api.Player;
import it.unibo.jurassiko.model.territory.api.Territory;
import it.unibo.jurassiko.model.territory.impl.TerritoryFactoryImpl;

/**
 * Implementation of the interface {@link Player}.
 */
public class PlayerImpl implements Player, Cloneable {

    private static final int MIN_CARDS_FOR_COMBINATION = 3;
    private static final int RESULT_COMBINATION_WITH_TREE_DIFFERENT = 5;
    private static final int COMBINATION_WITH_TREE_EQUALS = 3;
    private static final int JOKER_RESULT = 6;
    private static final int COMBINATION_WITH_TREE_DIFFERENT = 1;
    private static final int MIN_JOLLY = 1;
    private static final int COMBINATION_WITH_JOLLY = 2;
    private final Logger logger = LoggerFactory.getLogger(PlayerImpl.class);
    private final GameColor color;
    private final Objective objective;
    private final Set<Territory> territories;
    private final List<Card> deck;
    private final Set<Territory> totalTerritories = new TerritoryFactoryImpl().createTerritories();
    private static final Pair<String, Integer> NORD_AMERICA = new Pair<>("Nord America", 3);
    private static final Pair<String, Integer> GONDWANA_OCCIDENTALE = new Pair<>("Gondwana Occidentale", 5);
    private static final Pair<String, Integer> GONDWANA_ORIENTALE = new Pair<>("Gondwana Orientale", 3);
    private static final Pair<String, Integer> EUROASIA = new Pair<>("Eurasia", 6);
    private final Map<CardType, Integer> typeMap;
    private boolean assigned;

    /**
     * Constructor for the player.
     * 
     * @param color       player's color
     * @param objective   player's objective
     * @param territories player's owned territories
     */
    public PlayerImpl(final GameColor color,
            final Objective objective,
            final Set<Territory> territories) {
        this.color = color;
        Objects.requireNonNull(objective);
        this.objective = objective.getClone();
        this.territories = new HashSet<>(Objects.requireNonNull(territories));
        this.typeMap = new HashMap<>();
        this.deck = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameColor getColor() {
        return color;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Objective getObjective() {
        return objective.getClone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPlayerTerritory(final Territory territory) {
        territories.add(territory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePlayerTerritory(final Territory territory) {
        if (territories.contains(territory)) {
            territories.remove(territory);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Territory> getOwnedTerritories() {
        return Set.copyOf(territories);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBonusGroundDino() {
        int result = 0;
        result += bonusContinent(
                new Pair<Set<Territory>, Integer>(getContinent(NORD_AMERICA.x()), NORD_AMERICA.y()));
        result += bonusContinent(
                new Pair<Set<Territory>, Integer>(getContinent(GONDWANA_OCCIDENTALE.x()), GONDWANA_OCCIDENTALE.y()));
        result += bonusContinent(
                new Pair<Set<Territory>, Integer>(getContinent(GONDWANA_ORIENTALE.x()), GONDWANA_ORIENTALE.y()));
        result += bonusContinent(
                new Pair<Set<Territory>, Integer>(getContinent(EUROASIA.x()), EUROASIA.y()));
        return territories.size() / 2 + result;
    }

    /**
     * Used to check if the player has all the territory of a certain continent.
     * 
     * @param pair Pair of a Set or Territory and integer
     * @return the value of the bonus ground dino based on the continent you have
     */
    private int bonusContinent(final Pair<Set<Territory>, Integer> pair) {
        int result = 0;
        final Set<String> temp = territories.stream().map(t -> t.getName().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        if (temp.containsAll(pair.x().stream()
                .map(t -> t.getName().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet()))) {
            result = result + pair.y();
        }
        return result;
    }

    /**
     * @param name name of the continent
     * @return Set of the territory with the same continent name
     */
    private Set<Territory> getContinent(final String name) {
        return totalTerritories.stream()
                .filter(e -> e.getContinent().toLowerCase(Locale.ROOT).equals(name.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBonusWaterDino() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getPlayer() {
        try {
            return (Player) this.clone();
        } catch (final CloneNotSupportedException e) {
            this.logger.error("Cannot create a copy of the player");
        }
        throw new IllegalStateException("Failed to create a copy of the player");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCard(final Card card) {
        updateTypeMap(card.getType(), true);
        this.deck.add(card);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int combination(final MainController destinationDeck, final Set<Territory> territories) {
        final int bonusTerritory = calculateBonusTerritory(territories);
        final int nJack = typeMap.getOrDefault(CardType.JACK, 0);
        final int nHorse = typeMap.getOrDefault(CardType.HORSE, 0);
        final int nCannon = typeMap.getOrDefault(CardType.CANNON, 0);
        final int nJolly = typeMap.getOrDefault(CardType.JOLLY, 0);
        final int totalCards = nJack + nHorse + nCannon + nJolly;

        if (totalCards >= MIN_CARDS_FOR_COMBINATION) {
            if (nJolly >= MIN_JOLLY) {
                if (nJack >= COMBINATION_WITH_JOLLY) {
                    moveBetweenDeck(destinationDeck, CardType.JOLLY, MIN_JOLLY);
                    moveBetweenDeck(destinationDeck, CardType.JACK, COMBINATION_WITH_JOLLY);
                    return JOKER_RESULT + bonusTerritory;
                } else if (nHorse >= COMBINATION_WITH_JOLLY) {
                    moveBetweenDeck(destinationDeck, CardType.JOLLY, MIN_JOLLY);
                    moveBetweenDeck(destinationDeck, CardType.HORSE, 2);
                    return JOKER_RESULT + bonusTerritory;
                } else if (nCannon >= COMBINATION_WITH_JOLLY) {
                    moveBetweenDeck(destinationDeck, CardType.JOLLY, MIN_JOLLY);
                    moveBetweenDeck(destinationDeck, CardType.CANNON, COMBINATION_WITH_JOLLY);
                    return JOKER_RESULT + bonusTerritory;
                }
            } else if (nJack >= COMBINATION_WITH_TREE_DIFFERENT && nHorse >= COMBINATION_WITH_TREE_DIFFERENT
                    && nCannon >= COMBINATION_WITH_TREE_DIFFERENT) {
                moveBetweenDeck(destinationDeck, CardType.CANNON, COMBINATION_WITH_TREE_DIFFERENT);
                moveBetweenDeck(destinationDeck, CardType.JACK, COMBINATION_WITH_TREE_DIFFERENT);
                moveBetweenDeck(destinationDeck, CardType.HORSE, COMBINATION_WITH_TREE_DIFFERENT);
                return RESULT_COMBINATION_WITH_TREE_DIFFERENT + bonusTerritory;
            } else if (nHorse >= COMBINATION_WITH_TREE_EQUALS) {
                moveBetweenDeck(destinationDeck, CardType.HORSE, COMBINATION_WITH_TREE_EQUALS);
                return 4 + bonusTerritory;
            } else if (nJack >= COMBINATION_WITH_TREE_EQUALS) {
                moveBetweenDeck(destinationDeck, CardType.JACK, COMBINATION_WITH_TREE_EQUALS);
                return 3 + bonusTerritory;
            } else if (nCannon >= COMBINATION_WITH_TREE_EQUALS) {
                moveBetweenDeck(destinationDeck, CardType.CANNON, COMBINATION_WITH_TREE_EQUALS);
                return 2 + bonusTerritory;
            }
        }
        return 0;
    }

    /**
     * Calculates the bonus territory score based on a set of territories.
     * 
     * @param territories the set of territories to consider for the bonus
     * @return the bonus territory score
     */
    private int calculateBonusTerritory(final Set<Territory> territories) {
        final Set<String> temp = territories.stream().map(t -> t.getName().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        int i = 0;
        for (final var element : deck) {
            if (temp.contains(element.getTerritory())) {
                i++;
            }
        }
        return i;
    }

    /**
     * Method to update the count of cards of a specific type in the deck.
     * 
     * @param type      The type of card to update the count for.
     * @param increment True if you want to increment the count, false to decrement
     *                  it.
     */
    private void updateTypeMap(final CardType type, final boolean increment) {
        int cardCount = this.typeMap.getOrDefault(type, 0);
        if (increment) {
            this.typeMap.put(type, ++cardCount);
        } else {
            this.typeMap.put(type, --cardCount);
        }
    }

    /**
     * Moves a specified number of cards of a given type from this deck to a
     * destination deck.
     * 
     * @param destinationDeck the destination deck to move the cards to
     * @param type            the type of cards to move
     * @param n               the number of cards to move
     */
    private void moveBetweenDeck(final MainController destinationDeck, final CardType type, final int n) {
        final var iterator = this.deck.iterator();
        int cardsMoved = 0;
        while (iterator.hasNext() && cardsMoved < n) {
            final Card card = iterator.next();
            if (card.getType() == type) {
                destinationDeck.addCard(card);
                updateTypeMap(type, false);
                iterator.remove();
                cardsMoved++;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAssigned() {
        return this.assigned;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAssigned(final boolean assigned) {
        this.assigned = assigned;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<CardType, Integer> getTypeMap() {
        return Map.copyOf(this.typeMap);
    }

}
