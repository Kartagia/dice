package com.kautiainen.antti.rpgs.dice.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collector;

public class SimpleDiceMechanics<T> implements DiceMechanics<T> {

    /**
     * The error message indicating the die roll name was reserved.
     */
    public static final String DIE_ROLL_NAME_RESERVED_MESSAGE = "Die roll name already reserved";

    /**
     * Simple die roll is a simple implementation of a die roll.
     */
    public static class SimpleDieRoll<T, V> implements DiceMechanics.DieRoll<T, V> {

        /**
         * The message indicating the combiner was undefined.
         */
        public static final String UNDEFINED_COMBINER_MESSAGE = "Undefined combiner not allowed";

        /**
         * The error message indicating the name was undefined.
         */
        public static final String UNDEFINED_NAME_MESSAGE = "Undefined names not allowed";

        /**
         * The error message indicating the dice was undefined.
         */
        public static final String UNDEFINED_DICE_MESSAGE = "Undefined dice not supported";

        private String name;

        private List<Die<? extends T>> dice;

        private Collector<T, List<T>, V> combiner;

        /**
         * The roll modifiers of simple die roll.
         */
        private ArrayList<RollModifier<T, V>> modifiers;

        /**
         * Create a new simple die roll using evaluator function.
         * 
         * @param name      The name of the roll.
         * @param evaluator The evaluator function determining the roll value from
         *                  rolled value list.
         * @param dice      The dice of the roll.
         * @param modifiers The roll modifiers of the created simple die.
         */
        public SimpleDieRoll(
                String name,
                Function<? super List<T>, ? extends V> evaluator,
                Collection<? extends Die<? extends T>> dice,
                List<? extends RollModifier<T, V>> modifiers) {
            this.name = Objects.requireNonNull(name, UNDEFINED_NAME_MESSAGE);
            this.dice = Collections
                    .unmodifiableList(new ArrayList<>(Objects.requireNonNull(dice, UNDEFINED_DICE_MESSAGE)));
            combiner = Collector.of(
                    () -> ((List<T>) new ArrayList<T>()),
                    (List<T> head, T value) -> {
                        head.add(value);
                    },
                    (List<T> head, List<T> tail) -> {
                        head.addAll(tail);
                        return head;
                    },
                    (List<T> head) -> {
                        return evaluator.apply(head);
                    });
            this.modifiers = new ArrayList<>(modifiers);
        }

        /**
         * Create a new simple die roll using evaluator function.
         * 
         * @param name      The name of the roll.
         * @param evaluator The evaluator function determining the roll value from
         *                  rolled value list.
         * @param dice      The dice of the roll.
         */
        public SimpleDieRoll(
                String name,
                Function<? super List<T>, ? extends V> evaluator,
                Collection<? extends Die<? extends T>> dice) {
            this(name, evaluator, dice, Collections.emptyList());
        }

        @SuppressWarnings("unchecked")
        public SimpleDieRoll(String name, Function<? super List<T>, ? extends V> evaluator, Die<? extends T>... dice) {
            this(name, evaluator, Arrays.asList(dice));
        }

        /**
         * Create a new simple die roll with collector.
         * 
         * @param name      The name of the roll.
         * @param collector The collector combining the roll results.
         * @param dice      The dice of the roll.
         */
        public SimpleDieRoll(String name, Collector<? super T, ?, ? extends V> collector,
                List<? extends Die<? extends T>> dice) {

            this.name = Objects.requireNonNull(name, UNDEFINED_NAME_MESSAGE);
            this.combiner = Objects.requireNonNull(combiner, UNDEFINED_COMBINER_MESSAGE);
            this.dice = Collections
                    .unmodifiableList(new ArrayList<>(Objects.requireNonNull(dice, UNDEFINED_DICE_MESSAGE)));
        }

        @Override
        public String getName() {
            return this.name;
        }

        /**
         * Get evaluator function.
         * 
         * @return The evaluator function.
         */
        public Collector<? super T, ?, ? extends V> getCombiner() {
            return combiner;
        }

        /**
         * Get the roll modifiers.
         * 
         * @return The list of the roll modifiers.
         */
        public List<? extends RollModifier<T, V>> getModifiers() {
            return Collections.unmodifiableList(modifiers);
        }

        @Override
        public RollResult<T, V> roll() {
            RollResult<T, V> result = RollResult.of(dice, combiner);
            for (RollModifier<T, V> rollModifier : modifiers) {
                result = rollModifier.apply(result);
            }
            return result;
        }

    }

    /**
     * A simple roll modifier implementation using functional implementation.
     */
    public static class SimpleRollModifier<T, V> implements DiceMechanics.RollModifier<T, V> {

        /**
         * The optional name of the roll modifier.
         */
        private final Optional<String> name;

        /**
         * The function performing the roll modifier.
         */
        private final Function<RollResult<T, V>, ? extends RollResult<T, V>> modifier;

        /**
         * Create an identity modifier.
         * 
         * @param <T> The rolled die value type.
         * @param <V> The roll result value type.
         * @return The function generating identity modifier returning the given roll
         *         result as is.
         */
        public static <T, V> Function<RollResult<T, V>, ? extends RollResult<T, V>> identityModifierFunction() {
            return (value) -> (value);
        }

        public SimpleRollModifier(String name,
                Function<RollResult<T, V>, ? extends RollResult<T, V>> modifierFunction) {
            this.modifier = (modifierFunction == null ? identityModifierFunction() : modifierFunction);
            this.name = Optional.ofNullable(name);
        }

        /**
         * Create a simple roll modifier using modifier function.
         * 
         * @param modifierFunction The function modifying the roll result. Defaults to
         *                         an indentity function
         *                         returning its parameter as is.
         */
        public SimpleRollModifier(
                Function<RollResult<T, V>, ? extends RollResult<T, V>> modifierFunction) {
            this(null, modifierFunction);
        }

        @Override
        public Optional<String> getName() {
            return name;
        }

        @Override
        public RollResult<T, V> apply(RollResult<T, V> result) {
            return modifier.apply(result);
        }

    }

    public interface Entry<T, V> {
        public Class<? extends V> getResultType();

        public DieRoll<T, V> getRoll();
    }

    /**
     * A record implementation of an entry record.
     */
    protected record EntryRecord<T, V>(Class<? extends V> type, Function<? super List<T>, ? extends V> evaluator,
            DieRoll<T, V> roll) implements Entry<T, V> {

        @Override
        public Class<? extends V> getResultType() {
            return this.type;
        }

        @Override
        public DieRoll<T, V> getRoll() {
            return this.roll;
        }

    }

    private ConcurrentHashMap<String, Entry<T, ?>> supportedRolls = new ConcurrentHashMap<>();

    public SimpleDiceMechanics() {

    }

    /**
     * Create and register a named roll with dice using an evaluation function and list of modifiers.
     * 
     * @param <V> The value type of roll result.
     * @param rollName The name of the roll.
     * @param dice The dice used for the roll.
     * @param evaluator The evaluator of the roll.
     * @param modifiers The modifiers of the roll.
     * @return The die roll defining a new die roll.
     * @throws IllegalArgumentException
     */
    public <V> DieRoll<T, V> createRoll(
            String rollName,
            Dice<T> dice,
            Function<? super List<T>, ? extends V> evaluator,
            List<? extends RollModifier<T, V>> modifiers)
            throws IllegalArgumentException {
        if (supportedRolls.containsKey(rollName)) {
            throw new IllegalArgumentException(DIE_ROLL_NAME_RESERVED_MESSAGE);
        } else {
            SimpleDieRoll<T, V> roll = new SimpleDieRoll<>(rollName, evaluator, dice.getDice(), modifiers);
            supportedRolls.put(rollName, new EntryRecord<>(null, evaluator, roll));
            return roll;
        }
    }

    @Override
    public <V> DieRoll<T, V> createRoll(String rollName, Dice<T> dice,
            List<RollModifier<T, V>> modifiers) {

        Entry<T, ?> entry = supportedRolls.get(rollName);
        try {
            @SuppressWarnings("unchecked")
            DieRoll<T, V> result = (DieRoll<T, V>) entry.getRoll();
            return result;
        } catch (ClassCastException e) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public <V> DieRoll<T, V> createRoll(String rollName) throws NoSuchElementException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createRoll'");
    }

    @Override
    public Die<T> createDie(String dieName) throws NoSuchElementException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createDie'");
    }

}
