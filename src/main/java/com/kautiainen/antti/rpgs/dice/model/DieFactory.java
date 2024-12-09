package com.kautiainen.antti.rpgs.dice.model;

import java.util.List;

/**
 * The die factory interface is a factory creating die models.
 */
public interface DieFactory<T> {

    /**
     * The error message indicating the source result was undefined.
     */
    public static final String UNDEFINED_SOURCE_RESULT = "Undefined source result";
    /**
     * The error message indicating the source result was invalid.
     */
    public static final String INVALID_SOURCE_RESULT = "Invalid source result";

    /**
     * Create a new die.
     * 
     * @param sides The sides of the die.
     * @return The die with given sides.
     * @throws IllegalArgumentException The sides array was invalid.
     */
    default Die<T> createDie(List<? extends T> sides) throws IllegalArgumentException {
        return Die.of(new java.util.ArrayList<>(sides));
    }

    default Die<T> createDie(DieResult<? extends T> result) throws IllegalArgumentException {
        if (result == null) {
            throw new IllegalArgumentException(INVALID_SOURCE_RESULT,
                    new NullPointerException(UNDEFINED_SOURCE_RESULT));
        }
        try {
            return createDie(result.getDie().getSides());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(INVALID_SOURCE_RESULT, e);
        }
    }
}
