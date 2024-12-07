package com.kautiainen.antti.rpgs.dice.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A single die.
 */
public interface Die<VALUE> {

    /**
     * Roll the die to get single value.
     */
    default VALUE roll() {
        List<? extends VALUE> sides = getSides();
        return sides.get((int) Math.floor(Math.random() * (sides.size())));
    }

    /**
     * Get an immutable roll result.
     * 
     * @return An immutable die result with rolled value.
     */
    default DieResult<VALUE> getResult() {
        return DieResult.create(this);
    }

    /**
     * Get a rerollabe roll result.
     * 
     * @return A rerollable die result with rolled value.
     */
    default DieResult<VALUE> getRerollableResult() {
        return DieResult.createRerollable(this);
    }

    /**
     * The sides of the die.
     * 
     * @return An unmodifiable list of die side.s
     */
    public List<? extends VALUE> getSides();

    /**
     * Create a die with given sides.
     * 
     * @param <TYPE> The type of the side value.
     * @param sides  The sides of the die.
     * @return The die with given sides.
     * @throws IllegalArgumentException The side count was zero or the list was undefined.
     */
    public static <TYPE> Die<TYPE> of(List<TYPE> sides) throws IllegalArgumentException {
        if (sides == null) throw new IllegalArgumentException("Invalid sides of die", new NullPointerException("Undefined sides"));
        else if (sides.isEmpty())  throw new IllegalArgumentException("Invalid sides of die", new NullPointerException("No sides given"));
        return () -> Collections.unmodifiableList(sides);
    }

    /**
     * Generate a die with given number of sides.
     * 
     * @param sideCount The side count.
     * @return If the side count is negative, the die with sides from -1 to side
     *         count is returned.
     *         Otherwise the die with sides from 1 to side count is returned.
     * @throws IllegalArgumentException The side count was zero.
     */
    public static Die<Integer> of(int sideCount) throws IllegalArgumentException {
        if (sideCount == 0) {
            throw new IllegalArgumentException("Invalid die with zero sides");
        }
        List<Integer> sides = new ArrayList<>(Math.abs(sideCount));
        if (sideCount < 0) {
            for (int i = -1; i >= sideCount; i--) {
                sides.add(i);
            }
        } else {
            for (int i = 1; i <= sideCount; i++) {
                sides.add(i);
            }
        }
        return Die.of(sides);
    }

    /**
     * Generate the default basic dies.
     * 
     * @param dieName The name of the die.
     * @throws IllegalArgumentException The die name was not a valid die name.
     */
    public static Die<Integer> of(String dieName) throws IllegalArgumentException {
        switch (dieName) {
            case "F", "f" -> {
                return Die.of(Arrays.asList(-1, 0, 1));
            }
            case "C" -> {
                return Die.of(Arrays.asList(0, 1));
            }
            case "suite" -> {
                return Die.of(Arrays.asList(0, 1, 2, 4));
            }
            default -> {
                if (Pattern.matches("^\\d+$", dieName)) {
                    int sideCount = Integer.parseInt(dieName);
                    List<Integer> sides = new ArrayList<>(sideCount);
                    for (int i = 1; i <= sideCount; i++) {
                        sides.add(i);
                    }
                    return Die.of(sides);
                } else {
                    // An unknown die.
                    throw new IllegalArgumentException("Unknown die");
                }
            }
        }
    }
}
