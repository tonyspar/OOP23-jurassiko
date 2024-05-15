package it.unibo.jurassiko.controller.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import it.unibo.jurassiko.common.Pair;
import it.unibo.jurassiko.core.api.GamePhase;
import it.unibo.jurassiko.model.card.api.Card;
import it.unibo.jurassiko.model.player.api.Player;
import it.unibo.jurassiko.model.player.api.Player.GameColor;
import it.unibo.jurassiko.model.territory.api.Ocean;
import it.unibo.jurassiko.model.territory.api.Territory;

/**
 * Interface for the MainController, the core controller.
 */
public interface MainController {

    /**
     * Gets the map of territories.
     * 
     * @return map of the territories with the color and the amount of dino
     */
    Map<Territory, Pair<GameColor, Integer>> getTerritoriesMap();

    /**
     * Gets the pair containing the ocean and the current owner.
     * 
     * @return a pair with the selected ocean and its owner.
     */
    Optional<Pair<Ocean, GameColor>> getCurrentOcean();

    /**
     * Creates and returns a list of all the players.
     * 
     * @return list of the players
     * @throws CloneNotSupportedException if fails to clone the players
     */
    List<Player> getPlayers() throws CloneNotSupportedException;

    /**
     * Returns current player.
     * 
     * @return current player
     */
    Player getCurrentPlayer();

    /**
     * Retrieves the card at the specified index in the deck, if present.
     * 
     * @param index the index of the card to retrieve
     * @return an Optional containing the card at the specified index, or an empty
     *         Optional if the index is out of range
     */
    Optional<Card> getCard(int index);

    /**
     * Adds a card to the deck.
     * 
     * @param card the card to add
     */
    void addCard(Card card);

    /**
     * Updates and shows the buttons in the frame.
     */
    void openTerritorySelector();

    /**
     * Closes the frame of territories.
     */
    void closeTerritorySelector();

    /**
     * Opens the OptionPane containing the objective.
     */
    void openObjectiveCard();

    /**
     * Updates the panel of dinos.
     */
    void updateBoard();

    /**
     * Shows and updates the main frame.
     */
    void openView();

    /**
     * Close the view.
     */
    void closeGame();

    /**
     * Show a pop up window with the winner color.
     * 
     * @param winner color of the winner
     */
    void showWinnerName(GameColor winner);

    /**
     * Tells the game engine to start the loop.
     */
    void startGameLoop();

    /**
     * Gets the actual game phase from the GameEngine.
     * 
     * @return actual game phase
     */
    GamePhase.Phase getGamePhase();

    /**
     * Set the phase of the Game.
     * 
     * @param phase phase to set
     */
    void setGamePhase(GamePhase.Phase phase);

    /**
     * @return true if its the first turn, false otherwise
     */
    boolean isFirstTurn();

    /**
     * Pass the Turn to the next Player.
     */
    void endTurn();

    /**
     * Checks if the player has the territory in the map.
     * 
     * @param territoryName name of the territory
     * @return true if current player has the territory passed as input
     */
    boolean isAllyTerritory(String territoryName);

    /**
     * Same as isAllyTerritory but it must have more than one Dino.
     * 
     * @param territoryName name of the territory
     * @return true if current player has the territory with more than one dino
     */
    boolean isAllyTerritoryWithMoreThanOne(String territoryName);

    /**
     * Checks if the input territory has al least one adjEnemy.
     * 
     * @param territoryName name of the territory
     * @return true if has one adj Enemy, false otherwise
     */
    boolean hasAdjEnemy(String territoryName);

    /**
     * Checks if the input territory has al least one adjAlly.
     * 
     * @param territoryName name of the territory
     * @return true if has one adj Ally, false otherwise
     */
    boolean hasAdjAlly(String territoryName);

    /**
     * Get a Set of Adj territory name of the input.
     * 
     * @param territoryName name of the territory
     * @return a Set of String, name of the territory,
     *         that is Adj to the territoryName
     */
    Set<String> getAdj(String territoryName);

    /**
     * Based on the phase and the color of the current player.
     * It manages the various phases.
     * 
     * @param territory name of the territory
     */
    void manageSelection(String territory);

    /**
     * Returns the amount of clicks.
     * 
     * @return amount of clicks
     */
    int getTotalClicks();

    /**
     * Resets the amount of total clicks.
     */
    void resetTotalClicks();

    /**
     * Calculate dino to place and return it.
     * 
     * @return the amount of dino to place
     */
    int getRemainingDinoToPlace();

    /**
     * Assigns the cards from this deck to a destination deck.
     *
     * @return an Optional containing a card from this deck if available, or an
     *         empty Optional if the deck is empty
     */
    Optional<Card> assignment();

}
