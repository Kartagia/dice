package com.kautiainen.antti.rpgs.dice.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Rerolled die result represents a die results created by rerolling.
 * The rerolled die result contains all roll values, and function determining
 * the result of the reroll.
 */
public class RerolledDieResult<T> implements DieResult<T> {

    /**
     * The list of the rerolled values.
     */
    private final List<T> results = new ArrayList<>(1);

    /**
     * The rerolled die.
     */
    private final Die<? extends T> die;

    /**
     * The combiner of values.
     */
    private Function<? super List<? extends T>, ? extends T> combiner;

    /**
     * The value of the result.
     */
    private T value;

    /**
     * Create a new list.
     * 
     * @param value The value of a list.
     * @return The list containing the value.
     */
    public static <T> List<? extends T> createList(T value) {
        List<T> result = new ArrayList<>();
        result.add(value);
        return result;
    }

    /**
     * Create a new rerolled die result.
     * 
     * @param die      The rerolled die.
     * @param values   The values of the rerolls.
     * @param compiler The functtion detemrining the value of the compiler.
     */
    public RerolledDieResult(Die<? extends T> die, List<? extends T> values,
            Function<? super List<? extends T>, ? extends T> compiler) {
        this.die = die;
        this.combiner = compiler;
        this.results.addAll(values);
        this.value = compiler.apply(values);
    }

    /**
     * Create a new rerolled die result returning the most recent roll.
     * The value is determined by rolling a die.
     * 
     * @param die The rolled die.
     */
    public RerolledDieResult(Die<? extends T> die) {
        this(die, createList((T) die.roll()),
                (List<? extends T> list) -> (list != null && list.isEmpty() ? (T) list.get(list.size() - 1)
                        : (T) null));
    }

    @Override
    public Die<? extends T> getDie() {
        return this.die;
    }

    @Override
    public T getValue() {
        return value;
    }

    /**
     * Set the values of the rerolled result.
     * 
     * @param valeus The new set of reroleld values.
     * @throws IllegalArgumentException      The given value was invalid.
     * @throws UnsupportedOperationException The result is immutable.
     */
    public void setValues(List<? extends T> values) throws IllegalArgumentException, UnsupportedOperationException {
        this.value = getResult(values);
        this.results.clear();
        this.results.addAll(values);
    }

    /**
     * Get the result of the roll.
     * 
     * @param values The new set of values.
     */
    protected T getResult(List<? extends T> values) {
        return combiner.apply(values);
    }

    @Override
    public T reroll() {
        T newValue = DieResult.super.reroll();
        this.results.add(newValue);
        this.value = getResult(results);
        return getValue();
    }
}