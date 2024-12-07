package com.kautiainen.antti.rpgs.dice.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

import javax.naming.ConfigurationException;

/**
 * A factory for building roll results.
 */
public interface RollResultFactory<T, V> {

    /**
     * Create a new roll result factory.
     * 
     * @return A new default roll result factory.
     */
    public static <T, V> RollResultFactory<T, V> newInstance() {
        return new SimpleRollResultFactory<>();
    }

    /**
     * Create a new roll result factory with combiner.
     * 
     * @param <T>      The rolled die value type.
     * @param <V>      The result value type.
     * @param combiner The combiner function combining the roll results.
     * @return The roll result factory with given combiner.
     * @throws ConfigurationException The combiner was invalid.
     */
    public static <T, V> RollResultFactory<T, V> newInstance(Function<List<? extends T>, ? extends V> combiner)
            throws ConfigurationException {
        SimpleRollResultFactory<T, V> result = new SimpleRollResultFactory<>();
        result.setCombiner(combiner);
        return result;
    }

    /**
     * Get the function combining the results.
     * 
     * @returns The combiner function combining list of values into result.
     * @throws ConfigurationException The combiner has not been set.
     */
    public Function<List<? extends T>, ? extends V> getCombiner() throws ConfigurationException;

    /**
     * Create a new roll result from given dice and combiner.
     * 
     * @param dice     The rolled dice.
     * @param combiner The combiner combining the roll result to the value.
     * @return The roll result created from the combiner.
     */
    default RollResult<T, V> createResult(Dice<? extends T> dice,
            Function<? super List<? extends T>, ? extends V> combiner) {
        Collector<T, List<T>, V> collector = Collector.of(
                () -> (new ArrayList<T>()),
                (List<T> head, T value) -> {
                    head.add(value);
                },
                (List<T> head, List<T> tail) -> {
                    head.addAll(tail);
                    return head;
                },
                (List<T> result) -> {
                    return combiner.apply(result);
                });
        java.util.Collection<Die<? extends T>> rolledDice = new ArrayList<>(dice.getDice());
        return RollResult.of(rolledDice, collector);
    }

    /**
     * Create a new roll result.
     * 
     * @param dice The dice of the roll.
     * @return Create a new roll result.
     */
    default RollResult<T, V> createResult(Dice<? extends T> dice) throws IllegalStateException {
        try {
            return createResult(dice, getCombiner());
        } catch (ConfigurationException e) {
            throw new IllegalStateException("Invalid roll result builder configuration", e);
        }
    }
}
