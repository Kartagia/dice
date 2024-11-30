package com.kautiainen.antti.rpgs.dice.model;

/**
 * A die result.
 */
public interface DieResult<T> {

    /**
     * Create an immutable die result.
     * 
     * @param <VALUE> The value type of the die result.
     * @param die     The rolled die.
     * @return The die result generated from the die.
     * @throws NullPointerException The die is undefined.
     */
    static <VALUE> DieResult<VALUE> create(Die<? extends VALUE> die) {

        return new DieResult<>() {

            /**
             * The value of the die result.
             */
            private final VALUE value = die.roll();

            @Override
            public VALUE getValue() {
                return value;
            }

            @Override
            public Die<? extends VALUE> getDie() {
                return die;
            }
        };

    }

    /**
     * Create a new die result with given value.
     * 
     * @param die The die rolled to get the result.
     */
    static <VALUE> DieResult<VALUE> createRerollable(Die<? extends VALUE> die) {

        return new DieResult<>() {

            /**
             * The die of the roll.
             */
            private final Die<? extends VALUE> myDie = die;

            /**
             * The value of the die result.
             */
            private VALUE value = die.roll();

            @Override
            public synchronized VALUE getValue() {
                return value;
            }

            @Override
            public synchronized VALUE reroll() throws UnsupportedOperationException {
                this.value = this.myDie.roll();
                return getValue();
            }

            @Override
            public Die<? extends VALUE> getDie() {
                return this.myDie;
            }
        };
    }

    /**
     * The value of the die result.
     * 
     * @return The current value of the die result.
     */
    public T getValue();

    /**
     * Rerolls the die result.
     * 
     * @return The new value of the result.
     * @throws UnsupportedOperationException The rerolling is not supported.
     */
    default T reroll() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Rerolling not supported");
    }

    /**
     * Get the die of the roll.
     * 
     * @return The die rolled to get the result.
     */
    public Die<? extends T> getDie();


    /**
     * Create a wrapper of roll result of super class value.
     * @param <V> The resulting die result type.
     * @param <T> The original die result type.
     */
    static<V, T extends V> DieResult<V> of(DieResult<? extends T> source) {
        return new DieResult<V>() {

            @Override
            public V getValue() {
                return source.getValue();
            }

            @Override
            public Die<? extends V> getDie() {
                return source.getDie();
            }
            
            @Override
            public V reroll() throws UnsupportedOperationException {
                return source.reroll();
            }
        };
    }
}
