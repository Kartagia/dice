package com.kautiainen.antti.rpgs.dice.model;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;

/**
 * A result of a roll of one or more dice.
 */
public interface RollResult<T, V> extends DieResult<V> {

    /**
     * The rolled dice.
     * 
     * @return The rolled dice.
     */
    public Dice<T> getDice();

    /**
     * Get the members of the roll result.
     * 
     * @return The list of die results.
     */
    public List<DieResult<T>> getMembers();

    /**
     * The combiner function combining the results.
     * 
     * @return The collector combining the members to result.
     */
    public Collector<? super T, ?, ? extends V> getCombiner();

    @Override
    default V getValue() {
        return getMembers().stream().map(DieResult::getValue).collect(getCombiner());
    }

    /**
     * Create a combined die.
     * 
     * @param <SIDES>   The type of the die results.
     * @param <RESULT>  The result of the die.
     * @param <A>       The accumulator of the combiner.
     * @param dice      The dice rolled for the result.
     * @param combiner  The combiner function combining the dice results into roll
     *                  result.
     * @param rerollabe Is the created result rerollable or not.
     */
    public static <A, SIDES, RESULT> RollResult<SIDES, RESULT> of(
            Collection<Die<? extends SIDES>> dice,
            Collector<? super SIDES, A, ? extends RESULT> combiner, boolean rerollable) {
        return new RollResult<SIDES, RESULT>() {

            private final CombinedDie<SIDES, RESULT> die = new CombinedDie<>(dice, combiner);

            private final List<DieResult<SIDES>> results = (rerollable ? die.getRerollableResults() : die.getResults() );

            @Override
            public Die<? extends RESULT> getDie() {
                return this.die;
            }

            @Override
            public Dice<SIDES> getDice() {
                return die;
            }

            @Override
            public List<DieResult<SIDES>> getMembers() {
                return results;
            }

            @Override
            public Collector<? super SIDES, ?, ? extends RESULT> getCombiner() {
                return die.getCombiner();
            }

            @Override
            public RESULT reroll() {
                if (rerollable) {
                    // Perform reroll.
                    return getMembers().stream().map( DieResult::reroll ).collect(getCombiner());
                } else {
                    // Throw exception by calling the super constructor.
                    return RollResult.super.reroll();
                }
            }

        };
    }

    /**
     * Create a combined die.
     * 
     * @param <SIDES>  The type of the die results.
     * @param <RESULT> The result of the die.
     * @param <A>      The accumulator of the combiner.
     * @param dice      The dice rolled for the result.
     * @param combiner  The combiner function combining the dice results into roll
     *                  result.
     */
    public static <A, SIDES, RESULT> RollResult<SIDES, RESULT> of(
            Collection<Die<? extends SIDES>> dice,
            Collector<? super SIDES, A, ? extends RESULT> combiner) {
        return of(dice, combiner, false);
    }
}
