package com.sportradar.wc;

import org.jetbrains.annotations.NotNull;

/**
 * MatchEntry is a record class making it a thread safe and optimal structure to keep the match result
 * */
public record MatchEntry(CountryEnum homeTeam, CountryEnum awayTeam, int homeScore, int awayScore) implements Comparable<MatchEntry> {

    /**
     * Match key is composed of the names of the home and the away team
     * */
    public String key() {
        return this.homeTeam() + "-" + this.awayTeam();
    }

    /**
     * Total score is a sum of both scores.
     * */
    public int totalScore() {
        return this.homeScore() + this.awayScore();
    }

    /**
     * Two objects are equal if their keys are equal.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MatchEntry)) return false;
        return this.key().equals(((MatchEntry)other).key());
    }

    /**
     * If match is a tie (equals returns true) - then we do not want to return 0 as we are using 0 to identify the same team in our score board.
     * "-1" is returned as we want second team to be presented higher (or more to the left) - hence the lower number.
     */
    @Override
    public int compareTo(@NotNull MatchEntry o) {
        if (this.equals(o)) {
            return 0;
        }
        int scoreA = this.totalScore();
        int scoreB = o.totalScore();


        if (scoreA == scoreB) {
            return -1;
        }
        return Integer.compare(scoreB, scoreA);
    }
}
