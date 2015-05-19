package com.pocketmath.stasov.util;

/**
 * Created by etucker on 4/12/15.
 */
public class StasovStrings {

    public final static String conjoin(final String s1, final String s2, final String conjunction) {
        if (s1 == null) throw new IllegalArgumentException("input 1 was null");
        if (s2 == null) throw new IllegalArgumentException("input 2 was null");
        int cmp = s1.compareToIgnoreCase(s2);
        if (cmp == 0) cmp = s1.compareTo(s2);
        if (cmp < 0) {
            return s1 + conjunction + s2;
        } else {
            return s2 + conjunction + s1;
        }
    }

    public static final String WHITESPACE_CHARS =  ""       /* dummy empty string for homogeneity */
            + "\\u0009" // CHARACTER TABULATION
            + "\\u000A" // LINE FEED (LF)
            + "\\u000B" // LINE TABULATION
            + "\\u000C" // FORM FEED (FF)
            + "\\u000D" // CARRIAGE RETURN (CR)
            + "\\u0020" // SPACE
            + "\\u0085" // NEXT LINE (NEL)
            + "\\u00A0" // NO-BREAK SPACE
            + "\\u1680" // OGHAM SPACE MARK
            + "\\u180E" // MONGOLIAN VOWEL SEPARATOR
            + "\\u2000" // EN QUAD
            + "\\u2001" // EM QUAD
            + "\\u2002" // EN SPACE
            + "\\u2003" // EM SPACE
            + "\\u2004" // THREE-PER-EM SPACE
            + "\\u2005" // FOUR-PER-EM SPACE
            + "\\u2006" // SIX-PER-EM SPACE
            + "\\u2007" // FIGURE SPACE
            + "\\u2008" // PUNCTUATION SPACE
            + "\\u2009" // THIN SPACE
            + "\\u200A" // HAIR SPACE
            + "\\u2028" // LINE SEPARATOR
            + "\\u2029" // PARAGRAPH SEPARATOR
            + "\\u202F" // NARROW NO-BREAK SPACE
            + "\\u205F" // MEDIUM MATHEMATICAL SPACE
            + "\\u3000" // IDEOGRAPHIC SPACE
            ;

}
