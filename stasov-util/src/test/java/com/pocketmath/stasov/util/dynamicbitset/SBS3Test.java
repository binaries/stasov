package com.pocketmath.stasov.util.dynamicbitset;

import it.unimi.dsi.fastutil.longs.LongIterator;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

/**
 * Created by etucker on 4/20/16.
 */
public class SBS3Test {

    @Test
    public void test1() {
        final SBS3 o = new SBS3();
        o.print(System.out);
    }

    @Test
    public void test2() {
        final SBS3 o = new SBS3();
        o.set(0);
    }

    @Test
    public void test3() {
        final SBS3 o = new SBS3();
        o.set(1);
    }

    @Test
    public void test4() {
        final SBS3 o = new SBS3();
        o.set(1);
        o.set(2);
    }


    @Test
    public void test5() {
        final SBS3 o = new SBS3();
        o.set(1);
        o.set(2);
        o.set(8);
    }

    @Test
    public void test6() {
        final SBS3 o = new SBS3();
        o.set(1);
        o.set(2);
        o.set(64);
    }

    @Test
    public void test6b() {
        final SBS3 o = new SBS3();
        o.set(1);
        o.set(2);
        o.set(63);
    }

    @Test
    public void test7() {
        final SBS3 o = new SBS3();
        o.set(1);
        o.set(2);
        o.set(1024);
    }

    @Test
    public void test8() {
        final SBS3 o = new SBS3();
        o.set(1);
        o.set(2);
        o.set(1024*1024);
    }

    @Test
    public void test9() {
        final SBS3 o = new SBS3();
        o.set(o.maxPosition());
    }

    @Test
    public void test10() {
        final SBS3 o = new SBS3();
        o.set(1);
        o.set(o.maxPosition());
    }

    @Test
    public void test11() {
        final SBS3 o = new SBS3();
        o.set(0);
        o.set(1);
        o.set(o.maxPosition());
    }

    @Test
    public void test12() throws Exception {
        final SBS3 o = new SBS3();
        for (int i = 0; i < 1024; i++) {
            try {
                o.set(i);
            } catch (Exception e) {
                throw new Exception("i:" + i, e);
            }
        }
    }

    @Test(enabled=false, timeOut=60000*3)
    public void test13() {
        final SBS3 o = new SBS3();
        final int n = 1024*1024;
        for (int i = 0; i < n; i++) {
            if (i % 65536 == 0) System.out.println("i="+i);
            o.set(i);
        }
    }

    @Test(enabled=false)
    public void test14() {
        final SBS3 o = new SBS3(1, 16384);
        final int n = 1024*1024;
        for (int i = 0; i < n; i++) {
            if (i % 65536 == 0) System.out.println("i="+i);
            if (i*8 > o.maxPosition()) throw new IllegalStateException("error in test code -- position went too high");
            o.set(i*8);
        }
    }

    @Test(enabled=false,timeOut=(int)(60000*1.5))
    public void test15() {
        final SBS3 o = new SBS3(1, 16384);
        final int n = 1024*1024;
        for (int i = 0; i < n; i++) {
            if (i % 65536 == 0) System.out.println("i="+i);
            if (i*8 > o.maxPosition()) throw new IllegalStateException("error in test code -- position went too high");
            o.set(i*8);
            Assert.assertTrue(o.get(i*8));
            Assert.assertFalse(o.get(i*8+1));
        }
    }

    @Test
    public void test100() {
        final SBS3 o = new SBS3();
        o.set(16);
        Assert.assertTrue(o.get(16));
        Assert.assertFalse(o.get(2));
    }

    @Test
    public void test200() {
        final SBS3 o = new SBS3();
        o.clear(0);
    }

    @Test
    public void test201() {
        final SBS3 o = new SBS3();
        o.set(0);
        o.clear(0);
        Assert.assertFalse(o.get(0));
    }

    @Test
    public void test202() {
        final SBS3 o = new SBS3();
        o.set(1);
        o.clear(1);
        Assert.assertFalse(o.get(1));
    }

    @Test
    public void test203() {
        final SBS3 o = new SBS3();
        o.set(16);
        Assert.assertTrue(o.get(16));
        Assert.assertFalse(o.get(2));
        o.clear(16);
        Assert.assertFalse(o.get(16));
        Assert.assertFalse(o.get(2));
    }

    @Test
    public void test204() {
        final SBS3 o = new SBS3();
        o.set(16);
        Assert.assertTrue(o.get(16));
        Assert.assertFalse(o.get(2));
        o.clear(16);
        o.clear(2);
        Assert.assertFalse(o.get(16));
        Assert.assertFalse(o.get(2));
    }

    protected static enum Action {
        set, clear
    }

    protected static class PositionAction {
        private final long position;
        private final Action action;

        public PositionAction(final long position, final Action action) {
            this.position = position;
            this.action = action;
        }

        public long getPosition() {
            return position;
        }

        public Action getAction() {
            return action;
        }

        @Override
        public String toString() {
            return "PositionAction{" +
                    "position=" + position +
                    ", action=" + action +
                    '}';
        }
    }

    protected static class PositionActions {
        final ArrayList<PositionAction> arrayList = new ArrayList<PositionAction>();
        public PositionActions set(final long position) {
            arrayList.add(new PositionAction(position, Action.set));
            return this;
        }
        public PositionActions clear(final long position) {
            arrayList.add(new PositionAction(position, Action.clear));
            return this;
        }
        public PositionAction[] toArray() {
            return arrayList.toArray(new PositionAction[arrayList.size()]);
        }
        public PositionAction[][] toArrayArray() {
            return new PositionAction[][]{toArray()};
        }
        public Iterator<PositionAction> iterator() {
            return arrayList.iterator();
        }

        @Override
        public String toString() {
            return "PositionActions{" +
                    "arrayList=" + arrayList +
                    '}';
        }
    }

    abstract class AbstractPAItr implements Iterator<PositionAction> {
        int i = 0;
        final int max;

        public AbstractPAItr(final int max) {
            this.max = max;
        }

        @Override
        public boolean hasNext() {
            return i < max;
        }

        protected abstract PositionAction pa();

        @Override
        public PositionAction next() {
            final PositionAction pa = pa();
            i++;
            return pa;
        }
    }

    @DataProvider(name="dp206")
    public Object[][] dp206() {

        return new Object[][]{
                {
                        new AbstractPAItr(1024) {
                            @Override
                            public PositionAction pa() {
                                return new PositionAction(i, i % 2 == 0 ? Action.set : Action.clear);
                            }
                        }
                },
                {
                        new AbstractPAItr(16384) {
                            @Override
                            public PositionAction pa() {
                                return new PositionAction(i, i % 2 == 0 ? Action.set : Action.clear);
                            }
                        }
                },
                {
                        new AbstractPAItr(16384) {
                            final Random r = new Random();
                            @Override
                            public PositionAction pa() {
                                return new PositionAction(i, r.nextBoolean() ? Action.set : Action.clear);
                            }
                        }
                },
                {
                        new PositionActions()
                                .set(0)
                                .iterator()
                },
                {
                        new PositionActions()
                                .set(0)
                                .clear(0)
                                .iterator()
                },
                {
                        new PositionActions()
                                .set(0)
                                .clear(SBS3.maxIntegerPosition())
                                .iterator()
                }
        };

    }

    @Test(dataProvider="dp206")
    public void testPositionActionSequence(final Iterator<PositionAction> paitr) {
        final SBS3 o = new SBS3();

        final BitSet bsl = new BitSet();

        while (paitr.hasNext()) {
            final PositionAction pai = paitr.next();
            final long pos = pai.getPosition();
            if (pos > Integer.MAX_VALUE)
                throw new UnsupportedOperationException("This test case doesn't support beyond Integer.MAX_VALUE because it compares results with the java.util.BitSet");
            switch (pai.getAction()) {
                case set: {
                    bsl.set((int)pos);
                    o.set(pos);
                    break;
                }
                case clear: {
                    bsl.clear((int)pos);
                    o.clear(pos);
                    break;
                }
                default : { throw new IllegalStateException(); }
            }
        }

        for (int i = bsl.nextSetBit(0); i >= 0; i = bsl.nextSetBit(i+1)) {
            final boolean expected = bsl.get(i);
            final boolean actual = o.get(i);
            Assert.assertEquals(actual, expected);
        }

        final LongIterator itr = o.positionsIterator();
        while (itr.hasNext()) {
            final long position = itr.nextLong();
            final boolean expected = bsl.get((int)position);
            Assert.assertTrue(expected);
        }
    }

    @DataProvider(name="dp1000")
    public Object[][] dp1000() {
        return new Object[][]{
                {1024*1024,128},
                {1024,1024*1024},
                {1024*1024,1024*1024}
        };
    }

    @Test(enabled=false,dataProvider="dp1000")
    public void test1000(int n, long max) {
        final long actualMax = Math.min(max,Math.min((long)Integer.MAX_VALUE, SBS3.maxPosition()));
        final Random random = new Random();
        final SBS3 o = new SBS3();
        final BitSet bsClassic = new BitSet();
        for (int i = 0; i < n; i++) {
            if (i % 65536 == 0) System.out.println("i="+i);
            if (random.nextBoolean()) {
                final long p = Math.abs(random.nextLong()) % actualMax;
                bsClassic.set((int)p);
                o.set(p);
            } else {
                final long p = Math.abs(random.nextLong()) % actualMax;
                bsClassic.set((int)p);
                o.set(p);
                Assert.assertEquals(bsClassic.get((int)p), o.get(p));
            }
        }
    }

    @Test
    public void testIterator0() {
        final SBS3 o = new SBS3();
        final BitSet bsl = new BitSet();
        for (int i = 0; i < 1024; i++) {
            if (i % 2 == 0) {
                o.set(i);
                bsl.set(i);
            }
        }

        final TreeSet<Long> oResults = new TreeSet<Long>();
        final LongIterator oItr = o.positionsIterator();
        while (oItr.hasNext()) {
            final long position = oItr.nextLong();
            Assert.assertFalse(oResults.contains(position), "duplicate found at position: " + position);
            oResults.add(position);
        }

        for (final long position : oResults) {
            Assert.assertTrue(o.get(position));
            Assert.assertTrue(bsl.get((int)position));
        }

        for (int i = bsl.nextSetBit(0); i >= 0; i = bsl.nextSetBit(i+1)) {
            final boolean expected = bsl.get(i);
            final boolean actual = o.get(i);
            Assert.assertEquals(actual, expected);
        }

    }

    @Test
    public void testAnd1() {
        final SBS3 o1 = new SBS3();
        final SBS3 o2 = new SBS3();

        o1.set(5);
        o2.set(5);

        o1.and(o2);

        Assert.assertTrue(o1.get(5));
    }

    @Test
    public void testAnd2() {
        final SBS3 o1 = new SBS3();
        final SBS3 o2 = new SBS3();

        o1.set(5);
        o2.set(7);

        Assert.assertTrue(o1.get(5));
        Assert.assertTrue(o2.get(7));

        o1.and(o2);

        Assert.assertFalse(o1.get(5), o1.toString());
        Assert.assertFalse(o1.get(7), o1.toString());

        Assert.assertFalse(o2.get(5));
        Assert.assertTrue(o2.get(7));
    }

    @Test
    public void testOr1() {
        final SBS3 o1 = new SBS3();
        final SBS3 o2 = new SBS3();

        o1.set(5);
        o2.set(5);

        o1.or(o2);

        Assert.assertTrue(o1.get(5));
    }

    @Test
    public void testOr2() {
        final SBS3 o1 = new SBS3();
        final SBS3 o2 = new SBS3();

        o1.set(5);
        o2.set(7);

        Assert.assertTrue(o1.get(5));
        Assert.assertTrue(o2.get(7));

        o1.or(o2);

        Assert.assertTrue(o1.get(5), o1.toString());
        Assert.assertTrue(o1.get(7), o1.toString());

        Assert.assertFalse(o2.get(5));
        Assert.assertTrue(o2.get(7));
    }

    @Test
    public void testXor1() {
        final SBS3 o1 = new SBS3();
        final SBS3 o2 = new SBS3();

        o1.set(5);
        o2.set(5);

        o1.xor(o2);

        Assert.assertFalse(o1.get(5));
    }

    @Test
    public void testXor2() {
        final SBS3 o1 = new SBS3();
        final SBS3 o2 = new SBS3();

        o1.set(5);
        o2.set(7);

        Assert.assertTrue(o1.get(5));
        Assert.assertTrue(o2.get(7));

        o1.xor(o2);

        Assert.assertTrue(o1.get(5), o1.toString());
        Assert.assertTrue(o1.get(7), o1.toString());

        Assert.assertFalse(o2.get(5));
        Assert.assertTrue(o2.get(7));
    }

    @Test
    public void testNot1() {
        final SBS3 o = new SBS3();

        o.not();

        Assert.assertTrue(o.get(5));
    }

    @Test
    public void clear1() {
        final SBS3 o = new SBS3();

        o.set(5);
        o.clear(5);

        Assert.assertFalse(o.get(5));
    }

    @Test
    public void testReadOptimize1() {
        final SBS3 o = new SBS3();

        o.readOptimize();
    }

    @Test
    public void testReadOptimize2() {
        final SBS3 o = new SBS3();

        o.set(5);

        o.readOptimize();

        Assert.assertTrue(o.get(5));
        Assert.assertFalse(o.get(7));
    }

    @Test
    public void testReadOptimize3() {
        final SBS3 o = new SBS3();

        o.set(5);

        o.readOptimize();

        Assert.assertFalse(o.get(o.minIntegerPosition()));
        Assert.assertTrue(o.get(5));
        Assert.assertFalse(o.get(7));
        Assert.assertFalse(o.get(o.maxIntegerPosition()));
    }

    @Test
    public void testIterator1() {
        final SBS3 o = new SBS3();

        final Iterator<Long> itr = o.positionsIterator();
        Assert.assertFalse(itr.next() > 0);
    }

    @Test
    public void testIterator2() {
        final SBS3 o = new SBS3();

        o.set(5);

        final Iterator<Long> itr = o.positionsIterator();
        Assert.assertTrue(itr.hasNext());
        Assert.assertEquals(itr.next(), new Long(5));
        Assert.assertFalse(itr.hasNext());
        Assert.assertFalse(itr.next() >= 0);
    }

    @Test
    public void testIterator3() {
        final SBS3 o = new SBS3();

        final int n = 1024;

        for (int i = 0; i < n; i++) {
            o.set(i);
        }

        final Iterator<Long> itr = o.positionsIterator();

        for (int i = 0; i < n; i++) {
            Assert.assertTrue(itr.hasNext());
            Assert.assertEquals(itr.next(), new Long(i));
        }

        Assert.assertFalse(itr.hasNext());
        Assert.assertFalse(itr.next() >= 0);
    }

    @Test
    public void testIterator4() {
        final SBS3 o = new SBS3();

        final int n = 1024;

        for (int i = 0; i < n; i++) {
            if (i % 2 == 0)
                o.set(i);
        }

        final Iterator<Long> itr = o.positionsIterator();

        for (int i = 0; i < n / 2; i++) {
            Assert.assertEquals(itr.next(), new Long(i*2));
        }

        Assert.assertFalse(itr.hasNext());
        Assert.assertFalse(itr.next() >= 0);
    }

    @Test
    public void testIterator5() {
        final SBS3 o = new SBS3();

        final int n = 16384;

        for (int i = 0; i < n; i++) {
            if (i % 256 == 0)
                o.set(i);
        }

        final Iterator<Long> itr = o.positionsIterator();

        for (int i = 0; i < n / 256; i++) {
            Assert.assertEquals(itr.next(), new Long(i*256));
        }

        Assert.assertFalse(itr.hasNext());
        Assert.assertFalse(itr.next() >= 0);
    }

    @Test
    public void testIterator6() {
        final SBS3 o = new SBS3();

        o.set(1);
        o.set(o.maxPosition());

        final Iterator<Long> itr = o.positionsIterator();

        Assert.assertEquals(itr.next(), new Long(1));
        Assert.assertEquals(itr.next(), new Long(o.maxPosition()));

        Assert.assertFalse(itr.hasNext());
        Assert.assertFalse(itr.next() >= 0);
    }

    @Test
    public void testIterator7() {
        final SBS3 o = new SBS3();

        o.set(1);
        o.set(1032);
        o.set(o.maxPosition());

        final Iterator<Long> itr = o.positionsIterator();

        Assert.assertEquals(itr.next(), new Long(1));
        Assert.assertEquals(itr.next(), new Long(1032));
        Assert.assertEquals(itr.next(), new Long(o.maxPosition()));

        Assert.assertFalse(itr.hasNext());
        Assert.assertFalse(itr.next() >= 0);
    }

    @Test
    public void testIterator8() {
        final SBS3 o = new SBS3();

        o.set(1);
        o.set(8712381);
        o.set(o.maxPosition());

        final Iterator<Long> itr = o.positionsIterator();

        Assert.assertEquals(itr.next(), new Long(1));
        Assert.assertEquals(itr.next(), new Long(8712381));
        Assert.assertEquals(itr.next(), new Long(o.maxPosition()));

        Assert.assertFalse(itr.hasNext());
        Assert.assertFalse(itr.next() >= 0);
    }

    @Test
    public void testIterator9() {
        final SBS3 o = new SBS3();

        o.set(1);
        o.set(1024);
        o.set(o.maxPosition());

        final Iterator<Long> itr = o.positionsIterator();

        Assert.assertEquals(itr.next(), new Long(1));
        Assert.assertEquals(itr.next(), new Long(1024));
        Assert.assertEquals(itr.next(), new Long(o.maxPosition()));

        Assert.assertFalse(itr.hasNext());
        Assert.assertFalse(itr.next() >= 0);
    }

}