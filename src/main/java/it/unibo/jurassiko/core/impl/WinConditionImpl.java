package it.unibo.jurassiko.core.impl;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import it.unibo.jurassiko.common.Pair;
import it.unibo.jurassiko.core.api.WinCondition;
import it.unibo.jurassiko.model.territory.api.Territory;
import it.unibo.jurassiko.model.objective.api.Objective;
import it.unibo.jurassiko.model.objective.impl.ConquerContinentsObjective;
import it.unibo.jurassiko.model.objective.impl.ConquerTerritoriesObjective;
import it.unibo.jurassiko.model.objective.impl.DestroyArmyObjective;
import it.unibo.jurassiko.model.objective.impl.ObjectiveFactoryImpl;
import it.unibo.jurassiko.model.player.api.Player;
import it.unibo.jurassiko.model.player.api.Player.GameColor;

/**
 * Implementation of {@link WinCondition} interface.
 */
public class WinConditionImpl implements WinCondition {

    private static final int DEFAULT_NUM_TERRITORIES = 12;

    private Optional<Player> winner;
    private ConquerTerritoriesObjective defaultObjective;

    /**
     * Creates a WinCondition object.
     */
    public WinConditionImpl() {
        this.winner = Optional.empty();
        this.defaultObjective = getDefaultObjective();
    }

    /**
     * Checks if some player has completed their objective and if that is the case
     * that player is returned.
     *
     * @param territoriesMap the map representing the owner of all territories.
     * @param player         the player to be checked.
     * @param objective      the objective of the player to check.
     * @return an Optional containing the winning player if there is one, or an
     *         empty Optional otherwise.
     */
    @Override
    public Optional<Player> getWinner(final Map<Territory, Pair<GameColor, Integer>> territoriesMap,
            final Player player,
            final Objective objective) {
        checkWinCondition(territoriesMap, player, objective);
        return winner;
    }

    private void checkWinCondition(final Map<Territory, Pair<GameColor, Integer>> territoriesMap, final Player player,
            final Objective objective) {
        final var playerColor = player.getColor();
        switch (objective.getType()) {
            case "conquerContinents" -> {
                final var continentsObjective = (ConquerContinentsObjective) objective;
                setWinner(checkConquerContinents(territoriesMap, playerColor, continentsObjective), player);
            }
            case "conquerTerritories" -> {
                final var territoriesObjective = (ConquerTerritoriesObjective) objective;
                setWinner(checkConquerTerritories(territoriesMap, playerColor, territoriesObjective), player);
            }
            case "destroyArmy" -> {
                final var armyObjective = (DestroyArmyObjective) objective;
                setWinner(checkDestroyArmy(territoriesMap, playerColor, armyObjective), player);
            }
            default -> throw new IllegalArgumentException("Invalid objective type");
        }
    }

    private void setWinner(final boolean winCondition, final Player player) {
        if (winCondition) {
            this.winner = Optional.of(player);
        }
    }

    private boolean checkConquerContinents(final Map<Territory, Pair<GameColor, Integer>> territoriesMap,
            final GameColor playerColor,
            final ConquerContinentsObjective objective) {
        final Set<String> continents = objective.getContinents();

        final boolean continentsConquered = territoriesMap.entrySet().stream()
                .filter(t -> {
                    final String continent = t.getKey().getContinent();
                    return continents.contains(continent);
                })
                .allMatch(t -> t.getValue().x().equals(playerColor));

        boolean selectableContinentConquered = true;
        if (objective.isSelectableContinent()) {
            Set<String> selectableContinents = territoriesMap.keySet().stream()
                    .map(Territory::getContinent)
                    .distinct()
                    .filter(t -> !continents.contains(t))
                    .collect(Collectors.toSet());

            selectableContinentConquered = selectableContinents.stream()
                    .anyMatch(c -> territoriesMap.entrySet().stream()
                            .filter(t -> t.getKey().getContinent().equals(c))
                            .allMatch(t -> t.getValue().x().equals(playerColor)));
        }

        return continentsConquered && selectableContinentConquered;
    }

    private boolean checkConquerTerritories(final Map<Territory, Pair<GameColor, Integer>> territoriesMap,
            final GameColor playerColor,
            final ConquerTerritoriesObjective objective) {
        final int territoryAmount = objective.getNumTerritories();
        final int minDinos = objective.getMinDinos();
        final boolean numberReached = territoriesMap.values().stream()
                .filter(t -> t.x().equals(playerColor))
                .filter(t -> t.y() >= minDinos)
                .count() >= territoryAmount;

        return numberReached;
    }

    private boolean checkDestroyArmy(final Map<Territory, Pair<GameColor, Integer>> territoriesMap,
            final GameColor playerColor,
            final DestroyArmyObjective objective) {
        final var armyObjective = (DestroyArmyObjective) objective;
        final var armyColor = armyObjective.getArmyColor();

        boolean checkColorPresence = false;

        // If the player must destroy theirself, the objective becomes the default one
        checkColorPresence = armyColor.equals(playerColor)
                ? checkConquerTerritories(territoriesMap, playerColor, this.defaultObjective)
                : territoriesMap.values().stream().noneMatch(p -> p.x().equals(armyColor));

        return checkColorPresence;
    }

    private ConquerTerritoriesObjective getDefaultObjective() {
        Set<Objective> allObjectives = new ObjectiveFactoryImpl().createObjectives();
        var defaultObjective = allObjectives.stream()
                .filter(ConquerTerritoriesObjective.class::isInstance)
                .map(ConquerTerritoriesObjective.class::cast)
                .filter(t -> t.getNumTerritories() == DEFAULT_NUM_TERRITORIES)
                .findFirst()
                .get();
        return defaultObjective;
    }

}
