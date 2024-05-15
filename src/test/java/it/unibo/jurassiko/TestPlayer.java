package it.unibo.jurassiko;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.jurassiko.controller.api.MainController;
import it.unibo.jurassiko.controller.impl.MainControllerImpl;
import it.unibo.jurassiko.model.card.api.Card.CardType;
import it.unibo.jurassiko.model.card.impl.CardImpl;
import it.unibo.jurassiko.model.objective.api.Objective;
import it.unibo.jurassiko.model.objective.impl.ObjectiveFactoryImpl;
import it.unibo.jurassiko.model.player.api.Player;
import it.unibo.jurassiko.model.player.impl.PlayerImpl;
import it.unibo.jurassiko.model.territory.api.Territory;
import it.unibo.jurassiko.model.territory.impl.TerritoryFactoryImpl;

/**
 * Test class to test Player.
 */
class TestPlayer {

    private Player player;
    private final MainController controller = new MainControllerImpl();
    private final Set<Territory> territory = new TerritoryFactoryImpl().createTerritories();
    private final Set<Objective> objective = new ObjectiveFactoryImpl().createObjectives();
    private final Set<Territory> territoryforcombination = new HashSet<>();
    private static final String MESSICO = "messico";
    private static final int COMBINATION_WITH_TREE_DIFFERENT = 5;
    private static final int COMBINATION_WITH_TREE_HORSE = 4;
    private static final int COMBINATION_WITH_TREE_CANNON = 2;
    private static final int COMBINATION_WITH_TREE_JACK = 3;
    private static final int RESULT_OF_COMBINATION_WITH_JOLLY = 6;

    @BeforeEach
    void initPlayer() {
        player = new PlayerImpl(Player.GameColor.RED, objective.stream().findFirst().get(), new HashSet<>());
    }

    @Test
    void testTerritories() {
        final var iterator = territory.iterator();
        while (iterator.hasNext()) {
            final var temp = iterator.next();
            player.removePlayerTerritory(temp);
            if ("Cina".equals(temp.getName())) {
                player.addPlayerTerritory(temp);
                assertEquals(player.getOwnedTerritories(), Set.of(temp));
                player.removePlayerTerritory(temp);
                assertEquals(player.getOwnedTerritories(), Set.of());
            }
        }
    }

    @Test
    void testGetPlayer() throws CloneNotSupportedException {
        assertEquals(player.getColor(), Player.GameColor.RED);
        final Player temp = player.getPlayer();
        assertEquals(temp.getColor(), Player.GameColor.RED);
        assertNotEquals(temp, player);
    }

    @Test
    void testGetObjective() {
        final var temp = player.getObjective();
        assertNotEquals(temp, objective.stream().findFirst().get());
        assertNotEquals(temp, player.getObjective());
    }

    @Test
    void testGetBonus() {
        player.addPlayerTerritory(getTerritory("Groenlandia"));
        player.addPlayerTerritory(getTerritory("Canada"));
        assertEquals(1, player.getBonusGroundDino());
        player.addPlayerTerritory(getTerritory("Messico"));
        assertEquals(1, player.getBonusGroundDino());
        player.addPlayerTerritory(getTerritory("Appalachia"));
        // CHECKSTYLE: MagicNumber OFF
        // Test purpuse
        assertEquals(5, player.getBonusGroundDino());
        // CHECKSTYLE: MagicNumber ON
    }

    /**
     * @param name name of the Territory
     * @return the territory based of the name
     */
    private Territory getTerritory(final String name) {
        final var result = territory.stream()
                .filter(e -> e.getName().toLowerCase(Locale.ROOT).equals(name.toLowerCase(Locale.ROOT)))
                .findFirst();
        return result.get();
    }

    @Test
    void testCombination() {
        player.addCard(CardImpl.createCard(CardType.JOLLY, MESSICO));
        player.addCard(CardImpl.createCard(CardType.JACK, MESSICO));
        player.addCard(CardImpl.createCard(CardType.JACK, MESSICO));
        assertEquals(RESULT_OF_COMBINATION_WITH_JOLLY, player.combination(controller, territoryforcombination));
        assertEquals(0, player.getTypeMap().get(CardType.JOLLY));
        assertEquals(0, player.getTypeMap().get(CardType.JACK));
        for (int i = 0; i < 3; i++) {
            player.addCard(CardImpl.createCard(CardType.JACK, MESSICO));
        }
        assertEquals(COMBINATION_WITH_TREE_JACK, player.combination(controller, territoryforcombination));
        for (int i = 0; i < 3; i++) {
            player.addCard(CardImpl.createCard(CardType.CANNON, MESSICO));
        }
        assertEquals(COMBINATION_WITH_TREE_CANNON, player.combination(controller, territoryforcombination));
        for (int i = 0; i < 3; i++) {
            player.addCard(CardImpl.createCard(CardType.HORSE, MESSICO));
        }
        assertEquals(COMBINATION_WITH_TREE_HORSE, player.combination(controller, territoryforcombination));
        player.addCard(CardImpl.createCard(CardType.HORSE, MESSICO));
        player.addCard(CardImpl.createCard(CardType.CANNON, MESSICO));
        player.addCard(CardImpl.createCard(CardType.JACK, MESSICO));
        assertEquals(COMBINATION_WITH_TREE_DIFFERENT, player.combination(controller, territoryforcombination));
    }
}
