package com.sportradar.wc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sportradar.wc.CountryEnum.*;
import static org.junit.jupiter.api.Assertions.*;


public class LeaderBoardTest {

    @BeforeEach
    void clear() throws InterruptedException {
        LeaderBoard.getInstance().clearResults();
    }

    @Test
    void getInstance() {
        assertNotNull(LeaderBoard.getInstance());
    }

    @Test
    void getResults() throws InterruptedException {
        assertNotNull(LeaderBoard.getInstance().getResults());
    }

    @Test
    void clearResults() throws InterruptedException {
        LeaderBoard lb = LeaderBoard.getInstance();
        lb.addMatch(new MatchEntry(ARGENTINA, SLOVENIA, 0, 0));
        lb.updateMatch(new MatchEntry(ARGENTINA, SLOVENIA, 2,1));
        assertEquals(1, lb.getResults().size());
        lb.clearResults();
        assertEquals(0, lb.getResults().size());
    }

    @Test
    void addMatch() throws InterruptedException {
        LeaderBoard lb = LeaderBoard.getInstance();
        lb.addMatch(new MatchEntry(ARGENTINA, SLOVENIA, 0, 0));
        assertTrue(lb.containsMatch(new MatchEntry(ARGENTINA, SLOVENIA, 0,0)));
    }

    @Test
    void updateMatch() throws InterruptedException {
        LeaderBoard lb = LeaderBoard.getInstance();
        lb.addMatch(new MatchEntry(ARGENTINA, SLOVENIA, 0, 0));
        assertTrue(lb.containsMatch(new MatchEntry(ARGENTINA, SLOVENIA, 0,0)));
    }

    @Test
    void finishMatch() throws InterruptedException {
        LeaderBoard lb = LeaderBoard.getInstance();
        // add two matches
        lb.addMatch(new MatchEntry(ARGENTINA, SLOVENIA, 0, 0));
        lb.addMatch(new MatchEntry(URUGUAY, BRAZIL, 0, 0));

        lb.updateMatch(new MatchEntry(ARGENTINA, SLOVENIA, 5, 2));
        lb.updateMatch(new MatchEntry(URUGUAY, BRAZIL, 10, 2));

        lb.finishMatch(new MatchEntry(ARGENTINA, SLOVENIA, 0, 0));
        assertEquals(1, lb.getResults().size());
        assertTrue(lb.containsMatch(new MatchEntry(URUGUAY, BRAZIL, 0, 0)));
    }
}
