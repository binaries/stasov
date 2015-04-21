package com.pocketmath.stasov.util;

/**
 * Created by etucker on 4/12/15.
 */
public class StasovStrings {

    public final static String conjoin(final String s1, final String s2, final String conjunction) {
        if (s1 == null) throw new IllegalArgumentException();
        if (s2 == null) throw new IllegalArgumentException();
        int cmp = s1.compareToIgnoreCase(s2);
        if (cmp == 0) cmp = s1.compareTo(s2);
        if (cmp < 0) {
            return s1 + conjunction + s2;
        } else {
            return s2 + conjunction + s1;
        }
    }
}
