package it.unibo.jurassiko.model.battle.impl;

import java.util.List;

import it.unibo.jurassiko.common.Pair;
import it.unibo.jurassiko.model.battle.api.Battle;
import it.unibo.jurassiko.model.dice.api.Dice;
import it.unibo.jurassiko.model.dice.impl.DiceImpl;

/**
 * Implementation of Battle interface.
 */
public class BattleImpl implements Battle {

    private final Dice dice = new DiceImpl();

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Integer, Integer> attack(final int nTroopsAttack, final int nTroopsDefence, final int nDiceAttack,
            final int nDiceDefence) {
        final List<Integer> listRollAttack = dice.rollMultiple(nDiceAttack);
        final List<Integer> listRollDefence = dice.rollMultiple(nDiceDefence);
        final int nDiceLower = Math.min(nDiceAttack, nDiceDefence);

        int nTroopsAttackDeath = 0;
        int nTroopsDefenceDeath = 0;

        for (int i = 0; i < nDiceLower; i++) {
            if (listRollDefence.get(i) >= listRollAttack.get(i)) {
                nTroopsAttackDeath++;
            } else {
                nTroopsDefenceDeath++;
            }
        }
        return new Pair<Integer, Integer>(nTroopsAttackDeath, nTroopsDefenceDeath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int calculateDino(final int dinoAmount, final boolean offensive) {
        if (dinoAmount > 3) {
            return 3;
        } else if (offensive) {
            return dinoAmount - 1;
        }
        return dinoAmount;
    }
}
