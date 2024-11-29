package com.kautiainen.antti.rpgs.dice.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;


/**
 * A combined die is a set of dice acting as a die.
 */
public class CombinedDie<T, V> implements Dice<T>, Die<V> {

    private Collector<? super T, ?, ? extends V> combiner;
    private List<Die<? extends T>> dice;

    /**
     * Create a new combined die.
     * 
     * @param dice The members of the combined dice. 
     */
    public CombinedDie(
        Collection<Die<? extends T>> dice,
            Collector<? super T, ?, ? extends V> combiner) {

        this.dice = new ArrayList<>(dice);
        this.combiner = combiner;
    }

    public<A> CombinedDie(
        List<Die<T>> dice, 
        Collector<? super T, A, ? extends V> combiner) {
        this.dice = new ArrayList<>(dice.size());
        for (Die<T> die: dice) {
            this.dice.add(die);
        }
        this.combiner = combiner;
    }

    public CombinedDie(
        Dice<? extends T> dice,
        Collector<? super T, ?, ? extends V> combiner) {
        this(new ArrayList<>(dice.getDice()), combiner);
    }

    @Override
    public List<Die<? extends T>> getDice() {
        return dice;
    }

    /**
     * Get the combiner of the results.
     * 
     * @return The collector combining the roll results to the result.
     */
    public Collector<? super T, ?, ? extends V> getCombiner() {
        return combiner;
    }

    @Override
    public V roll() {
        List<DieResult<T>> results = getDice().stream().map(Die::getResult).map(die -> {
            DieResult<T> result = DieResult.of(die);
            return result;
        }).toList();
        return results.stream().map(DieResult::getValue).collect(getCombiner());
    }

    @Override
    public List<DieResult<T>> getResults() {
        return getDice().stream().map(Die::getResult).map(die -> {
            DieResult<T> result = DieResult.of(die);
            return result;
        }).toList();
    }

    @Override
    public List<DieResult<T>> getRerollableResults() throws UnsupportedOperationException {
        return getDice().stream().map(Die::getRerollableResult).map(die -> {
            DieResult<T> result = DieResult.of(die);
            return result;
        }).toList();
    }
}
