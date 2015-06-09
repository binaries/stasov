package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.util.StasovStrings;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by etucker on 4/27/15.
 */
public class Parser {

    protected static enum Token { AND, OR, NOT, LP, RP, WS, EQ }

    private static class TokenMatcher {
        private LinkedHashMap<Pattern,Token> patterns = new LinkedHashMap<Pattern, Token>();

        public TokenMatcher() {
            addPattern("[aA][nN][dD]", Token.AND);
            addPattern("[oO][rR]", Token.OR);
            addPattern("[nN][oO][tT]", Token.NOT);
            addPattern(Pattern.quote("("), Token.LP);
            addPattern(Pattern.quote(")"), Token.RP);
            addPattern("[" + StasovStrings.WHITESPACE_CHARS + "]", Token.WS);
            addPattern("[a-zA-Z]+[a-zA-Z0-9]*" + "[" + StasovStrings.WHITESPACE_CHARS + "]" + "[a-zA-Z0-9]+", Token.EQ);
        }

        private void addPattern(final String regex, final Token token) {
            Pattern pattern = Pattern.compile("^" + regex);
            patterns.put(pattern, token);
        }

        public Match match(final String input, final int startIndex) {
            for (final Map.Entry<Pattern,Token> pet : patterns.entrySet()) {
                final Matcher matcher = pet.getKey().matcher(input); // TODO: instantiating new matchers on every call is expensive
                if (matcher.find(startIndex)) {
                    final String string = matcher.group();
                    return new Match(string, pet.getValue(), matcher.end());
                }
            }
            return null;
        }
    }

    private static final TokenMatcher TOKEN_MATCHER = new TokenMatcher();

    static class Match {
        private String string;
        private Token token;
        private int lastIndex;
        public Match() {
            clear();
        }

        public Match(final String string, final Token token, final int lastIndex) {
            this.string = string;
            this.token = token;
            this.lastIndex = lastIndex;
        }

        public Match(final String string, final Token token) {
            this(string, token, -1);
        }

        public void clear() {
            token = null;
            lastIndex = -1;
        }

        public void set(final String string, final Token token, final int lastIndex) {
            this.string = string;
            this.token = token;
            this.lastIndex = lastIndex;
        }

        public void setLastIndex(int lastIndex) {
            this.lastIndex = lastIndex;
        }

        public int getLastIndex() {
            return lastIndex;
        }

        public String getString() {
            return string;
        }

        public Token getToken() {
            return token;
        }

        public boolean isMatch() {
            assert( token == null && lastIndex == -1 || token != null && lastIndex > 0 );
            return token != null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Match match = (Match) o;

            if (lastIndex != match.lastIndex) return false;
            return token == match.token;
        }

        @Override
        public int hashCode() {
            int result = token != null ? token.hashCode() : 0;
            result = 31 * result + lastIndex;
            return result;
        }
    }

    static class Node {
        private final Node parent;
        private Set<Node> children = new TreeSet<Node>();

        public Node(Node parent) {
            this.parent = parent;
        }

        public void addChild(Node child) {
            children.add(child);
        }

        public Node getParent() {
            return parent;
        }

        public boolean isEmpty() {
            return children.isEmpty();
        }
    }

    static class AndNode extends Node {
        public AndNode(Node parent) {
            super(parent);
        }
    }

    static class OrNode extends Node {
        public OrNode(Node parent) {
            super(parent);
        }
    }

    static class EqNode extends Node {
        private String varName = null;
        private String value = null;
        public EqNode(Node parent) {
            super(parent);
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getVarName() {
            return varName;
        }

        public void setVarName(String varName) {
            this.varName = varName;
        }
    }

    public void parse(final String input) {
        if (input == null) throw new IllegalArgumentException("input was null");
        if (input.isEmpty()) throw new IllegalArgumentException("input was empty");
        final Node root = new Node(null);
        parse(input, 0, root);
    }

    private int parse(final String input, int startIndex, final Node parent) {
      /*  if (input == null) throw new IllegalArgumentException("input was null");
        if (input.isEmpty()) throw new IllegalArgumentException("input was empty string");
        if (startIndex < 0) throw new IllegalArgumentException("startIndex was less than 0");
        if (parent == null) throw new IllegalArgumentException("node was null");
        Match m = null;
        int i = startIndex;
        Node current = null;
        Node previous = null;
        boolean insideAnd = false;
        while ( i < input.length() ) {
            i = sws(input, i);
            m = TOKEN_MATCHER.match(input, i);
            switch (m.getToken()) {
                case WS: {
                    i = m.getLastIndex();
                    break;
                }
                case AND: {
                    if (insideAnd) {

                    }
                    if (parent instanceof AndNode) {
                        parent.addChild(previous); // TODO: How is previous set?
                    } else {
                        final AndNode andNode = new AndNode(parent);
                        if (previous != null) {
                            andNode.addChild(previous);
                            parse(input, m.getLastIndex(), andNode);
                        }
                    }
                    break;
                }
                case OR: {
                    if (node instanceof AndNode) {
                        final Node parent = node.getParent();
                        if (parent == null) throw new IllegalStateException();
                        final OrNode orNode = new OrNode(parent);
                        orNode.addChild(node);
                    } else if (node instanceof OrNode) {
                        node.addChild(previous);
                    }
                    break;
                }
                case NOT: {
                    break;
                }
                case EQ: {
                    EqNode eqNode = new EqNode(node);
                    node.addChild(eqNode);
                    break;
                }
                case LP: {
                    final Node child = new Node(node);
                    //final String substring = input.substring(i, input.length());
                    i = parse(input, i, child, false);
                    if (!child.isEmpty())
                        node.addChild(child);
                    break;
                }
                case RP: {
                    Node parent = node.getParent();
                    return parent;
                }
            } // end switch
            previous = current;
        }*/
        return -1;
    }

    private Match match(final String inputUpperCase, final int index, final Token token, final Match match) {
     /*   final String tokenName = token.name();
        final int tokenNameLength = tokenName.length();
        if (inputUpperCase.startsWith(tokenName)) {
            match.set(token, index + tokenNameLength);
        }
        */ throw new UnsupportedOperationException();
    }

    private Match match(final String input, final int index, final Match match) {
        final String in = input.toUpperCase();
        Match retVal = null;
        for (final Token token : Token.values())
            if ((retVal = match(in, index, token, match)) != null) return retVal;
        return null;
    }

    /**
     * Skip whitespace
     *
     * @param input
     * @param index
     * @return
     */
    private int sws(final String input, final int index) {
        final int n = input.length();
        int i = index;
        while (i < n) {
            final char c = input.charAt(i);
            if (c==' ' || c=='\n' || c=='\r' || c=='\t')
                i++;
            else
                return i;
        }
        return i;
    }

    public static void main(String args[]) {

    }

}
