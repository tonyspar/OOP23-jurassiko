package it.unibo.jurassiko.core.impl;

import java.util.List;
import java.util.stream.Collectors;

import it.unibo.jurassiko.core.api.PlayerTurn;
import it.unibo.jurassiko.model.player.api.Player;
import it.unibo.jurassiko.model.player.api.Player.GameColor;

/**
 * Implementation ot {@link PlayerTurn} interface.
 */
public class PlayerTurnImpl implements PlayerTurn {

    private final List<Player> players;
    private int index;

    /**
     * Constructor for the Player Turn.
     * 
     * @param players List of the Players
     */
    public PlayerTurnImpl(final List<Player> players) {
        this.players = players.stream().sorted((o1, o2) -> o2.getColor().getColor().compareTo(o1.getColor().getColor()))
                .collect(Collectors.toList());
        index = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameColor getCurrentPlayerTurn() {
        return players.get(index).getColor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Player> getPlayers() {
        return List.copyOf(players);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void goNext() {
        index++;
        if (index >= players.size()) {
            index = 0;
        }
    }

}
