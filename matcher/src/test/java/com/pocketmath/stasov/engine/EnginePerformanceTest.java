package com.pocketmath.stasov.engine;

import com.google.common.collect.ImmutableMap;
import com.pocketmath.stasov.attributes.Order;
import com.pocketmath.stasov.util.StasovArrays;
import org.testng.annotations.Test;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by etucker on 8/27/15.
 */
public class EnginePerformanceTest extends EngineTestBase {

    private static long[] arrayify(Collection<Long> collection) {
        return StasovArrays.toSortedArray(collection);
    }

    /**
     * The most rudimentary test.
     *
     * @throws IndexingException
     */
    @Test
    public void test1() throws IndexingException {

        final Set<Long> expectedResults = new HashSet<Long>(Arrays.asList(new Long[]{1L}));
        final String template = "${k1}=${v1}";
        final Map<Long,String> specifications = new HashMap<Long,String>();
        final Map<String,String> opportunityAttributes = new HashMap<String, String>();

        generateRandomParameters(1, 1, 1, Order.UNDEFINED, Order.ASCENDING, template, specifications, opportunityAttributes);

        testIndexAndQuery(specifications, opportunityAttributes, null);
    }

}
