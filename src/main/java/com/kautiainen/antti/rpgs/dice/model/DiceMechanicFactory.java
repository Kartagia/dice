package com.kautiainen.antti.rpgs.dice.model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collector;

import javax.naming.ConfigurationException;

/**
 * A factory producing dice mechanics.
 * 
 * @param <T> the type of the die values.
 */
public interface DiceMechanicFactory<T> {

    /**
     * The error message indicating the die roll is not found.
     */
    public static final String DIE_ROLL_NOT_FOUND_MESSAGE = "No die roll exists with given name and result type";

    /**
     * A factory producing die rolls.
     * 
     * @param <T> the type of the die values.
     */
    public static interface DieRollFactory<T> {

        /**
         * Create new instance of the die roll factory.
         */
        @SuppressWarnings("Convert2Lambda")
        static <T> DieRollFactory<T> newInstance() {
            return new DieRollFactory<T>() {
                @Override
                public <V> DiceMechanics.DieRoll<T, V> createDieRoll(String name, Dice<? extends T> dice,
                        Function<? super List<? extends T>, ? extends V> valueConverter) throws NoSuchElementException {
                    return new DiceMechanics.DieRoll<T, V>() {

                        @Override
                        public String getName() {
                            return name;
                        }

                        @Override
                        public RollResult<T, V> roll() {
                            return new RollResult<T, V>() {

                                private final Collector<T, ArrayList<T>, V> combiner = Collector.of(
                                        () -> (new java.util.ArrayList<T>()),
                                        (ArrayList<T> head, T result) -> {
                                            head.add(result);
                                        },
                                        (ArrayList<T> head, ArrayList<T> tail) -> {
                                            head.addAll(tail);
                                            return head;
                                        },
                                        (ArrayList<T> head) -> {
                                            return (V) valueConverter.apply(head);
                                        });

                                private final CombinedDie<T, V> myDie = new CombinedDie<>(dice, this.combiner);

                                @Override
                                public Die<? extends V> getDie() {
                                    return myDie;
                                }

                                @Override
                                public Dice<T> getDice() {
                                    return Dice.of(myDie.getDice());
                                }

                                @Override
                                public List<DieResult<T>> getMembers() {
                                    return myDie.getResults();
                                }

                                @Override
                                public Collector<? super T, ?, ? extends V> getCombiner() {
                                    return this.combiner;
                                }

                            };
                        }

                    };
                }
            };
        };

        /**
         * Create a new die roll.
         * 
         * @param <V> The die roll result type.
         * @param name The die roll name.
         * @param dice The dice rolled for the die roll.
         * @param valueConverter The function converting the rolled die results into die roll value.
         * @return The die roll result with given name, dice, and converter.
         * @throws NoSuchElementException The die roll is not available.
         */
        public <V> DiceMechanics.DieRoll<T, V> createDieRoll(String name,
                Dice<? extends T> dice,
                Function<? super java.util.List<? extends T>, ? extends V> valueConverter)
                throws NoSuchElementException;

    }

    /**
     * Create a factory producing roll modifiers.
     * 
     * @param <T> The value type of the die values.
     */
    public static interface RollModifierFactory<T> {

        /**
         * Create a roll modifier.
         * 
         * @param <V>      The value type of the roll result.
         * @param name     The name of the modifier.
         * @param modifier The modifier function.
         * @return The roll modifier applying the given modifier function to the roll
         *         result.
         */
        default <V> DiceMechanics.RollModifier<T, V> createRollModifier(String name,
                Function<RollResult<? extends T, ? extends V>, RollResult<T, V>> modifier) {
            return (RollResult<T, V> result) -> modifier.apply(result);
        }
    }

    /**
     * Get the roll modifier factory for a type.
     * 
     * @param <V>        The value type of the factory.
     * @param resultType The result value type of the factory.
     * @return The roll modifier factory ensuring all results are of the given type.
     * @throws NoSuchElementException There is no factory for given type.
     */
    public <V> RollModifierFactory<T> getRollModifierFactory(Class<? extends V> resultType)
            throws NoSuchElementException;

    /**
     * Get roll modifier factory.
     * 
     * @return The roll modifier factory.
     */
    public RollModifierFactory<T> getRollModifierFactory();

    /**
     * The die roll factory of the mechanic factory.
     * 
     * @return The die roll factory producing results of the given type.
     * @throws NoSuchElementException There is no factory for the given die roll
     *                                result type.
     */
    public <V> DieRollFactory<T> getDieRollFactory(Class<? extends V> resultType) throws NoSuchElementException;

    /**
     * Get die roll factory.
     * 
     * @return The die roll factory of the dice mechanics.
     */
    public DieRollFactory<T> getDieRollFactory();

    /**
     * Create a roll modifier.
     * 
     * @param <V>      The value type of the roll result.
     * @param name     The name of the modifier.
     * @param modifier The modifier function.
     * @return The roll modifier applying the given modifier function to the roll
     *         result.
     */
    default <V> DiceMechanics.RollModifier<T, V> createRollModifier(String name,
            Function<RollResult<? extends T, ? extends V>, RollResult<T, V>> modifier) {
        return getRollModifierFactory().createRollModifier(name, modifier);
    }

    /**
     * Create a new die roll.
     * 
     * @param <V>            The value type of the die roll results.
     * @param name           The name of the die roll.
     * @param dice           The dice of the die roll.
     * @param valueConverter The function converting the member die results into
     *                       roll result.
     * @return The die roll performing the die roll of the given dice.Ja
     */
    default <V> DiceMechanics.DieRoll<T, V> createDieRoll(
            String name,
            Dice<? extends T> dice,
            Function<? super java.util.List<? extends T>, ? extends V> valueConverter) throws NoSuchElementException {
        try {
            return getDieRollFactory().createDieRoll(name, dice, valueConverter);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(DIE_ROLL_NOT_FOUND_MESSAGE);
        }
    }

    /**
     * Create a new Dice Mechanics.
     * 
     * @return The new dice mechanics.
     * @throws ConfigurationException The factory is not properly configured.
     */
    public DiceMechanics<T> createMechanics() throws FactoryConfigurationException;
}
