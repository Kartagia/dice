package com.kautiainen.antti.rpgs.dice.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The list of list creates a list storing values of lists.
 */
public class ListOfList<T> extends ArrayList<List<T>> {

    /**
     * Create a new list of list containing the lists of a colleciton.
     * 
     * @param list The colleciton of lists containing the initial lists.
     */
    public ListOfList(Collection<? extends List<T>> list) {
        super(list);
    }
    
    /**
     * Create a new empty list of lists.
     */
    public ListOfList() {
        super();
    }

    /**
     * Create a new list of list with an initial capacity.
     * 
     * @param capacity The initial capapacity.
     */
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
            result.addAll((Collection<List<E>>)head);
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
     * Add all emmbers of the given list to the list of list.
     * @param added The added lists. 
     */
    public void addAll(List<List<? extends T>> added) {
        for( List<? extends T> newMember: added) {
            add(new ArrayList<>(newMember));
        }
    }

    public void addAll(ListOfList<? extends T> added) {
        for (List<? extends T> newMember: added) {
            add(new ArrayList<>(newMember));
        }
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
    public static <E> ListOfList<E> combine(ListOfList<? extends E> head, ListOfList<? extends E> tail) {
        ListOfList<E> result = new ListOfList<>(
                head.isEmpty() ? tail.size() : (tail.isEmpty() ? head.size() : head.size() * tail.size()));
        if (tail.isEmpty()) {
            result.addAll(head);
        } else if (head.isEmpty()) {
            result.addAll(tail);
        } else {
            for (List<? extends E> headSides : head) {
                for (List<? extends E> tailSides : tail) {
                    List<E> newMember = new ArrayList<>(headSides);
                    newMember.addAll(tailSides);
                    result.add(newMember);
                }
            }
        }
        return result;
    }


}