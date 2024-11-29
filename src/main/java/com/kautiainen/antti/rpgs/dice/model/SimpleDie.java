package com.kautiainen.antti.rpgs.dice.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleDie<E> implements Die<E> {
    

    /**
     * The sides of the die.
     */
    private final List<E> sides; 

    public SimpleDie(List<E> sides) {
        this.sides = new ArrayList<>(sides);
    }

    public SimpleDie(Collection<? extends E> sides) {
        this.sides = new ArrayList<>(sides);
    }

    @Override
    public E roll() {
        int side = (int)Math.floor(Math.random()*sides.size());
        return sides.get(side);
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
}
