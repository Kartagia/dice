package com.kautiainen.antti.rpgs.dice.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A dice represents one or more dice.
 * 
 * @param <T> The value type of the rolled dice.
 */
public interface Dice<T> {

    /**
     * Get the list of dice in the dice pool.
     */
    public List<Die<? extends T>> getDice();

    /**
     * Create a die of
     */
    public static <VALUE> Dice<VALUE> of(List<Die<? extends VALUE>> dice) {
        return new Dice<>() {

            private final List<Die<? extends VALUE>> myDice = new ArrayList<>(dice);

            @Override
            public List<Die<? extends VALUE>> getDice() {
                return this.myDice;
            }
        };
    }

    /**
     * Get roll results.
     * 
     * @return The list of results of the dice of the roll.
     */
    default List<DieResult<T>> getResults() {
        return getDice().stream().map((Die<? extends T> die) -> {
            DieResult<T> result = DieResult.of(die.getResult());
            return result;
        }).toList();
    }

    /**
     * Get rerollable roll results.
     * 
     * @return The list of rerollable resutls of the dice of the roll.
     * @throws UnsupportedOperationException The rerolling is not supported.
     */
    default List<DieResult<T>> getRerollableResults() throws UnsupportedOperationException {
        return getDice().stream().map( (Die<? extends T> die) -> {
            DieResult<T> result;
            try {
                // Trying to create rerollable result.
                result = DieResult.createRerollable(die);
            } catch (UnsupportedOperationException e) {
                // The die without reroll support is returned as it is.
                result = DieResult.of(die.getResult());
            }
            return result;
        }).toList();
    }
}
