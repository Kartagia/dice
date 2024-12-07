package com.kautiainen.antti.rpgs.dice.model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class DiceTest {
    @Test
    void testGetDice() {

    }

    @Test
    void testGetRerollableResults() {

    }

    @Test
    void testGetResults() {

    }

    @Test
    void testOf() {

        List<Die<? extends Integer>> members = java.util.Arrays.asList(Die.of(4), Die.of(6), Die.of(8), 
        Die.of(10), Die.of(12), Die.of(20));

        members.forEach( singleDie -> {
            Dice<Integer> result = Dice.of(java.util.Arrays.asList(singleDie));
            assertTrue(result != null);
            assertTrue(result.getDice() != null);
            assertTrue(result.getDice().size() == 1);
            assertEquals(singleDie, result.getDice().get(0));
        });
    }
}
