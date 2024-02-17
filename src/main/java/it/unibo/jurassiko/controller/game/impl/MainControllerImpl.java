package it.unibo.jurassiko.controller.game.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import it.unibo.jurassiko.common.Pair;
import it.unibo.jurassiko.controller.game.api.MainController;
import it.unibo.jurassiko.core.api.GameEngine;
import it.unibo.jurassiko.core.api.GamePhase.Phase;
import it.unibo.jurassiko.core.impl.GameEngineImpl;
import it.unibo.jurassiko.model.battle.api.Battle;
import it.unibo.jurassiko.model.battle.impl.BattleImpl;
import it.unibo.jurassiko.model.borders.api.Border;
import it.unibo.jurassiko.model.borders.impl.BorderImpl;
import it.unibo.jurassiko.model.objective.api.Objective;
import it.unibo.jurassiko.model.objective.impl.ObjectiveFactoryImpl;
import it.unibo.jurassiko.model.player.api.Player;
import it.unibo.jurassiko.model.player.api.Player.GameColor;
import it.unibo.jurassiko.model.player.impl.PlayerImpl;
import it.unibo.jurassiko.model.territory.api.Ocean;
import it.unibo.jurassiko.model.territory.api.Territory;
import it.unibo.jurassiko.model.territory.impl.OceanFactoryImpl;
import it.unibo.jurassiko.model.territory.impl.TerritoryFactoryImpl;
import it.unibo.jurassiko.view.gamescreen.impl.ViewImpl;
import it.unibo.jurassiko.view.window.TerritorySelector;

/**
 * Implementation of the interface {@MainController}.
 */
public class MainControllerImpl implements MainController {

    private static final int MAX_TERRITORIES = 7;
    private static final int START_AMOUNT_DINO = 1;

    private final Set<Territory> allTerritories;
    private final Set<Ocean> oceans;
    private final Set<Objective> objectives;
    private Map<Territory, Pair<GameColor, Integer>> territoriesMap;
    private Optional<Pair<Ocean, GameColor>> currentOcean;

    private final GameEngine game;
    private final TerritorySelector terrSelect;
    private final ViewImpl mainFrame;
    private final Border border;

    private final List<Player> players;
    private Territory attack;
    private Territory defence;
    private final Battle battle;

    /**
     * Costrunctor to initialize and full the mapTerritories and the mapOcean.
     */
    public MainControllerImpl() {
        this.allTerritories = new TerritoryFactoryImpl().createTerritories();
        this.oceans = new OceanFactoryImpl().createOceans();
        this.objectives = new ObjectiveFactoryImpl().createObjectives();
        this.players = new ArrayList<>();
        createPlayers();
        fullTerritories();
        this.currentOcean = Optional.empty();
        this.game = new GameEngineImpl(this);
        this.mainFrame = new ViewImpl(this);
        this.terrSelect = new TerritorySelector(this);
        this.border = new BorderImpl();
        this.battle = new BattleImpl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openTerritorySelector() {
        this.terrSelect.display();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeTerritorySelector() {
        this.terrSelect.closeView();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openObjectiveCard() {
        this.mainFrame.displayObjective();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openView() {
        mainFrame.display();
        updateBoard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateBoard() {
        mainFrame.updatePanel();
        this.terrSelect.updateButtons();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void manageSelection(final String territory) {
        final var colorCurrentPlayer = game.getCurrentPlayerTurn().getColor();
        switch (this.game.getGamePhase()) {
            case PLACEMENT:
                placeGroundDino(territory, START_AMOUNT_DINO);
                break;
            case ATTACK_FIRST_PART:
                attack = getMapTerritoryKey(territory);
                break;
            case ATTACK_SECOND_PART:
                defence = getMapTerritoryKey(territory);
                final var pairattack = getMapTerritoryValue(attack.getName());
                final var pairdefence = getMapTerritoryValue(defence.getName());
                final var deaths = battle.attack(pairattack.y(), pairdefence.y(), calculateDice(pairattack.y()),
                        calculateDice(pairdefence.y()));
                placeGroundDino(attack.getName(), -deaths.x());
                placeGroundDino(defence.getName(), -deaths.y());
                if (getMapTerritoryValue(defence.getName()).y() <= 0) {
                    final int dinoToMove = calculateDinoToMove(getMapTerritoryValue(attack.getName()).y());
                    final Pair<GameColor, Integer> defReplacement = new Pair<>(colorCurrentPlayer, dinoToMove);
                    final var loserColor = getMapTerritoryValue(territory).x();
                    for (final var player : players) {
                        if (player.getColor().equals(colorCurrentPlayer)) {
                            player.addPlayerTerritory(getMapTerritoryKey(territory));
                        } else if (player.getColor().equals(loserColor)) {
                            player.removePlayerTerritory(getMapTerritoryKey(territory));
                        }
                    }
                    territoriesMap.replace(defence, defReplacement);
                    placeGroundDino(attack.getName(), -dinoToMove);
                }
                updateBoard();
                break;
            case MOVEMENT_FIRST_PART:

                break;
            default:
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getCurrentPlayer() {
        return game.getCurrentPlayerTurn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFirstTurn() {
        return game.isFirstTurn();
    }

    /**
     * {@inheritDoc}
     */
    public Set<Territory> getTerritories(final GameColor color) {
        final var setTerr = territoriesMap.entrySet()
                .stream()
                .filter(s -> s.getValue().x().equals(color))
                .map(s -> s.getKey())
                .collect(Collectors.toSet());
        return setTerr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Player> getPlayers() throws CloneNotSupportedException {
        return new ArrayList<>(players);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase getGamePhase() {
        return game.getGamePhase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalClick() {
        return terrSelect.getTotalClick();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetTotalClick() {
        terrSelect.resetTotalClick();
    }

    /**
     * Adds an amount of dino at the territory in the map.
     * 
     * @param territoryName name of the territory
     * @param amount        amount of dino to add at the map
     */
    private void placeGroundDino(final String territoryName, final int amount) {
        if (getOceanByName(territoryName).isPresent()) {
            placeWaterDino(territoryName);
            return;
        }
        final var temp = getMapTerritoryValue(territoryName);
        final var newPair = new Pair<>(temp.x(), temp.y() + amount);
        territoriesMap.replace(getMapTerritoryKey(territoryName), newPair);
    }

    /**
     * Puts the color of the player in the correct ocean.
     * 
     * @param oceanName name of the ocean
     */
    private void placeWaterDino(final String oceanName) {
        this.currentOcean = Optional.of(new Pair<Ocean, GameColor>(getOceanByName(oceanName).get(),
                this.game.getCurrentPlayerTurn().getColor()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endTurn() {
        game.endTurn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGamePhase(final Phase phase) {
        game.setGamePhase(phase);
    }

    /**
     * Given a name return the value.
     * 
     * @param territoryName name of the territory
     * @return the color and the amount of dino
     */
    private Pair<GameColor, Integer> getMapTerritoryValue(final String territoryName) {
        return territoriesMap.entrySet().stream()
                .filter(t -> t.getKey().getName().equals(territoryName))
                .map(t -> t.getValue())
                .findFirst()
                .get();
    }

    /**
     * Given a name return the key.
     * 
     * @param territoryName name of the territory
     * @return key of the map
     */
    private Territory getMapTerritoryKey(final String territoryName) {
        return territoriesMap.entrySet().stream()
                .filter(t -> t.getKey().getName().equals(territoryName))
                .map(t -> t.getKey())
                .findFirst()
                .get();
    }

    /**
     * Given a name return an Optional.
     * 
     * @param oceanName namme of the ocean
     * @return optional of ocean
     */
    private Optional<Ocean> getOceanByName(final String oceanName) {
        return this.oceans.stream()
                .filter(o -> o.getName().equals(oceanName))
                .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    public Map<Territory, Pair<GameColor, Integer>> getTerritoriesMap() {
        return Map.copyOf(territoriesMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Pair<Ocean, GameColor>> getCurrentOcean() {
        return this.currentOcean.isPresent() ? Optional.of(new Pair<>(this.currentOcean.get())) : Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    public void startGameLoop() {
        this.game.startGameLoop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAllyTerritory(final String territoryName) {
        final var currentColor = this.game.getCurrentPlayerTurn().getColor();
        return getColorTerritory(territoryName).equals(currentColor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAllyTerritoryWithMoreThanOne(final String territoryName) {
        return isAllyTerritory(territoryName) && getMapTerritoryValue(territoryName).y() > 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAdjEnemy(final String territoryName) {
        return supportHasAdj(territoryName, t -> !t.x().equals(game.getCurrentPlayerTurn().getColor()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAdjAlly(final String territoryName) {
        return supportHasAdj(territoryName, t -> t.x().equals(game.getCurrentPlayerTurn().getColor()));
    }

    /**
     * Support method.
     * 
     * @param territoryName territory name
     * @param condition     condition
     * @return true if the condition is verified, false otherwise
     */
    private boolean supportHasAdj(final String territoryName, final Predicate<Pair<GameColor, Integer>> condition) {
        for (final var temp : getAdj(territoryName)) {
            if (condition.test(getMapTerritoryValue(temp))) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getAdj(final String territoryName) {
        return border.getTerritoriesBorder(getMapTerritoryKey(territoryName), currentOcean.get().x());
    }

    /**
     * Returns the color of the territory that we passed as input.
     * 
     * @param terr input territory name
     * @return Color of the Territory
     */
    private GameColor getColorTerritory(final String terr) {
        for (final var player : players) {
            for (final var temp : player.getOwnedTerritories()) {
                if (temp.getName().equals(terr)) {
                    return player.getColor();
                }
            }
        }
        throw new IllegalArgumentException("Invalid territory name");
    }

    /**
     * Create all the players using the methods that we have created,
     * inside the constructor.
     */
    private void createPlayers() {
        final Set<Territory> copyTerritories = new HashSet<>(this.allTerritories);
        final Set<Objective> copyObjectives = new HashSet<>(this.objectives);
        final var redPlayer = new PlayerImpl(GameColor.RED,
                shuffleObjective(copyObjectives),
                shuffleTerritories(copyTerritories, MAX_TERRITORIES),
                Set.of());
        final var greenPlayer = new PlayerImpl(GameColor.GREEN,
                shuffleObjective(copyObjectives),
                shuffleTerritories(copyTerritories, MAX_TERRITORIES),
                Set.of());
        final var bluePlayer = new PlayerImpl(GameColor.BLUE,
                shuffleObjective(copyObjectives),
                shuffleTerritories(copyTerritories, MAX_TERRITORIES),
                Set.of());
        players.add(redPlayer);
        players.add(greenPlayer);
        players.add(bluePlayer);
    }

    /**
     * Shuffle all the territories and return 7 territories
     * for the current player.
     * 
     * @param territories    all territories in the game
     * @param maxTerritories max territories for each player
     * @return a set of 7 territories
     */
    private Set<Territory> shuffleTerritories(final Set<Territory> territories, final int maxTerritories) {
        final List<Territory> territoryList = new ArrayList<>(territories);
        Collections.shuffle(territoryList);
        final Set<Territory> temp = territoryList.stream()
                .limit(maxTerritories)
                .collect(Collectors.toSet());
        territories.removeAll(temp);
        return temp;
    }

    /**
     * Return a single objective for the current player and
     * remove it from the set.
     * 
     * @param objective all of the objectives
     * @return a single objective for the corresponding player
     */
    private Objective shuffleObjective(final Set<Objective> objectives) {
        final List<Objective> objectiveList = new ArrayList<>(objectives);
        Collections.shuffle(objectiveList);
        final Objective temp = objectiveList.get(0);
        objectives.remove(temp);
        return temp;
    }

    /**
     * Fill the mapTerritories with the territories and
     * the corresponding color and initial amout.
     */
    private void fullTerritories() {
        territoriesMap = new HashMap<>();
        allTerritories.stream()
                .forEach(terr -> territoriesMap.put(terr,
                        new Pair<>(getColorTerritory(terr.getName()), START_AMOUNT_DINO)));
    }

    // TODO: add javaDoc, remove if you don't want to
    private int calculateDice(final int dinoAmount) {
        if (dinoAmount >= 3) {
            return 3;
        } else {
            return dinoAmount;
        }
    }

    // TODO: add javaDoc, remove if you don't want to
    private int calculateDinoToMove(final int dinoAmount) {
        if (dinoAmount > 3) {
            return 3;
        } else {
            return dinoAmount - 1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getSelectedTerritory() {
        return terrSelect.getSelectedTerritory();
    }
}
