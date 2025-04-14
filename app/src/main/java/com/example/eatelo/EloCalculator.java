package com.example.eatelo;

public class EloCalculator {
    private static final int K_FACTOR = 32;

    public static class EloChange {
        public final int winnerChange;
        public final int loserChange;

        public EloChange(int winnerChange, int loserChange) {
            this.winnerChange = winnerChange;
            this.loserChange = loserChange;
        }
    }

    public static EloChange calculateRatingChanges(int winnerElo, int loserElo) {
        double expectedWinner = 1.0 / (1.0 + Math.pow(10, (loserElo - winnerElo) / 400.0));
        double expectedLoser = 1.0 / (1.0 + Math.pow(10, (winnerElo - loserElo) / 400.0));

        int winnerChange = (int) (K_FACTOR * (1 - expectedWinner));
        int loserChange = (int) (K_FACTOR * (0 - expectedLoser));

        return new EloChange(winnerChange, loserChange);
    }
}