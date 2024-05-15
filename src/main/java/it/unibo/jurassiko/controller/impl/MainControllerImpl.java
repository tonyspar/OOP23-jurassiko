package it.unibo.jurassiko.controller.impl;

import java.awt.BorderLayout;
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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.jurassiko.common.Pair;
import it.unibo.jurassiko.controller.api.MainController;
import it.unibo.jurassiko.core.api.GameEngine;
import it.unibo.jurassiko.core.api.GamePhase.Phase;
import it.unibo.jurassiko.core.impl.GameEngineImpl;
import it.unibo.jurassiko.model.battle.api.Battle;
import it.unibo.jurassiko.model.battle.impl.BattleImpl;
import it.unibo.jurassiko.model.borders.api.Border;
import it.unibo.jurassiko.model.borders.impl.BorderImpl;
import it.unibo.jurassiko.model.card.api.Card;
import it.unibo.jurassiko.model.objective.api.Objective;
import it.unibo.jurassiko.model.objective.impl.ObjectiveFactoryImpl;
import it.unibo.jurassiko.model.player.api.Player;
import it.unibo.jurassiko.model.player.api.Player.GameColor;
import it.unibo.jurassiko.model.player.impl.PlayerImpl;
import it.unibo.jurassiko.model.territory.api.Ocean;
import it.unibo.jurassiko.model.territory.api.Territory;
import it.unibo.jurassiko.model.territory.impl.OceanFactoryImpl;
import it.unibo.jurassiko.model.territory.impl.TerritoryFactoryImpl;
import it.unibo.jurassiko.reader.impl.DeckDataReader;
import it.unibo.jurassiko.view.gamescreen.impl.ViewImpl;
import it.unibo.jurassiko.view.panels.SpriteLoader;
import it.unibo.jurassiko.view.windows.TerritorySelector;

/**
 * Implementation of the interface {@MainController}.
 */
public class MainControllerImpl implements MainController {

    private static final int SELECTOR_HGAP = 15;
    private static final int MAX_TERRITORIES = 7;
    private static final int START_AMOUNT_DINO = 1;
    private static final String DECK_PATH = "config/deck.json";

    private final Set<Territory> allTerritories;
    private final Set<Ocean> oceans;
    private final Set<Objective> objectives;
    private Map<Territory, Pair<GameColor, Integer>> territoriesMap;
    private Optional<Pair<Ocean, GameColor>> currentOcean;

    private final GameEngine game;
    private final TerritorySelector terrSelect;
    private final ViewImpl mainFrame;
    private final Border border;
    private final List<Card> deck;
    private final List<Player> players;

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
        this.terrSelect = new TerritorySelector(this);
        this.mainFrame = new ViewImpl(this);
        this.border = new BorderImpl();
        this.battle = new BattleImpl();
        this.deck = new DeckDataReader().readFileData(DECK_PATH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public List<Player> getPlayers() throws CloneNotSupportedException {
        return new ArrayList<>(players);
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
    public void updateBoard() {
        this.mainFrame.updatePanel();
        this.terrSelect.updateButtons();
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
    @SuppressFBWarnings(value = "DM_EXIT", justification = "Shut down program after closing the main frame")
    public void closeGame() {
        this.mainFrame.dispose();
        System.exit(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showWinnerName(final GameColor winner) {
        final var dinoSprites = new SpriteLoader().getDinoSprites();

        final ImageIcon winnerSprite = dinoSprites.get(winner);
        final String message = "Il giocatore " + colorToString(winner) + " ha vinto!";

        final JPanel winnerPanel = new JPanel(new BorderLayout());
        final JLabel spriteLabel = new JLabel(winnerSprite);
        final JLabel messageLabel = new JLabel(message);
        winnerPanel.add(spriteLabel, BorderLayout.WEST);
        winnerPanel.add(messageLabel, BorderLayout.EAST);

        JOptionPane.showMessageDialog(this.mainFrame, winnerPanel, "Fine del gioco", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startGameLoop() {
        this.game.startGameLoop();
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
    public void setGamePhase(final Phase phase) {
        game.setGamePhase(phase);
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
    @Override
    public void endTurn() {
        game.endTurn();
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
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAdj(final String territoryName) {
        return border.getTerritoriesBorder(getMapTerritoryKey(territoryName), currentOcean.get().x());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Card> assignment() {
            final Card card = this.deck.get(0);
            this.deck.remove(card);
            return Optional.of(card);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void manageSelection(final String territory) {
        final var colorCurrentPlayer = game.getCurrentPlayerTurn().getColor();
        switch (this.game.getGamePhase()) {
            case PLACEMENT -> {
                changeGroundDinoAmount(territory, START_AMOUNT_DINO);
            }
            case ATTACK_FIRST_PART -> {
            }
            case ATTACK_SECOND_PART -> {
                boolean conquestSuccesful = false;
                final var attacker = terrSelect.getSelectedTerritory().get();
                final var defender = getMapTerritoryKey(territory);
                final var pairAttack = getMapTerritoryValue(terrSelect.getSelectedTerritory().get());
                final var pairDefence = getMapTerritoryValue(defender.getName());
                final var deaths = battle.attack(pairAttack.y(), pairDefence.y(),
                        battle.calculateDino(pairAttack.y(), true),
                        battle.calculateDino(pairDefence.y(), false));
                changeGroundDinoAmount(attacker, -deaths.x());
                changeGroundDinoAmount(defender.getName(), -deaths.y());
                if (getMapTerritoryValue(defender.getName()).y() <= 0) {
                    conquestSuccesful = true;
                    final var card = assignment();
                    if (!game.getCurrentPlayerTurn().isAssigned() && !card.isEmpty()) {
                        game.getCurrentPlayerTurn().setAssigned(true);
                        game.getCurrentPlayerTurn().addCard(card.get());
                    }
                    final int dinoToMove = battle.calculateDino(getMapTerritoryValue(attacker).y(), true);
                    final Pair<GameColor, Integer> defReplacement = new Pair<>(colorCurrentPlayer, dinoToMove);
                    final var loserColor = getMapTerritoryValue(territory).x();
                    for (final var player : players) {
                        if (player.getColor().equals(colorCurrentPlayer)) {
                            player.addPlayerTerritory(getMapTerritoryKey(territory));
                        } else if (player.getColor().equals(loserColor)) {
                            player.removePlayerTerritory(getMapTerritoryKey(territory));
                        }
                    }
                    territoriesMap.replace(defender, defReplacement);
                    changeGroundDinoAmount(attacker, -dinoToMove);
                }
                updateBoard();
                showBattleOutcome(attacker, deaths.x(), territory, deaths.y(), conquestSuccesful);
            }
            case MOVEMENT_FIRST_PART -> {
            }
            case MOVEMENT_SECOND_PART -> {
                final var firstSelected = terrSelect.getSelectedTerritory().get();
                final var secondSelected = getMapTerritoryKey(territory);
                final int amount = showDinoAmountSelector(firstSelected, territory,
                        getMapTerritoryValue(firstSelected).y() - 1);
                changeGroundDinoAmount(firstSelected, -amount);
                changeGroundDinoAmount(secondSelected.getName(), amount);
                updateBoard();
            }
            default -> throw new IllegalStateException("Invalid game phase");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalClicks() {
        return terrSelect.getTotalClicks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetTotalClicks() {
        terrSelect.resetTotalClicks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRemainingDinoToPlace() {
        return this.game.getRemainingDinoToPlace();
    }


    /**
     * Adds the specified amount of dino to the territory in the map.
     * 
     * @param territoryName name of the territory
     * @param amount        amount of dino to add at the map
     */
    private void changeGroundDinoAmount(final String territoryName, final int amount) {
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
        final Player redPlayer = new PlayerImpl(GameColor.RED,
                shuffleObjective(copyObjectives),
                shuffleTerritories(copyTerritories, MAX_TERRITORIES));
        final Player greenPlayer = new PlayerImpl(GameColor.GREEN,
                shuffleObjective(copyObjectives),
                shuffleTerritories(copyTerritories, MAX_TERRITORIES));
        final Player bluePlayer = new PlayerImpl(GameColor.BLUE,
                shuffleObjective(copyObjectives),
                shuffleTerritories(copyTerritories, MAX_TERRITORIES));
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
     * @param objectives all of the objectives
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

    private int showDinoAmountSelector(final String source, final String target, final int maximum) {
        final JPanel amountSelectorPanel = new JPanel(new BorderLayout());
        final JLabel text = new JLabel("Inserisci il numero di Dino da spostare da " + source + " a " + target + ":");
        text.setHorizontalAlignment(JLabel.CENTER);
        text.setBorder(new EmptyBorder(0, 0, 0, SELECTOR_HGAP));

        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, maximum, 1));
        amountSelectorPanel.add(text, BorderLayout.WEST);
        amountSelectorPanel.add(spinner, BorderLayout.EAST);

        final int result = JOptionPane.showOptionDialog(this.mainFrame, amountSelectorPanel, "Spostamento",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (result == JOptionPane.CANCEL_OPTION) {
            return 0;
        }
        return (int) spinner.getValue();
    }

    private void showBattleOutcome(final String attacker, final int attackerLosses, final String defender,
            final int defenderLosses,
            final boolean success) {
        String outcomeMessage = attacker + " ha perso " + attackerLosses + " Dino durante l'attacco.";
        if (success) {
            outcomeMessage = outcomeMessage.concat(" Territorio " + defender + " conquistato.");
        } else {
            outcomeMessage = outcomeMessage
                    .concat(" " + defender + " ha perso " + defenderLosses + " Dino in difesa.");
        }
        JOptionPane.showMessageDialog(this.mainFrame, outcomeMessage, "Esito battaglia",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Turns a game color instance into a string with the color translated in
     * Italian for the objective description.
     * 
     * @param color the color to transform
     * @return the translation in Italian of the color
     */
    private String colorToString(final GameColor color) {
        return switch (color) {
            case RED -> "ROSSO";
            case BLUE -> "BLU";
            case GREEN -> "VERDE";
            default -> throw new IllegalArgumentException("Invalid color");
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCard(final Card card) {
        this.deck.add(card);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Card> getCard(final int index) {
        if (index >= 0 && index < this.deck.size()) {
            return Optional.of(this.deck.get(index));
        } else {
            return Optional.empty();
        }
    }
}
