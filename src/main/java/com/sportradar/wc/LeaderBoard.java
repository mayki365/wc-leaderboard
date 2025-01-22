package com.sportradar.wc;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Represents a leaderboard that maintains rankings and scores.
 * This class implements the Singleton pattern to ensure only one instance exists.
 */
public class LeaderBoard {

    @Contract(pure = true)
    private LeaderBoard() {}

    private static final Semaphore SEMAPHORE = new Semaphore(1);

    // Switch to ConcurrentSkipListSet decreases performance.
    // Thread safety is guaranteed by usage of Semaphore.
    private final Set<MatchEntry> leaderboard = new TreeSet<>();

    // Switching to ConcurrentHashMap decreases performance.
    // Thread safety is guaranteed by usage of Semaphore
    private final Map<String, MatchEntry> teamBoard = new HashMap<>();

    /**
     * Returns the Singleton instance of the leaderboard.
     * This approach is:
     * 1. thread safe due to static instantiation of the class,
     * 2. instantiation is deferred until class is actually used,
     * 3. static factory object creation and instance is not accessible outside the getInstance() method.
     * */
    private static class Holder {
        private static final LeaderBoard INSTANCE = new LeaderBoard();
    }

    /**
     * Returns instance of the LeaderBoard library.
     */
    public static LeaderBoard getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Returns current leaderboard sorted by the total score
     * @return sorted leaderboard as a list
     */
    public List<MatchEntry> getResults() throws InterruptedException {
        List<MatchEntry> boardResult;
        try {
            SEMAPHORE.acquire();
            boardResult = new ArrayList<>(this.leaderboard);
        } finally {
            SEMAPHORE.release();
        }
        return boardResult;
    }

    /**
     * Clears the board and removes the teams.
     */
    public void clearResults() throws InterruptedException  {
        try {
            SEMAPHORE.acquire();
            this.leaderboard.clear();
            this.teamBoard.clear();
        } finally {
            SEMAPHORE.release();
        }
    }

    /**
     * Adds the match to the team board.
     * @param matchEntry - match entry to be added (score needs to be 0:0).
     */
    public void addMatch(@NotNull MatchEntry matchEntry) throws InterruptedException {
        try {
            if (matchEntry.homeScore() == 0 && matchEntry.awayScore() == 0) {
                SEMAPHORE.acquire();
                teamBoard.put(matchEntry.key(), matchEntry);
            } else {
                throw new IllegalArgumentException("Initial store has to be 0:0");
            }
        } finally {
            SEMAPHORE.release();
        }
    }

    /**
     * Checks if a leader board contains a match.
     * @param matchEntry - specify the home and away team for match lookup.
     * @return true if home-away team match is already entered, false otherwise.
     */
    public boolean containsMatch(@NotNull MatchEntry matchEntry) throws InterruptedException {
        boolean containsMatch;
        try {
            SEMAPHORE.acquire();
            containsMatch = this.teamBoard.containsKey(matchEntry.key());
        } finally {
            SEMAPHORE.release();
        }
        return containsMatch;
    }

    /**
     * Use this function to enter match results
     * @param matchEntry - match entry to be updated with corresponding scores.
     */
    public void updateMatch(
            @NotNull MatchEntry matchEntry)
    throws InterruptedException {
        try {
            SEMAPHORE.acquire();
            // Check if the teams are entered in the team board
            if (!this.teamBoard.containsKey(matchEntry.key())) {
                // create new team entry if it is not
                this.teamBoard.put(matchEntry.key(), matchEntry);
            } else {
                this.leaderboard.remove(matchEntry);
            }
            this.leaderboard.add(matchEntry);
        } finally {
            SEMAPHORE.release();
        }
    }

    /**
     * Removes the match from the leaderboard.
     */
    public void finishMatch(@NotNull MatchEntry matchEntry) throws InterruptedException {
        try {
            SEMAPHORE.acquire();
            MatchEntry entry = this.teamBoard.remove(matchEntry.key());
            this.leaderboard.remove(entry);
        } finally {
            SEMAPHORE.release();
        }
    }
}
