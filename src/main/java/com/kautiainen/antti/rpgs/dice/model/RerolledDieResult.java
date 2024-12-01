package com.kautiainen.antti.rpgs.dice.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * Rerolled die result represents a die results created by rerolling.
 * The rerolled die result contains all roll values, and function determining
 * the result of the reroll.
 */
public class RerolledDieResult<T> implements DieResult<T> {

    /**
     * Create a function getting the last element of the list or an undefined value
     * for an empty list.
     * 
     * @param <T> The content type.
     * @return If the list is empty, an undefined value. Otherwise the last element
     *         of the list.
     */
    public static <T> Function<List<? extends T>, ? extends T> getLastOf() {
        return (List<? extends T> list) -> {
            if (list.isEmpty()) {
                return (T) null;
            } else {
                return (T) list.get(list.size() - 1);
            }
        };
    }

    /**
     * Create a function getting the best of all values.
     * 
     * @param <T>        The content type.
     * @param comparator The comparator of the values.
     * @return The function returning the best of the values.
     */
    public static <T> Function<List<? extends T>, ? extends T> getBestOf(Comparator<? super T> comparator) {
        return (List<? extends T> list) -> (list.stream().collect(
                Collector.of(
                        () -> (new AtomicReference<>()),
                        (AtomicReference<T> head, T item) -> {
                            if (head.get() == null || comparator.compare(head.get(), item) < 0) {
                                head.set(item);
                            }
                        },
                        (AtomicReference<T> head, AtomicReference<T> tail) -> {
                            T headKey = head.get();
                            T tailKey = tail.get();
                            if ((headKey == null && tailKey == null) ||
                                    tailKey == null
                                    || (headKey != null && (comparator.compare(headKey, tailKey) < 0))) {
                                return head;
                            } else {
                                return tail;
                            }
                        },
                        AtomicReference::get)));
    }

    /**
     * Create a function getting the worst of all values.
     * 
     * @param <T>        The content type.
     * @param comparator The comparator of the values.
     * @return The function returning the worst of the values.
     */
    public static <T> Function<List<? extends T>, ? extends T> getWorstOf(Comparator<? super T> comparator) {
        return (List<? extends T> list) -> (list.stream().collect(
                Collector.of(
                        () -> (new AtomicReference<>()),
                        (AtomicReference<T> head, T item) -> {
                            if (head.get() == null || comparator.compare(head.get(), item) < 0) {
                                head.set(item);
                            }
                        },
                        (AtomicReference<T> head, AtomicReference<T> tail) -> {
                            T headKey = head.get();
                            T tailKey = tail.get();
                            if ((headKey == null && tailKey == null) ||
                                    headKey == null
                                    || (tailKey != null && (comparator.compare(headKey, tailKey) > 0))) {
                                return head;
                            } else {
                                return tail;
                            }
                        },
                        AtomicReference::get)));
    }

    /**
     * Create function returning best value of the natural order.
     * 
     * @param <T> The content type omaprable with itself or its superclass.
     * @return The funciton returning the largets number on the list.
     */
    public static <T extends Comparable<? super T>> Function<List<? extends T>, ? extends T> getBestOf() {
        return getBestOf(Comparator.naturalOrder());
    }

    /**
     * Create function returning worst value of the natural order.
     * 
     * @param <T> The content type with natural order.
     * @return The funciton returning the smallest number on the list.
     */
    public static <T extends Comparable<? super T>> Function<List<? extends T>, ? extends T> getWorstOf() {
        return getWorstOf(Comparator.naturalOrder());
    }

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