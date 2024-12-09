package com.kautiainen.antti.rpgs.dice.model;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

/**
 * An interface representing a dice system.
 * 
 * @param <T> The value type of the dice values.
 */
public interface DiceMechanics<T> {

    /**
     * A roll modifier alters teh roll result.
     */
    public static interface RollModifier<T, V> extends Function<RollResult<T, V>, RollResult<T, V>> {

        /**
         * Get the name of the modifier.
         * 
         * @return The optional name of the modifier.
         */
        default Optional<String> getName() {
            return Optional.empty();
        }

        /**
         * Modify roll result by the modifier.
         * 
         * @param result The modified roll result.
         * @return The modified roll result.
         */
        @Override
        public RollResult<T, V> apply(RollResult<T, V> result);
    }

    /**
     * A die roll reprsents a named die roll of the dice mechanics.
     * 
     * @todo Add override to the roll with character value after creating
     *       characters.
     * @param <T> The value type of hte rolled dice.
     * @param <V> The value type of the roll result.
     */
    public interface DieRoll<T, V> {

        /**
         * The name of the roll.
         * 
         * @return The name of the roll.
         */
        public String getName();

        /**
         * Get the roll result.
         * 
         * @return Perform the roll.
         */
        public RollResult<T, V> roll();

        /**
         * Get the roll result with a list of modifiers.
         * 
         * @param modifiers The modifiers applied to the roll.
         * @return The modified roll result.
         * @throws IllegalArgumentException Any modifeir was invalid.
         */
        default RollResult<T, V> roll(List<RollModifier<T, V>> modifiers) throws IllegalArgumentException {
            RollResult<T, V> result = roll();
            for (RollModifier<T, V> mod : modifiers) {
                result = mod.apply(result);
            }
            return result;
        }
    }

    /**
     * Create a die roll.
     * 
     * @param <V>       The value type of the roll.
     * @param rollName  The name of the roll.
     * @param dice      The dice rolled.
     * @param modifiers The modifiers of the roll.
     * @return The roll created from given dice and modifiers.
     */
    public <V> DieRoll<T, V> createRoll(String rollName, Dice<T> dice, List<RollModifier<T, V>> modifiers);

    /**
     * Create die roll of the die mechanics.
     * 
     * @param <V>      The result type.
     * @param rollName The roll name.
     * @return The die roll of the given name.
     * @throws NoSuchElementException The given die roll does not exist.
     */
    public <V> DieRoll<T, V> createRoll(String rollName) throws NoSuchElementException;

    /**
     * Create a new die.
     * 
     * @param dieName The die name.
     * @return The die with the given name.
     * @throws NoSuchElementException The given die is not supported by the dice
     *                                mechanics.
     */
    public Die<T> createDie(String dieName) throws NoSuchElementException;

    /**
     * Create a new roll result.
     * 
     * @param <V>      The roll value type.
     * @param rollName The roll name.
     * @param dice     The dice rolled.
     * @return The roll with given result value.
     * @throws NoSuchElementException The roll does not exists.
     * @throws ClassCastException     The roll does not produce given result.
     */
    default <V> RollResult<T, V> createRollResult(String rollName, Dice<T> dice, List<RollModifier<T, V>> modifiers)
            throws NoSuchElementException, ClassCastException {
        DieRoll<T, V> result = createRoll(rollName, dice, modifiers);
        return result.roll();
    }

    /**
     * Create a new roll result.
     * 
     * @param <V>      The roll value type.
     * @param rollName The roll name.
     * @param dice     The dice rolled.
     * @return The roll with given result value.
     * @throws NoSuchElementException The roll does not exists.
     * @throws ClassCastException     The roll does not produce given result.
     */
    default <V> RollResult<T, V> createRollResult(String rollName, Dice<T> dice)
            throws NoSuchElementException, ClassCastException {
        return createRollResult(rollName, dice, Collections.emptyList());
    }

}
