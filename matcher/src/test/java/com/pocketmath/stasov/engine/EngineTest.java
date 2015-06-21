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
            System.out.println(pmtl);
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
    public void test3() throws IndexingException {
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

}
