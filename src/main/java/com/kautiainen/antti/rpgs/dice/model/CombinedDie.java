package com.kautiainen.antti.rpgs.dice.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;

/**
 * A combined die is a set of dice acting as a die.
 */
public class CombinedDie<T, V> implements Dice<T>, Die<V> {

    public static class ListOfList<T> extends ArrayList<List<T>> {

        public ListOfList(Collection<? extends List<T>> list) {
            super(list);
        }

        public ListOfList() {
            super();
        }

        public ListOfList(int capacity) {
            super(capacity);
        }

        /**
         * Cobmine list of list with values.
         * 
         * @param <E>    The type of the list values.
         * @param head   The list combined with values.
         * @param values The values.
         * @return The list of list containing all combinations of head lists and
         *         values.
         */
        public static <E> ListOfList<E> combine(ListOfList<E> head, Collection<? extends E> values) {
            ListOfList<E> result = new ListOfList<>(
                    head.isEmpty() ? values.size() : (values.isEmpty() ? head.size() : head.size() * values.size()));
            if (values.isEmpty()) {
                result.addAll(head);
            } else if (head.isEmpty()) {
                List<E> newMember = new ArrayList<>(values);
                result.add(newMember);
            } else {
                for (List<E> headSides : head) {
                    for (E value : values) {
                        List<E> newMember = new ArrayList<>(headSides);
                        newMember.add(value);
                        result.add(newMember);
                    }
                }
            }
            return result;
        }

        /**
         * Cobmine list of list with values.
         * 
         * @param <E>    The type of the list values.
         * @param head   The list combined with values.
         * @param values The values.
         * @return The list of list containing all combinations of head lists and
         *         values.
         */
        public static <E> ListOfList<E> combine(ListOfList<E> head, ListOfList<E> tail) {
            ListOfList<E> result = new ListOfList<>(
                    head.isEmpty() ? tail.size() : (tail.isEmpty() ? head.size() : head.size() * tail.size()));
            if (tail.isEmpty()) {
                result.addAll(head);
            } else if (head.isEmpty()) {
                result.addAll(tail);
            } else {
                for (List<E> headSides : head) {
                    for (List<E> tailSides : tail) {
                        List<E> newMember = new ArrayList<>(headSides);
                        newMember.addAll(tailSides);
                        result.add(newMember);
                    }
                }
            }
            return result;
        }


    }

    private Collector<? super T, ?, ? extends V> combiner;
    private List<Die<? extends T>> dice;

    /**
     * Create combined die from roll result.
     * 
     * @param
     */
    public CombinedDie(RollResult<T, V> result) {
        this.combiner = result.getCombiner();
        this.dice = new ArrayList<>(result.getDice().getDice());
    }

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

    public <A> CombinedDie(
            List<Die<T>> dice,
            Collector<? super T, A, ? extends V> combiner) {
        this.dice = new ArrayList<>(dice.size());
        for (Die<T> die : dice) {
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

    @Override
    public List<? extends V> getSides() {
        // Generating the combinations table for the dice.
        Collector<List<? extends T>, ListOfList<T>, List<? extends V>> sideCombiner = Collector.of(
                () -> (new ListOfList<T>()),
                (ListOfList<T> head, List<? extends T> sides) -> {
                    ListOfList<T> newSidesResult = new ListOfList<>(head.size() * sides.size());
                    for (List<T> headSides : head) {
                        for (T side : sides) {
                            ArrayList<T> newSides = new ArrayList<>(headSides);
                            newSides.add(side);
                            newSidesResult.add(newSides);
                        }
                    }
                    head.clear();
                    head.addAll(newSidesResult);
                },
                (ListOfList<T> head, ListOfList<T> tail) -> {
                    return ListOfList.combine(head, tail);
                },
                (ListOfList<T> head) -> {
                    List<V> result = new ArrayList<>(
                            head.stream().map((List<T> sides) -> ((V) sides.stream().collect(getCombiner()))).toList()
                    );
                    return result;
                });
        return getDice().stream().map(die -> {
            List<? extends T> sides = die.getSides();
            return sides;
        }).collect(sideCombiner);
    }

}
