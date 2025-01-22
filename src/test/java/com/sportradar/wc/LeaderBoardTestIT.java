package com.sportradar.wc;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static com.sportradar.wc.CountryEnum.*;
import static org.junit.jupiter.api.Assertions.*;

public class LeaderBoardTestIT {
    @BeforeEach
    void clear() throws InterruptedException {
        LeaderBoard.getInstance().clearResults();
    }

    @Test
    public void testAcceptance() throws InterruptedException {
        LeaderBoard lb = initLB();
        lb.updateMatch(new MatchEntry(MEXICO, CANADA, 0, 5));
        lb.updateMatch(new MatchEntry(SPAIN, BRAZIL, 10, 2));
        lb.updateMatch(new MatchEntry(GERMANY, FRANCE, 2, 2));
        lb.updateMatch(new MatchEntry(URUGUAY, ITALY, 6, 6));
        lb.updateMatch(new MatchEntry(ARGENTINA, AUSTRALIA, 3, 1));

        assertEquals(List.of(
                new MatchEntry(URUGUAY, ITALY, 6, 6),
                new MatchEntry(SPAIN, BRAZIL, 10, 2),
                new MatchEntry(MEXICO, CANADA, 0, 5),
                new MatchEntry(ARGENTINA, AUSTRALIA, 3, 1),
                new MatchEntry(GERMANY, FRANCE, 2, 2)
        ), lb.getResults());
    }

    @Test
    public void testInit() throws InterruptedException  {
        Assertions.assertTrue(initLB().getResults().isEmpty());
    }

    @Test
    public void testScoreBoardOrder() throws InterruptedException {
        LeaderBoard lb1 = initLB();
        lb1.updateMatch(new MatchEntry(SPAIN, BRAZIL, 10, 2));
        lb1.updateMatch(new MatchEntry(URUGUAY, ITALY, 6, 6));
        assertEquals(List.of(
                new MatchEntry(URUGUAY, ITALY, 6, 6),
                new MatchEntry(SPAIN, BRAZIL, 10, 2)
        ), lb1.getResults());

        LeaderBoard lb2 = initLB();
        lb2.updateMatch(new MatchEntry(URUGUAY, ITALY, 6, 6));
        lb2.updateMatch(new MatchEntry(SPAIN, BRAZIL, 10, 2));

        assertEquals(List.of(
                new MatchEntry(SPAIN, BRAZIL, 10, 2),
                new MatchEntry(URUGUAY, ITALY, 6, 6)
        ), lb2.getResults());
    }

    @Test
    public void testThreadSafety() throws InterruptedException {
        final LeaderBoard lb = initLB();
        final int threadCount = listOfTestEntries.size();
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            final int threadNumber = i;
            new Thread(() -> {
                try {
                    lb.updateMatch(listOfTestEntries.get(threadNumber));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        latch.await();
        List<MatchEntry> scoreBoard = lb.getResults();

        // Check if all matches are in the resulting board
        assertEquals(threadCount, scoreBoard.size(), "Must contain all matches");

        // Check if the score board is order by size
        for (int i = 1; i < threadCount; i++) {
            assertNotEquals(scoreBoard.get(i).key(), scoreBoard.get(i-1).key(), "Same entry twice in the result.");
            assertTrue(scoreBoard.get(i).totalScore() <= scoreBoard.get(i-1).totalScore(), "Matches have to be sorted by total score.");
        }
    }

    private static final CountryEnum[] wcCountries = values();
    CountryEnum getRandomCountry() {
        return wcCountries[new Random().nextInt(wcCountries.length)];
    }

    @Test
    public void testPerformance() throws InterruptedException {
        final LeaderBoard lb = LeaderBoard.getInstance();
        lb.clearResults();
        try (ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100)) {
            for (int i = 0; i < 100000; i++) {
                executor.submit(() -> {
                    MatchEntry matchEntry = new MatchEntry(getRandomCountry(), getRandomCountry(), (int)(Math.random()*10), (int)(Math.random()*10));
                    try {
                        if (!lb.containsMatch(matchEntry) && matchEntry.homeTeam() != matchEntry.awayTeam()) {
                            lb.updateMatch(matchEntry);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        List<MatchEntry> scoreBoard = lb.getResults();
        // Check if the score board is order by size
        for (int i = 1; i < scoreBoard.size(); i++) {
            MatchEntry prev = scoreBoard.get(i-1);
            MatchEntry curr = scoreBoard.get(i);
            assertNotEquals(curr.key(), prev.key(), "Same entry twice in the result.");
            assertTrue(curr.totalScore() <= prev.totalScore(), curr.totalScore() +  " - " +  prev.totalScore());
        }
    }

    private static final List<MatchEntry> listOfTestEntries = List.of(
            new MatchEntry(MEXICO, CANADA, 0, 5),
            new MatchEntry(SPAIN, BRAZIL, 10, 2),
            new MatchEntry(GERMANY, FRANCE, 2, 2),
            new MatchEntry(URUGUAY, ITALY, 6, 6),
            new MatchEntry(ARGENTINA, AUSTRALIA, 3, 1)
    );

    private static @NotNull LeaderBoard initLB() throws InterruptedException {
        final LeaderBoard lb = LeaderBoard.getInstance();
        lb.clearResults();
        for (MatchEntry matchEntry : listOfTestEntries) {
            lb.addMatch(new MatchEntry(matchEntry.homeTeam(), matchEntry.awayTeam(), 0, 0));
        }
        return lb;
    }
}
