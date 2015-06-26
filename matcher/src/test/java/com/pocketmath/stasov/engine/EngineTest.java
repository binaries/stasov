package com.pocketmath.stasov.engine;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.pocketmath.stasov.OpportunityData;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import org.testng.Assert;
import org.testng.Assert.*;
import org.testng.annotations.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by etucker on 6/9/15.
 */
public class EngineTest {

    @Test
    public void simpleIndexTest1() throws IndexingException {
        Engine engine = new Engine();

        String spec = "DeviceType in (\"iPhone\")";
        long id = 1;
        engine.index(spec, id);
    }

    /**
     *
     * @param specifications The PTML specification.
     * @param opportunityAttributes The attributes from a simulated impression opportunity.
     * @param expectedResults The expected IO ids.
     * @throws IndexingException
     */
    private void testIndexAndQuery(
            final Map<Long,String> specifications,
            final Map<String,String> opportunityAttributes,
            final long[] expectedResults) throws IndexingException {

        System.out.println("specs:  " + specifications.toString());
        System.out.println("opp:    " + opportunityAttributes.toString());
        System.out.println("expect: " + Arrays.toString(expectedResults));

        final Engine engine = new Engine();

        for (final Map.Entry<Long, String> spec : specifications.entrySet()) {
            final long id = spec.getKey();
            final String pmtl = spec.getValue();
            assert(pmtl != null);
            engine.index(pmtl, id);
        }

        final MapOpportunityData opp = new MapOpportunityData(engine.attrSvc);

        for (final Map.Entry<String, String> attribute : opportunityAttributes.entrySet()) {
            final String name = attribute.getKey();
            final String value = attribute.getValue();
            assert(name != null);
            assert(value != null);
            opp.put(name, value);
        }

        System.out.println("oppObj: " + opp);

        System.out.println("engine: " + engine.prettyPrint());

        final LongSortedSet results = engine.query(opp);

        if (expectedResults.length < 1) {
            Assert.assertEquals(results, null);
        } else {
            Assert.assertNotEquals(results, null);
        }

        if (results == null) {
            Assert.assertTrue(expectedResults == null || expectedResults.length == 0);
            return;
        }

        { // assert that each result exists in the expected results
            long[] sortedExpectedResults = expectedResults.clone();
            Arrays.sort(sortedExpectedResults);
            for (final Long result : results) {
                final int found = Arrays.binarySearch(sortedExpectedResults, result.longValue());
                Assert.assertTrue(found > -1);
            }
        }

        { // assert that each expected result exists in the actual results
            for (final long expectedResult : expectedResults) {
                Assert.assertTrue(results.contains(expectedResult));
            }
        }

        Assert.assertTrue(results.size() == expectedResults.length);

    }

    @Test
    public void test1_in() throws IndexingException {
        testIndexAndQuery(
                ImmutableMap.<Long,String>of(
                        1L, "devicetype in (\"iphone\")"
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone"
                ),
                new long[]{ 1L }
        );
    }

    @Test
    public void test2_in() throws IndexingException {
        testIndexAndQuery(
                ImmutableMap.<Long,String>of(
                        1L, "devicetype in (\"iphone\")"
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin"
                ),
                new long[]{ 1L }
        );
    }

    @Test
    public void test3_in() throws IndexingException {
        testIndexAndQuery(
                ImmutableMap.<Long,String>of(
                        1L, "devicetype in (\"iphone\") and city in (\"austin\")"
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin"
                ),
                new long[]{ 1L }
        );
    }

    @Test
    public void test4_in() throws IndexingException {
        testIndexAndQuery(
                ImmutableMap.<Long,String>of(
                        1L, "devicetype in (\"android\")"
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin"
                ),
                new long[]{}
        );
    }

    @Test
    public void test5_eq() throws IndexingException {
        testIndexAndQuery(
                ImmutableMap.<Long,String>of(
                        1L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin"
                ),
                new long[]{1L}
        );
    }

    @Test
    public void test6_eq() throws IndexingException {
        testIndexAndQuery(
                ImmutableMap.<Long,String>of(
                        1L, "devicetype = \"android\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin"
                ),
                new long[]{}
        );
    }

    @Test
    public void test100_eq() throws IndexingException {
        testIndexAndQuery(
                ImmutableMap.<Long,String>of(
                        1L, "devicetype = \"android\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin"
                ),
                new long[]{2L}
        );
    }

    @Test
    public void test101_eq() throws IndexingException {
        testIndexAndQuery(
                ImmutableMap.<Long,String>of(
                        1L, "devicetype = \"android\" AND city = \"london\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin"
                ),
                new long[]{2L}
        );
    }

    @Test
    public void test102_eq() throws IndexingException {
        testIndexAndQuery(
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new long[]{1,2}
        );
    }

    @Test
    public void test103_eq() throws IndexingException {
        testIndexAndQuery(
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone"
                ),
                new long[]{2}
        );
    }

    @Test
    public void test104_eq() throws IndexingException {
        testIndexAndQuery(
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new long[]{1}
        );
    }

    @Test
    public void test200_eq() throws IndexingException {
        testIndexAndQuery(
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" AND NOT devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new long[]{1}
        );
    }

    @Test
    public void test201_eq() throws IndexingException {
        testIndexAndQuery(
                ImmutableMap.<Long,String>of(
                        1L, "(NOT devicetype = \"android\" AND devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new long[]{}
        );
    }

    @Test
    public void test202_eq() throws IndexingException {
        testIndexAndQuery(
                ImmutableMap.<Long,String>of(
                        1L, "NOT (devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new long[]{}
        );
    }


    @Test
         public void test203_eq() throws IndexingException {
        testIndexAndQuery(
                ImmutableMap.<Long,String>of(
                        1L, "NOT (NOT devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new long[]{1}
        );
    }

    @Test
    public void test204_eq() throws IndexingException {
        testIndexAndQuery(
                ImmutableMap.<Long,String>of(
                        1L, "NOT (devicetype = \"android\" OR NOT devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new long[]{}
        );
    }

}
