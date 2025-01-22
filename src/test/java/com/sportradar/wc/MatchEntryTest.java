package com.sportradar.wc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchEntryTest {

    @Test
    void testEquals() {
        MatchEntry m1 = new MatchEntry(CountryEnum.SLOVENIA, CountryEnum.AUSTRALIA, 1, 2);
        MatchEntry m2 = new MatchEntry(CountryEnum.SLOVENIA, CountryEnum.AUSTRALIA, 0, 0);
        assertTrue(m1.equals(m2));
    }

    @Test
    void compareTo() {
        MatchEntry m1 = new MatchEntry(CountryEnum.SLOVENIA, CountryEnum.AUSTRALIA, 0, 1);
        MatchEntry m2 = new MatchEntry(CountryEnum.CROATIA, CountryEnum.JAPAN, 2, 0);
        assertTrue(m2.compareTo(m1) < 0);
    }
}