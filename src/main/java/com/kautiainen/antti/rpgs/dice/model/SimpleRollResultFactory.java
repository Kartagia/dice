package com.kautiainen.antti.rpgs.dice.model;

import java.util.List;
import java.util.function.Function;

import javax.naming.ConfigurationException;

/**
 * Simple implementation of a roll result factory.
 */
public class SimpleRollResultFactory<T, V> implements RollResultFactory<T, V> {

    /**
     * The combining function.
     */
    Function<List<? extends T>, ? extends V> combiner = null;

    /**
     * Create a simple roll result factory.
     */
    public SimpleRollResultFactory() {

    }

    @Override
    public Function<List<? extends T>, ? extends V> getCombiner() throws ConfigurationException {
        if (combiner == null) {
            throw new ConfigurationException("The combiner has not been set");
        }
        return combiner;
    }

    /**
     * Set combiner of the factory.
     * 
     * @param combiner The new combiner function.
     * @throws ConfigurationException The combiner was invalid.
     */
    public void setCombiner(Function<List<? extends T>, ? extends V> combiner) throws ConfigurationException {
        if (combiner == null)
            throw new ConfigurationException("Undefined combiner");
        this.combiner = combiner;
    }
}