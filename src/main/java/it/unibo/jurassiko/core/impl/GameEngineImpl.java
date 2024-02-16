package it.unibo.jurassiko.core.impl;

import java.util.Optional;

import it.unibo.jurassiko.controller.game.api.MainController;

import it.unibo.jurassiko.core.api.GameEngine;
import it.unibo.jurassiko.core.api.GamePhase;
import it.unibo.jurassiko.core.api.PlayerTurn;
import it.unibo.jurassiko.core.api.WinCondition;
import it.unibo.jurassiko.core.api.GamePhase.Phase;
import it.unibo.jurassiko.model.player.api.Player;
import it.unibo.jurassiko.model.player.api.Player.GameColor;

/**
 * Implementation of the interface {@GameEngine}.
 */
public class GameEngineImpl implements GameEngine {

    private static final int MAX_PLAYERS = 3;
    private static final int FIRST_TURN_BONUS = 13;

    private final GamePhase gamePhase;
    private final PlayerTurn playerTurn;
    private final MainController controller;
    private final WinCondition winCondition;

    private boolean firstTurn;
    private Optional<Player> winner;

    /**
     * Contructor for the GameEngine.
     * 
     * @param controller the MainController used to interact with the view
     */
    public GameEngineImpl(final MainController controller) {
        this.gamePhase = new GamePhaseImpl();
        this.controller = controller;
        this.winCondition = new WinConditionImpl();
        try {
            this.playerTurn = new PlayerTurnImpl(this.controller.getPlayers());
        } catch (final CloneNotSupportedException e) {
            throw new IllegalStateException("Failed to create a new istance of the player", e);
        }
        this.firstTurn = true;
        this.winner = Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startGameLoop() {
        placementPhase();
        attackPhase();
        movimentPhase();
        controller.updateBoard();
    }

    /**
     * Manage the Placement Phase of the game.
     */
    private void placementPhase() {
        final var bonusGroundDino = playerTurn.getCurrentPlayerTurn().getBonusGroundDino();
        final var bonusWaterDino = playerTurn.getCurrentPlayerTurn().getBonusWaterDino();
        if (firstTurn) {
            firstTurnPlacement();
            return;
        }
        if (gamePhase.getPhase().equals(GamePhase.Phase.PLACEMENT)) {
            controller.updateTerritorySelectorButtons();
            controller.openTerritorySelector();
            if (controller.getTotalClick() == bonusGroundDino + bonusWaterDino) {
                gamePhase.goNext();
                controller.resetTotalClick();
                controller.closeTerritorySelector();
            }
        }
    }

    /**
     * Init the first Placing Phase of the game.
     */
    private void firstTurnPlacement() {
        // controller.openObjectiveCard(); // TODO: it must open only once per player
        controller.updateTerritorySelectorButtons();
        controller.openTerritorySelector();
        if (controller.getTotalClick() == FIRST_TURN_BONUS) {
            playerTurn.goNext();
            controller.resetTotalClick();
            controller.updateTerritorySelectorButtons();
            if (checkInitDino()) {
                controller.closeTerritorySelector();
                firstTurn = false;
            }
        }
    }

    /**
     * Check if every player has placed the initial dino.
     * 
     * @return true if every player has placed the initial dino, false otherwise
     */
    private boolean checkInitDino() {
        final var temp = controller.getTerritoriesMap().values();
        final var initDinoAmount = FIRST_TURN_BONUS
                + (int) temp.stream().filter(t -> t.x().equals(GameColor.RED)).count();
        return temp.stream()
                .mapToInt(value -> value.y())
                .sum() == initDinoAmount * MAX_PLAYERS;
    }

    private void attackPhase() {
        if (gamePhase.getPhase().equals(Phase.ATTACK_FIRST_PART)
                || gamePhase.getPhase().equals(Phase.ATTACK_SECOND_PART)) {
            controller.updateBoard();
            controller.updateTerritorySelectorButtons();
        }
    }

    private void movimentPhase() {
        if (gamePhase.getPhase().equals(Phase.MOVEMENT_FIRST_PART)) {
            controller.updateTerritorySelectorButtons();
            controller.openTerritorySelector();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endTurn() {
        playerTurn.goNext();
        gamePhase.setPhase(Phase.PLACEMENT);
        controller.updateBoard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOver() {
        final var currentPlayer = this.controller.getCurrentPlayer();

        return this.winCondition
                .getWinner(this.controller.getTerritoriesMap(), currentPlayer, currentPlayer.getObjective())
                .isPresent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getWinner() {
        return winner.orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase getGamePhase() {
        return gamePhase.getPhase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGamePhase(final Phase phase) {
        gamePhase.setPhase(phase);
        controller.updateBoard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlayerTurn getPlayerTurn() {
        return new PlayerTurnImpl(playerTurn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFirstTurn() {
        return firstTurn;
    }

}
