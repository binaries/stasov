package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.Order;
import com.pocketmath.stasov.util.StasovArrays;
import com.pocketmath.stasov.util.Weighted;
import org.testng.annotations.Test;

import org.testng.Assert;

import java.util.*;

/**
 * Created by etucker on 8/27/15.
 */
@Test(singleThreaded = true)
public class EnginePerformanceTest extends EngineTestBase {

    private static long[] arrayify(Collection<Long> collection) {
        return StasovArrays.toSortedArray(collection);
    }

    /**
     * The most rudimentary test.
     *
     * @throws IndexingException
     */
 /*   @Test
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

        double avgTime = (endTime - startTime) / i;
        Assert.assertTrue(avgTime < 10d);

        System.out.println("Avg query time: " + avgTime + "ms");
    }

    @Test
    public void test6() throws IndexingException, InterruptedException {

        final Set<Weighted<String>> templates = new HashSet<Weighted<String>>(Collections.unmodifiableList(Arrays.asList(new Template[]{
                //new Template("${k1}=\"${v1}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                new Template("${k1}=\"${v1}\" OR ${k2}=\"${v2}\" OR ${k3}=\"${v3}\"", 1d),
              //  new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" OR ${k4}=\"${v4}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" AND ${k4}=\"${v4}\" AND ${k5}=\"${v5}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
        })));
        final Map<Long,String> specifications = new HashMap<Long,String>();
        final Map<String,String> opportunityAttributes = new HashMap<String, String>();

        for (int i = 1; i <= 20; i++) {
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
        for (; i < 10; i++) {
            final LongSortedSet results = query(engine, opportunityAttributes, null);
            if (results != null) {
                System.out.println("opp: " + opportunityAttributes);
                System.out.println("results: " + results);
            }
        }
        long endTime = System.currentTimeMillis();

        double avgTime = (endTime - startTime) / i;
        Assert.assertTrue(avgTime < 10d);

        System.out.println("Avg query time: " + avgTime + "ms");
    }
*/

    @Test
    public void test7() throws IndexingException, InterruptedException {

        final Set<Weighted<String>> templates = new HashSet<Weighted<String>>(Collections.unmodifiableList(Arrays.asList(new Template[]{
                //new Template("${k1}=\"${v1}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                new Template("${k1}=\"${v1}\" OR ${k2}=\"${v2}\" OR ${k3}=\"${v3}\" OR ${k4}=\"${v4}\" OR ${k5}=\"${v5}\" OR ${k6}=\"${v6}\" OR ${k7}=\"${v7}\" OR ${k8}=\"${v8}\" OR ${k9}=\"${v9}\"", 1d), // OR ${k10}=\"${v10}\" OR ${k11}=\"${v11}\" OR ${k12}=\"${v12}\" OR ${k13}=\"${v13}\" OR ${k14}=\"${v14}\" OR ${k15}=\"${v15}\" OR ${k16}=\"${v16}\" OR ${k17}=\"${v17}\" OR ${k18}=\"${v18}\" OR ${k19}=\"${v19}\" OR ${k20}=\"${v20}\" OR ${k21}=\"${v21}\"", 1d),
                //  new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" OR ${k4}=\"${v4}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" AND ${k4}=\"${v4}\" AND ${k5}=\"${v5}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
        })));
        final Map<Long,String> specifications = new HashMap<Long,String>();
        final Collection<Map<String,String>> opportunities = new HashSet<Map<String,String>>();

        for (int i = 1; i <= 20; i++) {
            if (i % 100 == 0) System.out.println("i=" + i);
            generateRandomParameters(i, 100, 100, 1d, Order.RANDOM, Order.RANDOM, templates, specifications, opportunities);
        }

        final Map[] oppsArray = opportunities.toArray(new Map[opportunities.size()]);

        final EngineBase engine = new EngineBase();
        index(engine, specifications);

        System.out.println(engine.prettyPrint());

        //System.gc();
        //Thread.sleep(5000); // give garbage collection some time to operate

        final Random random = new Random();

        long startTime = System.currentTimeMillis();
        int i = 0;
        for (; i < 10; i++) {
            final Map<String,String> opp = oppsArray[random.nextInt(oppsArray.length)];
            final Object[] results = query(engine, opp, null);
            if (results != null) {
                System.out.println("opp: " + opp);
                System.out.println("results: " + results);
            }
        }
        long endTime = System.currentTimeMillis();

        double avgTime = (endTime - startTime) / (double)i;
        Assert.assertTrue(avgTime < 10d);

        System.out.println("Avg query time: " + avgTime + "ms");
    }

    @Test
    public void test8() throws IndexingException, InterruptedException {

        final Set<Weighted<String>> templates = new HashSet<Weighted<String>>(Collections.unmodifiableList(Arrays.asList(new Template[]{
                //new Template("${k1}=\"${v1}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                new Template("${k1}=\"${v1}\" OR ${k2}=\"${v2}\" OR ${k3}=\"${v3}\" OR ${k4}=\"${v4}\" OR ${k5}=\"${v5}\" OR ${k6}=\"${v6}\" OR ${k7}=\"${v7}\" OR ${k8}=\"${v8}\" OR ${k9}=\"${v9}\"", 1d), // OR ${k10}=\"${v10}\" OR ${k11}=\"${v11}\" OR ${k12}=\"${v12}\" OR ${k13}=\"${v13}\" OR ${k14}=\"${v14}\" OR ${k15}=\"${v15}\" OR ${k16}=\"${v16}\" OR ${k17}=\"${v17}\" OR ${k18}=\"${v18}\" OR ${k19}=\"${v19}\" OR ${k20}=\"${v20}\" OR ${k21}=\"${v21}\"", 1d),
                //  new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" OR ${k4}=\"${v4}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" AND ${k4}=\"${v4}\" AND ${k5}=\"${v5}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
        })));
        final Map<Long,String> specifications = new HashMap<Long,String>();
        final Collection<Map<String,String>> opportunities = new HashSet<Map<String,String>>();

        for (int i = 1; i <= 10000; i++) {
            if (i % 100 == 0) System.out.println("i=" + i);
            generateRandomParameters(i, 100, 100, 1d, Order.RANDOM, Order.RANDOM, templates, specifications, opportunities);
        }

        final Map[] oppsArray = opportunities.toArray(new Map[opportunities.size()]);

        final EngineBase engine = new EngineBase();
        index(engine, specifications);

        System.out.println(engine.prettyPrint());

        //System.gc();
        //Thread.sleep(5000); // give garbage collection some time to operate

        final Random random = new Random();

        long startTime = System.currentTimeMillis();
        int i = 0;
        for (; i < 1000; i++) {
            final Map<String,String> opp = oppsArray[random.nextInt(oppsArray.length)];
            final Object[] results = query(engine, opp, null);
            //if (results != null) {
            //    System.out.println("opp: " + opp);
            //    System.out.println("results: " + results);
            //}
        }
        long endTime = System.currentTimeMillis();

        double avgTime = (endTime - startTime) / (double)i;
        Assert.assertTrue(avgTime < 10d);

        System.out.println("Avg query time: " + avgTime + "ms");
    }

    @Test
    public void test9() throws IndexingException, InterruptedException {

        final Set<Weighted<String>> templates = new HashSet<Weighted<String>>(Collections.unmodifiableList(Arrays.asList(new Template[]{
                //new Template("${k1}=\"${v1}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" AND ${k4}=\"${v4}\" AND ${k5}=\"${v5}\" AND ${k6}=\"${v6}\" AND ${k7}=\"${v7}\" AND ${k8}=\"${v8}\" AND ${k9}=\"${v9}\"", 1d), // OR ${k10}=\"${v10}\" OR ${k11}=\"${v11}\" OR ${k12}=\"${v12}\" OR ${k13}=\"${v13}\" OR ${k14}=\"${v14}\" OR ${k15}=\"${v15}\" OR ${k16}=\"${v16}\" OR ${k17}=\"${v17}\" OR ${k18}=\"${v18}\" OR ${k19}=\"${v19}\" OR ${k20}=\"${v20}\" OR ${k21}=\"${v21}\"", 1d),
                //  new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" OR ${k4}=\"${v4}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" AND ${k4}=\"${v4}\" AND ${k5}=\"${v5}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
        })));
        final Map<Long,String> specifications = new HashMap<Long,String>();
        final Collection<Map<String,String>> opportunities = new HashSet<Map<String,String>>();

        for (int i = 1; i <= 10000; i++) {
            if (i % 100 == 0) System.out.println("i=" + i);
            generateRandomParameters(i, 100, 100, 1d, Order.RANDOM, Order.RANDOM, templates, specifications, opportunities);
        }

        final Map[] oppsArray = opportunities.toArray(new Map[opportunities.size()]);

        final EngineBase engine = new EngineBase();
        index(engine, specifications);

        System.out.println(engine.prettyPrint());

        //System.gc();
        //Thread.sleep(5000); // give garbage collection some time to operate

        final Random random = new Random();

        long startTime = System.currentTimeMillis();
        int i = 0;
        for (; i < 1000; i++) {
            final Map<String,String> opp = oppsArray[random.nextInt(oppsArray.length)];
            final Object[] results = query(engine, opp, null);
            //if (results != null) {
            //    System.out.println("opp: " + opp);
            //    System.out.println("results: " + results);
            //}
        }
        long endTime = System.currentTimeMillis();

        double avgTime = (endTime - startTime) / (double)i;
        Assert.assertTrue(avgTime < 10d);

        System.out.println("Avg query time: " + avgTime + "ms");
    }

    @Test
    public void test100() throws IndexingException {
        final Collection<Weighted<String>> templates = new HashSet<Weighted<String>>(Collections.unmodifiableList(Arrays.asList(new Template[]{
                //new Template("${k1}=\"${v1}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" AND ${k4}=\"${v4}\" AND ${k5}=\"${v5}\" AND ${k6}=\"${v6}\" AND ${k7}=\"${v7}\" AND ${k8}=\"${v8}\" AND ${k9}=\"${v9}\"", 1d), // OR ${k10}=\"${v10}\" OR ${k11}=\"${v11}\" OR ${k12}=\"${v12}\" OR ${k13}=\"${v13}\" OR ${k14}=\"${v14}\" OR ${k15}=\"${v15}\" OR ${k16}=\"${v16}\" OR ${k17}=\"${v17}\" OR ${k18}=\"${v18}\" OR ${k19}=\"${v19}\" OR ${k20}=\"${v20}\" OR ${k21}=\"${v21}\"", 1d),
                //  new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" OR ${k4}=\"${v4}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" AND ${k4}=\"${v4}\" AND ${k5}=\"${v5}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
                //new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\"", 1d),
        })));
        final TestData testData = buildTest(templates, 100, 10);

        final TestResult result = randomQueries(testData, 10000L);

        System.out.println("Result test100: " + result);

        Assert.assertTrue(result.averageTime() < 10);
        Assert.assertTrue(result.getMaxTime() < 20);
    }

    @Test
    public void test101() throws IndexingException {
        final Collection<Weighted<String>> templates = new HashSet<Weighted<String>>(Collections.unmodifiableList(Arrays.asList(new Template[]{
                new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" AND ${k4}=\"${v4}\" AND ${k5}=\"${v5}\" AND ${k6}=\"${v6}\" AND ${k7}=\"${v7}\" AND ${k8}=\"${v8}\" AND ${k9}=\"${v9}\"", 1d), // OR ${k10}=\"${v10}\" OR ${k11}=\"${v11}\" OR ${k12}=\"${v12}\" OR ${k13}=\"${v13}\" OR ${k14}=\"${v14}\" OR ${k15}=\"${v15}\" OR ${k16}=\"${v16}\" OR ${k17}=\"${v17}\" OR ${k18}=\"${v18}\" OR ${k19}=\"${v19}\" OR ${k20}=\"${v20}\" OR ${k21}=\"${v21}\"", 1d),
        })));
        final TestData testData = buildTest(templates, 10000, 10000);

        final TestResult result = randomQueries(testData, 10000L);

        System.out.println("Result test101: " + result);

        Assert.assertTrue(result.averageTime() < 10);
        Assert.assertTrue(result.getMaxTime() < 20);
    }

    @Test
    public void test102() throws IndexingException {
        final Collection<Weighted<String>> templates = new HashSet<Weighted<String>>(Collections.unmodifiableList(Arrays.asList(new Template[]{
                new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" AND ${k4}=\"${v4}\" AND ${k5}=\"${v5}\" AND ${k6}=\"${v6}\" AND ${k7}=\"${v7}\" AND ${k8}=\"${v8}\" AND ${k9}=\"${v9}\"", 1d), // OR ${k10}=\"${v10}\" OR ${k11}=\"${v11}\" OR ${k12}=\"${v12}\" OR ${k13}=\"${v13}\" OR ${k14}=\"${v14}\" OR ${k15}=\"${v15}\" OR ${k16}=\"${v16}\" OR ${k17}=\"${v17}\" OR ${k18}=\"${v18}\" OR ${k19}=\"${v19}\" OR ${k20}=\"${v20}\" OR ${k21}=\"${v21}\"", 1d),
        })));
        final TestData testData = buildTest(templates, 20000, 10000);

        final TestResult result = randomQueries(testData, 10000L);

        System.out.println("Result test102: " + result);

        Assert.assertTrue(result.averageTime() < 10);
        Assert.assertTrue(result.getMaxTime() < 20);
    }

    @Test
    public void test103() throws IndexingException {
        final Collection<Weighted<String>> templates = new HashSet<Weighted<String>>(Collections.unmodifiableList(Arrays.asList(new Template[]{
                new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" AND ${k4}=\"${v4}\" AND ${k5}=\"${v5}\" AND ${k6}=\"${v6}\" AND ${k7}=\"${v7}\" AND ${k8}=\"${v8}\" AND ${k9}=\"${v9}\"", 1d), // OR ${k10}=\"${v10}\" OR ${k11}=\"${v11}\" OR ${k12}=\"${v12}\" OR ${k13}=\"${v13}\" OR ${k14}=\"${v14}\" OR ${k15}=\"${v15}\" OR ${k16}=\"${v16}\" OR ${k17}=\"${v17}\" OR ${k18}=\"${v18}\" OR ${k19}=\"${v19}\" OR ${k20}=\"${v20}\" OR ${k21}=\"${v21}\"", 1d),
        })));
        final TestData testData = buildTest(templates, 20000, 1000000);

        final TestResult result = randomQueries(testData, 1000000L);

        System.out.println("Result test103: " + result);

        Assert.assertTrue(result.averageTime() < 10);
        Assert.assertTrue(result.getMaxTime() < 20);
    }

    @Test(enabled = false)
    public void test104() throws IndexingException {
        final Collection<Weighted<String>> templates = new HashSet<Weighted<String>>(Collections.unmodifiableList(Arrays.asList(new Template[]{
                new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" AND ${k4}=\"${v4}\" AND ${k5}=\"${v5}\" AND ${k6}=\"${v6}\" AND ${k7}=\"${v7}\" AND ${k8}=\"${v8}\" AND ${k9}=\"${v9}\"", 1d), // OR ${k10}=\"${v10}\" OR ${k11}=\"${v11}\" OR ${k12}=\"${v12}\" OR ${k13}=\"${v13}\" OR ${k14}=\"${v14}\" OR ${k15}=\"${v15}\" OR ${k16}=\"${v16}\" OR ${k17}=\"${v17}\" OR ${k18}=\"${v18}\" OR ${k19}=\"${v19}\" OR ${k20}=\"${v20}\" OR ${k21}=\"${v21}\"", 1d),
        })));
        final TestData testData = buildTest(templates, 50000, 1000000);

        final TestResult result = randomQueries(testData, 1000000L);

        System.out.println("Result test104: " + result);

        Assert.assertTrue(result.averageTime() < 10);
        Assert.assertTrue(result.getMaxTime() < 20);
    }

    @Test(enabled = false)
    public void test105() throws IndexingException {
        final Collection<Weighted<String>> templates = new HashSet<Weighted<String>>(Collections.unmodifiableList(Arrays.asList(new Template[]{
                new Template("${k1}=\"${v1}\" AND ${k2}=\"${v2}\" AND ${k3}=\"${v3}\" AND ${k4}=\"${v4}\" AND ${k5}=\"${v5}\" AND ${k6}=\"${v6}\" AND ${k7}=\"${v7}\" AND ${k8}=\"${v8}\" AND ${k9}=\"${v9}\"", 1d), // OR ${k10}=\"${v10}\" OR ${k11}=\"${v11}\" OR ${k12}=\"${v12}\" OR ${k13}=\"${v13}\" OR ${k14}=\"${v14}\" OR ${k15}=\"${v15}\" OR ${k16}=\"${v16}\" OR ${k17}=\"${v17}\" OR ${k18}=\"${v18}\" OR ${k19}=\"${v19}\" OR ${k20}=\"${v20}\" OR ${k21}=\"${v21}\"", 1d),
        })));
        final TestData testData = buildTest(templates, 100000, 1000000);

        final TestResult result = randomQueries(testData, 1000000L);

        System.out.println("Result test105: " + result);

        Assert.assertTrue(result.averageTime() < 10);
        Assert.assertTrue(result.getMaxTime() < 20);
    }

    @Test(enabled = false)
    public void test200() throws IndexingException {
        final Collection<Weighted<String>> templates = new HashSet<Weighted<String>>(Collections.unmodifiableList(Arrays.asList(new Template[]{
                new Template("${k1}=\"${v1}\" OR ${k2}=\"${v2}\" OR ${k3}=\"${v3}\" OR ${k4}=\"${v4}\" OR ${k5}=\"${v5}\" OR ${k6}=\"${v6}\" AND ${k7}=\"${v7}\" AND ${k8}=\"${v8}\" AND ${k9}=\"${v9}\"", 1d), // OR ${k10}=\"${v10}\" OR ${k11}=\"${v11}\" OR ${k12}=\"${v12}\" OR ${k13}=\"${v13}\" OR ${k14}=\"${v14}\" OR ${k15}=\"${v15}\" OR ${k16}=\"${v16}\" OR ${k17}=\"${v17}\" OR ${k18}=\"${v18}\" OR ${k19}=\"${v19}\" OR ${k20}=\"${v20}\" OR ${k21}=\"${v21}\"", 1d),
        })));
        final TestData testData = buildTest(templates, 100000, 1000000);

        final TestResult result = randomQueries(testData, 1000000L);

        System.out.println("Result test105: " + result);

        Assert.assertTrue(result.averageTime() < 10);
        Assert.assertTrue(result.getMaxTime() < 20);
    }

}
