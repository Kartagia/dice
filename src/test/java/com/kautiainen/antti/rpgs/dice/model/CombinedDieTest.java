package com.kautiainen.antti.rpgs.dice.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;

import org.junit.jupiter.api.Test;

public class CombinedDieTest {

    /**
     * A standard card suite.
     */
    public static enum Suite {
        Club, Diamond, Heart, Spade;
    }

    /**
     * The suite of a tarot card.
     */
    public static enum TarotSuite {
        Club, Coin, Goblet, Sword;
    }

    /**
     * Card value represents card.
     */
    public static class CardValue implements Comparable<CardValue> {

        /**
         * The caption of the card.
         */
        private String caption;

        /**
         * The value of the card.
         */
        private int value;

        /**
         * Create a named card.
         * 
         * @param name  The name of the card.
         * @param value The value of the card.
         */
        public CardValue(String name, int value) {
            this.caption = name;
            this.value = value;
        }

        /**
         * Create a valued card. The name of the valued card is the card value.
         * 
         * @param value The value of the card.
         */
        public CardValue(int value) {
            this(Integer.toString(value), value);
        }

        /**
         * Get the integer value of the card value.
         */
        public int intValue() {
            return this.value;
        }

        @Override
        public int compareTo(CardValue other) {
            return Integer.compare(intValue(), other.intValue());
        }

        @Override
        public String toString() {
            return this.caption;
        }
    }

    /**
     * Class representing a card.
     */
    public static class Card implements Comparable<Card> {

        /**
         * The suite of the card.
         */
        private final Suite suite;

        /**
         * The value of the card.
         */
        private final CardValue value;

        public Card(Suite suite, CardValue value) {
            this.suite = suite;
            this.value = value;
        }

        @Override
        public int compareTo(Card other) {
            int result = this.getSuite().compareTo(other.getSuite());
            if (result == 0) {
                result = getValue().compareTo(other.getValue());
            }
            return result;
        }

        public Suite getSuite() {
            return this.suite;
        }

        public CardValue getValue() {
            return this.value;
        }
    }

    public static final List<List<Card>> TEST_DECKS = Arrays.asList(
            Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14).stream().flatMap(
                    value -> {
                        return Arrays.stream(Suite.values()).map(
                                suite -> (value < 11 ? new Card(suite, new CardValue(value))
                                        : new Card(suite, new CardValue(
                                                Arrays.asList("J", "Q", "K", "A").get(value - 11), value))));
                    }).toList(),
            Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14).stream().flatMap(
                    value -> {
                        return Arrays.stream(Suite.values()).map(
                                suite -> (value < 11 ? new Card(suite, new CardValue(value))
                                        : new Card(suite, new CardValue(
                                                Arrays.asList("J", "Q", "K", "A").get(value - 11), value == 14 ? 1 : value))));
                    }).toList());

    public static final List<CombinedDie<Card, Integer>> getTested() {
        return TEST_DECKS.stream().map( 
            members -> {
                List<Die<Card>> dieMembers = members.stream().map( card -> {
                    Die<Card> newDie = Die.of(members);
                    return newDie;
                }).toList();
                Collector<Card, AtomicInteger, Integer> collector = Collector.of( 
                    () -> (new AtomicInteger(0)), 
                    (AtomicInteger res, Card value) -> {
                        res.addAndGet(value.getValue().intValue());
                }, (AtomicInteger head, AtomicInteger tail) -> {
                    head.addAndGet(tail.get());
                    return head;
                }, 
                AtomicInteger::get
                );
                Collector<? super Card, ?, ? extends Integer> castedCollector = collector;
                CombinedDie<Card, Integer> result = new CombinedDie<Card, Integer>(dieMembers, castedCollector);
                return result;
            }
        ).toList();
    }

    @Test
    void testGetCombiner() {

    }

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
    void testRoll() {

    }
}
