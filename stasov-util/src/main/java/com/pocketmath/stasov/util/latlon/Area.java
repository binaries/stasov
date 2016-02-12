package com.pocketmath.stasov.util.latlon;

/**
 * Created by etucker on 2/9/16.
 */
public abstract class Area implements Comparable {

    public static class Result {
        public static final Result POSITIVE_MATCH = new Result(1f,0f);
        public static final Result POSITIVE_MATCH_VERY_BOGUS = new Result(1f,1f);
        public static final Result NEGATIVE_MATCH = new Result(-1f,0f);
        public static final Result NEGATIVE_MATCH_VERY_BOGUS = new Result(-1f,1f);

        private final float score;
        private final float bogusLikelihood;

        public Result(final float score, final float bogusLikelihood) {
            this.score = score;
            this.bogusLikelihood = bogusLikelihood;
        }

        public float getScore() {
            return score;
        }

        public float getBogusLikelihood() {
            return bogusLikelihood;
        }
    }

    abstract Result scoredIn(final double latitude, final double longitude);

    /**
     *
     * It's recommended that subclasses override this method when it's possible to
     * be more efficient than their implementation of scoredIn.
     *
     * @param latitude
     * @param longitude
     * @return 0 = no opinion or undetermined, 1 = positive match, -1 is definitely not matched
     */
    int inBounds(final double latitude, final double longitude) {
        final Result r = scoredIn(latitude, longitude);
        final float score = r.getScore();
        if (score > 0)
            return r.getBogusLikelihood() <= 0 ? 1 : -1;
        else if (score == 0)
            return r.getBogusLikelihood() <= 0 ? 0 : -1;
        else {
            assert(score < 0);
            return -1;
        }
    }

    /**
     *
     * It's recommended that subclasses override this method when it's possible to
     * be more efficient than other methods.
     *
     * @param latitude
     * @param longitude
     */
    boolean inBoundsBoolean(final double latitude, final double longitude) {
        return inBounds(latitude, longitude) > 0;
    }

        @Override
    public int compareTo(Object o) {
        throw new UnsupportedOperationException();
    }
}
