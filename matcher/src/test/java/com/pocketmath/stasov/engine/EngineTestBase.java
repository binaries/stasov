package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.attributes.Order;
import com.pocketmath.stasov.util.StasovArrays;
import com.pocketmath.stasov.util.Weighted;

import it.unimi.dsi.fastutil.longs.LongSortedSet;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.IllegalClassException;
import org.testng.Assert;

import java.io.PrintWriter;
import java.util.*;

/**
 * Created by etucker on 8/27/15.
 */
public class EngineTestBase {

    static class Template extends Weighted<String> {
        private final String text;

        public Template(final String text, final double weight) {
            super(weight);
            if (text == null) throw new IllegalArgumentException();
            this.text = text;
        }

        public String getValue() {
            return text;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Template template = (Template) o;
            return Objects.equals(getValue(), template.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getValue());
        }

        @Override
        public String toString() {
            return "Template{" +
                    "text='" + text + '\'' +
                    ", weight=" + getWeight() +
                    '}';
        }
    }

    static void index(
            final Engine engine,
            Map<Long,String> specifications) throws IndexingException {

        for (final Map.Entry<Long, String> spec : specifications.entrySet()) {
            final long id = spec.getKey();
            final String pmtl = spec.getValue();
            //System.out.println("PMTL: " + pmtl);
            assert(pmtl != null);
            engine.index(pmtl, id);
        }
    }

    static LongSortedSet query(
            final Engine engine,
            Map<String,String> opportunityAttributes,
            final long[] expectedResults) {


        final MapOpportunityData opp = new MapOpportunityData(engine.attrSvc);

        for (final Map.Entry<String, String> attribute : opportunityAttributes.entrySet()) {
            final String name = attribute.getKey();
            final String value = attribute.getValue();
            assert(name != null);
            assert(value != null);
            opp.put(name, value);
        }

        //System.out.println("oppObj: " + opp);

        //System.out.println("engine: " + engine.prettyPrint());

        final LongSortedSet results = engine.query(opp);

        if (expectedResults != null) {

            if (expectedResults.length < 1) {
                Assert.assertNull(results);
            } else {
                Assert.assertNotNull(results);
            }

            if (results == null) {
                Assert.assertTrue(expectedResults == null || expectedResults.length == 0);
                return null;
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

        return results;
    }


    /**
     *
     * @param specifications The PMTL specification.
     * @param opportunityAttributes The attributes from a simulated impression opportunity.
     * @param expectedResults The expected IO ids.
     * @throws IndexingException
     */
    private static LongSortedSet testIndexAndQuery(
            final Engine engine,
            final Map<Long,String> specifications,
            final Map<String,String> opportunityAttributes,
            final long[] expectedResults) throws IndexingException {

        System.out.println("specs:  " + specifications.toString());
        System.out.println("opp:    " + opportunityAttributes.toString());
        System.out.println("expect: " + Arrays.toString(expectedResults));

        index(engine, specifications);

        System.out.println("Engine: " + engine.prettyPrint());

        return query(engine, opportunityAttributes, expectedResults);
    }

    private static String fill(
            final Engine engine,
            final String template,
            final Order attributeTypesOrder,
            final Order attributeValuesOrder,
            final Collection<Map<String,String>> opportunities) {

        if (engine == null) throw new IllegalArgumentException();
        if (template == null) throw new IllegalArgumentException();
        if (template.isEmpty()) throw new IllegalArgumentException();
        if (attributeTypesOrder == null) throw new IllegalArgumentException();
        if (attributeValuesOrder == null) throw new IllegalArgumentException();
        if (opportunities == null) throw new IllegalArgumentException();

        String spec = new String(template); // copy

        final AttrSvcBase attrSvc = engine.attrSvc;

        final long[] attrTypeIds = attrSvc.getAttrTypeIds().clone();
        switch (attributeTypesOrder) {
            case UNDEFINED: { break; }
            case RANDOM: { StasovArrays.randomizeOrder(attrTypeIds); break; }
            case ASCENDING: { Arrays.sort(attrTypeIds); break; }
            case DESCENDING: {
                Arrays.sort(attrTypeIds);
                ArrayUtils.reverse(attrTypeIds);
                break;
            }
            default: throw new IllegalStateException();
        }


        int seq = 1;

        final Map<String,String> opp = new HashMap<String, String>();

        for (final long attrTypeId : attrTypeIds) {
            final Iterable<String> attrValues;
            try {
                attrValues = attrSvc.sampleValues(attrTypeId, attributeValuesOrder);
            } catch (UnsupportedOperationException e) {
                //Logger.getAnonymousLogger().log(Level.WARNING, "Sample values were not supported for attrTypeId={0}", attrTypeId);
                continue;
            }

            final String k = attrSvc.getAttrName(attrTypeId);

            if (attrValues == null) throw new UnsupportedOperationException("attribute sample values not found for attrTypeId: " + attrTypeId);
            for (final String attrValue : attrValues) {
                //Logger.getAnonymousLogger().log(Level.INFO, "k={0}", k);
                final String v = attrValue;

                opp.put(k, v); // TODO: this is repetitive here -- instead select this randomly only once

                //if (random.nextDouble() < matchRate) {
                spec = spec.replace("${k" + seq + "}", k);
                spec = spec.replace("${v" + seq + "}", v);
                seq++;
                //}

                //System.out.println(specifications.toString());
            }

        }

        if (!opp.isEmpty()) opportunities.add(opp);

        return spec;
    }

    static LongSortedSet testIndexAndQuery(
            final Map<Long,String> specifications,
            final Map<String,String> opportunityAttributes,
            final long[] expectedResults) throws IndexingException {

        final Engine engine = new Engine();

        return testIndexAndQuery(engine, specifications, opportunityAttributes, expectedResults);
    }

    static void generateRandomParameters(
            final long ioId,
            final int maxValuesPerAttr,
            final int maxAttrs,
            final double matchRate,
            final Order attributeTypesOrder,
            final Order attributeValuesOrder,
            final Collection<Weighted<String>> templates,
            final Map<Long,String> specifications,
            final Collection<Map<String,String>> opportunities) {

        if (ioId < 1) throw new IllegalArgumentException();
        if (maxValuesPerAttr < 1) throw new IllegalArgumentException();
        if (maxAttrs < 1) throw new IllegalArgumentException();
        if (matchRate != 1.0d) throw new UnsupportedOperationException("probabilistic matching not yet supported");
        if (attributeTypesOrder == null) throw new IllegalArgumentException();
        if (attributeValuesOrder == null) throw new IllegalArgumentException();
        if (templates == null) throw new IllegalArgumentException();
        if (templates.isEmpty()) throw new IllegalArgumentException();
        if (specifications == null) throw new IllegalArgumentException();
        if (opportunities == null) throw new IllegalArgumentException();

        final Engine engine = new Engine();

        final String template = StasovArrays.chooseRandomWeightedValue(templates);

        final String spec = fill(engine, template, attributeTypesOrder, attributeValuesOrder, opportunities);

        specifications.put(ioId, spec);

        engine.prettyPrint();

    }

    static class TestData {
        private Map<Long,String> specifications = new HashMap<Long,String>();
        private List<Map<String,String>> opportunities = new ArrayList<Map<String,String>>();
        private final Engine engine = new Engine();
        private boolean frozen = false;

        public void freeze() {
            specifications = Collections.<Long,String>unmodifiableMap(specifications);
            opportunities = Collections.<Map<String,String>>unmodifiableList(opportunities);
            frozen = true;
        }

        public Map<String,String> getOpportunityByIndex(final int index) {
            return opportunities.get(index);
        }

        public int getOpportunitiesCount() {
            return opportunities.size();
        }

        public boolean isFrozen() {
            return frozen;
        }

        public Engine getEngine() {
            return engine;
        }
    }

    static TestData buildTest(final Collection<Weighted<String>> templates, final int ordersCount, final int opportunitiesCount, final PrintWriter progressWriter) throws IndexingException {
        final TestData data = new TestData();

        for (int i = 1; i <= ordersCount; i++) {
            if (i % 100 == 0 && progressWriter != null) progressWriter.println("i=" + i);
            generateRandomParameters(i, 100, 100, 1d, Order.RANDOM, Order.RANDOM, templates, data.specifications, data.opportunities);
        }

        final Engine engine = new Engine();
        index(engine, data.specifications);

        return data;
    }

    static TestData buildTest(final Collection<Weighted<String>> templates, final int ordersCount, final int opportunitiesCount) throws IndexingException {
        return buildTest(templates, ordersCount, opportunitiesCount, null);
    }

    static LongSortedSet randomQuery(final TestData data, final long[] expectedResults, final Random random) {
        final int r = random.nextInt(data.getOpportunitiesCount());
        final LongSortedSet results = query(data.engine, data.getOpportunityByIndex(r), expectedResults);
        return results;
    }

    TestResult randomQueries(final TestData data, long n) {
        Runnable runnable = new Runnable() {
            public void run() {
                randomQuery(data, null, random);
            }
        };
        TestResult result = new TestResult(runnable);
        result.invoke(n);
        return result;
    }

    protected Random random = new Random();

    static class TestResult {
        private Runnable runnable;

        private long startTime = -1;
        private long invocations = 0;
        private long endTime = -1;
        private long maxTime = Long.MIN_VALUE;
        private long minTime = Long.MAX_VALUE;

        public TestResult(Runnable thread) {
            this.runnable = thread;
        }

        public void invoke() {
            final long start = System.currentTimeMillis();
            runnable.run();
            final long end = System.currentTimeMillis();
            final long time = end - start;
            if (time > maxTime) maxTime = time;
            if (time < minTime) minTime = time;
            invocations++;
        }

        public void invoke(final long invocations) {
            startTime = System.currentTimeMillis();
            for (int i = 0; i < invocations; i++) invoke();
            endTime = System.currentTimeMillis();
        }

        public long getTime() {
            if (invocations <= 0) throw new IllegalStateException();
            return endTime - startTime;
        }

        public long getInvocations() {
            return invocations;
        }

        public double averageTime() {
            if (invocations < 0) throw new IllegalStateException();
            return getTime() / (double)getInvocations();
        }

        public long getMaxTime() {
            if (invocations < 0) throw new IllegalStateException();
            return maxTime;
        }

        public long getMinTime() {
            if (invocations < 0) throw new IllegalStateException();
            return minTime;
        }

        public double getInvocationsPerSecond() {
            if (invocations < 0) throw new IllegalStateException();
            return ((double)invocations) * ( ((double)1000) / getTime() );
        }

        @Override
        public String toString() {
            return "TestResult{" +
                    "time=" + getTime() + "ms" +
                  //  "endTime=" + endTime +
                  //  ", runnable=" + runnable +
                  //  ", startTime=" + startTime +
                    ", invocations=" + invocations +
                    ", maxTime=" + maxTime + "ms" +
                    ", minTime=" + minTime + "ms" +
                    ", avgTime=" + averageTime() + "ms" +
                    ", invocations/s=" + getInvocationsPerSecond() +
                    '}';
        }
    }

}
