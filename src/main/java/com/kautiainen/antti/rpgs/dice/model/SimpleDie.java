package com.kautiainen.antti.rpgs.dice.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SimpleDie<E> implements Die<E> {
    

    /**
     * The sides of the die.
     */
    private final List<E> sides; 
 
    /**
     * Create a die from list of sides.
     * @param sides The list of sides.
     * @throws IllegalArgumentException The sides list was invalid.
     */
    public SimpleDie(List<E> sides) throws IllegalArgumentException {
        this.sides = new ArrayList<>(sides);
    }

    /**
     * Create a new from a colleciton of sides.
     * @param sides The collection of sides.
     * @throws IllegalArgumentException The sides list was invalid.
     */
    public SimpleDie(Collection<? extends E> sides) throws IllegalArgumentException {
        this.sides = new ArrayList<>(sides);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("d[");
        for (E side: this.sides) {
            if (result.length() > 2) {
                result.append(",");
            }
            result.append(side);
        }
        result.append("]");
        return result.toString();
    }

    @Override
    public List<? extends E> getSides() {
        return Collections.unmodifiableList(this.sides);
    }
}
