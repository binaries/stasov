package com.pocketmath.stasov.engine;

import com.google.common.collect.ImmutableMap;
import com.pocketmath.stasov.attributes.Order;
import com.pocketmath.stasov.util.StasovArrays;
import com.pocketmath.stasov.util.Weighted;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
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

        final Set<Weighted<String>> templates = new HashSet<Weighted<String>>(Collections.unmodifiableList(Arrays.asList(new Template[]{new Template("${k1}=\"${v1}\"", 1d)})));
        final Map<Long,String> specifications = new HashMap<Long,String>();
        final Map<String,String> opportunityAttributes = new HashMap<String, String>();

        generateRandomParameters(1, 1, 1, 1d, Order.UNDEFINED, Order.ASCENDING, templates, specifications, opportunityAttributes);

        testIndexAndQuery(specifications, opportunityAttributes, null);
    }

    @Test
    public void test2() throws IndexingException {

        final Set<Weighted<String>> templates = new HashSet<Weighted<String>>(Collections.unmodifiableList(Arrays.asList(new Template[]{new Template("${k1}=\"${v1}\"", 1d)})));
        final Map<Long,String> specifications = new HashMap<Long,String>();
        final Map<String,String> opportunityAttributes = new HashMap<String, String>();

        for (int i = 1; i <= 100; i++) {
            generateRandomParameters(i, 100, 100, 1d, Order.UNDEFINED, Order.RANDOM, templates, specifications, opportunityAttributes);
        }

        final LongSortedSet results = testIndexAndQuery(specifications, opportunityAttributes, null);

        System.out.println("Results: " + results);
    }

    @Test
    public void test3() throws IndexingException, InterruptedException {

        final Set<Weighted<String>> templates = new HashSet<Weighted<String>>(Collections.unmodifiableList(Arrays.asList(new Template[]{new Template("${k1}=\"${v1}\"", 1d)})));
        final Map<Long,String> specifications = new HashMap<Long,String>();
        final Map<String,String> opportunityAttributes = new HashMap<String, String>();

        for (int i = 1; i <= 1000*50; i++) {
            if (i % 100 == 0) System.out.println("i=" + i);
            generateRandomParameters(i, 100, 100, 1d, Order.UNDEFINED, Order.RANDOM, templates, specifications, opportunityAttributes);
        }

        final Engine engine = new Engine();
        index(engine, specifications);

        //System.gc();
        //Thread.sleep(5000); // give garbage collection some time to operate

        long startTime = System.currentTimeMillis();
        int i = 0;
        for (; i < 1000; i++) {
            final LongSortedSet results = query(engine, opportunityAttributes, null);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Avg query time: " + (endTime - startTime) / i + "ms");
    }

    @Test
    public void test4() throws IndexingException, InterruptedException {

        final Set<Weighted<String>> templates = new HashSet<Weighted<String>>(Collections.unmodifiableList(Arrays.asList(new Template[]{
                //new Template("${k1}=\"${v1}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\"", 1d),
                new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" AND ${k4}=\"${v4}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" AND ${k4}=\"${v4}\" AND ${k5}=\"${v5}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
        })));
        final Map<Long,String> specifications = new HashMap<Long,String>();
        final Map<String,String> opportunityAttributes = new HashMap<String, String>();

        for (int i = 1; i <= 10000; i++) {
            if (i % 100 == 0) System.out.println("i=" + i);
            generateRandomParameters(i, 100, 100, 1d, Order.UNDEFINED, Order.RANDOM, templates, specifications, opportunityAttributes);
        }

        final Engine engine = new Engine();
        index(engine, specifications);

        System.out.println(engine.prettyPrint());

        //System.gc();
        //Thread.sleep(5000); // give garbage collection some time to operate

        long startTime = System.currentTimeMillis();
        int i = 0;
        for (; i < 100000; i++) {
            final LongSortedSet results = query(engine, opportunityAttributes, null);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Avg query time: " + (endTime - startTime) / i + "ms");
    }

    @Test
    public void test5() throws IndexingException, InterruptedException {

        final Set<Weighted<String>> templates = new HashSet<Weighted<String>>(Collections.unmodifiableList(Arrays.asList(new Template[]{
                //new Template("${k1}=\"${v1}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\"", 1d),
                new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" OR ${k4}=\"${v4}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" AND ${k4}=\"${v4}\" AND ${k5}=\"${v5}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
        })));
        final Map<Long,String> specifications = new HashMap<Long,String>();
        final Map<String,String> opportunityAttributes = new HashMap<String, String>();

        for (int i = 1; i <= 10000; i++) {
            if (i % 100 == 0) System.out.println("i=" + i);
            generateRandomParameters(i, 100, 100, 1d, Order.UNDEFINED, Order.RANDOM, templates, specifications, opportunityAttributes);
        }

        final Engine engine = new Engine();
        index(engine, specifications);

        System.out.println(engine.prettyPrint());

        //System.gc();
        //Thread.sleep(5000); // give garbage collection some time to operate

        long startTime = System.currentTimeMillis();
        int i = 0;
        for (; i < 100000; i++) {
            final LongSortedSet results = query(engine, opportunityAttributes, null);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Avg query time: " + (endTime - startTime) / i + "ms");
    }

}
