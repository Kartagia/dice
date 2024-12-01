package com.kautiainen.antti.rpgs.dice.model;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class SimpleDieTest {

    /**
     * The error message constant indicating the test failed due result value was not within the side container.
     */
    public static final String ROLL_RESULT_VALUE_WAS_NOT_WITHIN_THE_SIDE_CONTAINER = "Roll result value was not within the side container";

    @Test
    void construction() {

    }

    /**
     * Convert a list into a list of strings.
     * 
     * @param <E> The list item type.
     * @param list The converted list.
     * @param stringifier The function converting the list items.
     * @return The list containing string representations of the list.
     */
    public static<E> List<String> toStringList(List<? extends E> list, Function<? super E, ? extends CharSequence> stringifier) {
        return list.stream().map(item -> (item == null ? null : stringifier.apply(item).toString())).toList();
    }

    /**
     * Convert a list into a human readable string representation.
     * 
     * @param <E> The list item type.
     * @param list The converted list.
     * @param stringifier The function converting the list items.
     * @return The string representation of the list.
     */
    public static<E> String listToString(List<? extends E> list, Function<? super E, ? extends CharSequence> stringifier) {
        StringBuilder result = new StringBuilder();
        CharSequence[] strings = list.stream().map(stringifier).toArray( count -> (new String[count]));
        result.append(String.join(",", strings));
        result.insert(0, "[");
        result.append("]");
        return result.toString();
    }

    

    public static List<List<Integer>> sides = Arrays.asList(Arrays.asList(1,2,3), Arrays.asList(1,2,3,4,5,6), Arrays.asList(-1,0,1));

    @Test
    void testRoll() {
        List<SimpleDie<Integer>> dice = sides.stream().map(SimpleDie::new).toList();

        for (int i=0; i < dice.size(); i++) {
            for (int attempt=0; attempt < 1000; attempt++) {
                assertTrue(sides.get(i).contains(dice.get(i).roll()), "Roll result was not within the side container");
            }
        }
    }

    @Test
    void testToString() {
        List<SimpleDie<Integer>> dice = sides.stream().map(SimpleDie::new).toList();

        List<String> expectedResults = sides.stream().map( sideList -> {
            return String.format("d%s", listToString(sideList, side -> (Integer.toString(side))));
        }).toList();
        for (int i=0; i < dice.size(); i++) {
            String expResult = expectedResults.get(i);
            SimpleDie<Integer> instance = dice.get(i);
            assertEquals(expResult, instance.toString());
        }

    }

    @Test
    void testGetResult() {
        List<SimpleDie<Integer>> dice = sides.stream().map(SimpleDie::new).toList();

        for (int i=0; i < dice.size(); i++) {
            for (int attempt=0; attempt < 1000; attempt++) {
                DieResult<Integer> result = dice.get(i).getResult();
                assertTrue(sides.get(i).contains(result.getValue()), ROLL_RESULT_VALUE_WAS_NOT_WITHIN_THE_SIDE_CONTAINER);
            }
        }

    }
}
