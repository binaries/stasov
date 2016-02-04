package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.attributes.Order;
import com.pocketmath.stasov.util.StasovArrays;
import com.pocketmath.stasov.util.Weighted;

import org.apache.commons.lang.ArrayUtils;
import org.checkerframework.checker.igj.qual.ReadOnly;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.testng.Assert;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.PrintWriter;
import java.text.DecimalFormat;
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
            final @NonNull @ReadOnly Engine engine,
            final @NonNull @ReadOnly Map<Long,String> specifications) throws IndexingException {

        if (engine == null) throw new IllegalArgumentException("engine was null");
        if (specifications == null) throw new IllegalArgumentException("specifications was null");

        for (final Map.Entry<Long, String> spec : specifications.entrySet()) {
            final Long id = spec.getKey();
            final String pmtl = spec.getValue();
            //System.out.println("PMTL: " + pmtl);
            if (id == null) throw new IllegalStateException("id was null");
            if (pmtl == null) throw new IllegalStateException("PMTL was null");
            engine.index(pmtl, id);
        }
    }

    static @Nullable Object[] query(
            final @NonNull @ReadOnly Engine engine,
            final @NonNull @ReadOnly Map<String,String> opportunityAttributes,
            final @Nullable @ReadOnly Object[] expectedResults) {

        if (engine == null) throw new IllegalArgumentException("engine was null");
        if (opportunityAttributes == null) throw new IllegalArgumentException("opportunity attributes was null");

        final MapOpportunityData opp = new MapOpportunityData(engine.getAttrSvc());

        for (final Map.Entry<String, String> attribute : opportunityAttributes.entrySet()) {
            final String name = attribute.getKey();
            final String value = attribute.getValue();
            if (name == null) throw new IllegalStateException();
            if (value == null) throw new IllegalStateException();
            opp.put(name, value);
        }

        //System.out.println("oppObj: " + opp);

        //System.out.println("engine: " + engine.prettyPrint());

        final Object[] results = engine.query(opp);

        return results;
    }

    private static void assertExpectedResult(final @Nullable @ReadOnly Object[] expectedResults, final @Nullable @ReadOnly Object[] actualResults) {
        if (expectedResults != null) {

            if (expectedResults.length < 1) {
                Assert.assertNull(actualResults);
            } else {
                Assert.assertNotNull(actualResults);
            }

            if (actualResults == null) {
                Assert.assertTrue(expectedResults == null || expectedResults.length == 0);
                return;
            }

            { // assert that each result exists in the expected results
                Object[] sortedExpectedResults = expectedResults.clone();
                Arrays.sort(sortedExpectedResults);
                for (final Object result : actualResults) {
                    final int found;
                    if (result instanceof Long)
                        found = Arrays.binarySearch(sortedExpectedResults, (Long)result);
                    else
                        throw new UnsupportedOperationException();
                    Assert.assertTrue(found > -1);
                }
            }

            { // assert that each expected result exists in the actual results
                for (final Object expectedResult : expectedResults) {
                    Assert.assertTrue(Arrays.binarySearch(actualResults, expectedResult) >= 0);
                    //Assert.assertTrue(results.contains(expectedResult));
                }
            }

            Assert.assertTrue(actualResults.length == expectedResults.length);

        }
    }

    /**
     *
     * @param specifications The PMTL specification.
     * @param opportunityAttributes The attributes from a simulated impression opportunity.
     * @param expectedResults The expected IO ids.
     * @throws IndexingException
     */
    static Object[] testIndexRemoveAndQuery(
            final @Nonnull @ReadOnly Engine engine,
            final @Nonnull @ReadOnly Map<Long,String> specifications,
            final @Nonnull @ReadOnly Collection<Long> removals,
            final @Nonnull @ReadOnly Map<String,String> opportunityAttributes,
            final @Nullable @ReadOnly Object[] expectedResults) throws IndexingException {

        System.out.println("specs:  " + specifications.toString());
        System.out.println("opp:    " + opportunityAttributes.toString());
        System.out.println("expect: " + Arrays.toString(expectedResults));

        // index
        index(engine, specifications);

        // remove
        if (removals != null) {
            for (final Long removal : removals) {
                engine.remove(removal);
            }
        }

        //System.out.println("Engine: " + engine.prettyPrint());

        // query
        final Object[] results = query(engine, opportunityAttributes, expectedResults);

        System.out.println("results:  " + Arrays.toString(results));

        // assert output correctness
        assertExpectedResult(expectedResults, results);

        return results;
    }

    /**
     *
     * @param specifications The PMTL specification.
     * @param opportunityAttributes The attributes from a simulated impression opportunity.
     * @param expectedResults The expected IO ids.
     * @throws IndexingException
     */
    static Object[] testIndexRemoveAndQuery(
            final @Nonnull @ReadOnly Map<Long,String> specifications,
            final @Nonnull @ReadOnly Collection<Long> removals,
            final @Nonnull @ReadOnly Map<String,String> opportunityAttributes,
            final @Nullable @ReadOnly Object[] expectedResults) throws IndexingException {

        final Engine engine = Engine.newDefaultEngine();
        return testIndexRemoveAndQuery(engine, specifications, removals, opportunityAttributes, expectedResults);
    }

    /**
     *
     * @param specifications The PMTL specification.
     * @param opportunityAttributes The attributes from a simulated impression opportunity.
     * @param expectedResults The expected IO ids.
     * @throws IndexingException
     */
    static Object[] testIndexAndQuery(
            final @Nonnull @ReadOnly Engine engine,
            final @Nonnull @ReadOnly Map<Long,String> specifications,
            final @Nonnull @ReadOnly Map<String,String> opportunityAttributes,
            final @Nullable @ReadOnly Object[] expectedResults) throws IndexingException {

        return testIndexRemoveAndQuery(engine, specifications, null, opportunityAttributes, expectedResults);
    }

    static Object[] testIndexAndQuery(
            final @Nonnull @ReadOnly Map<Long,String> specifications,
            final @Nonnull @ReadOnly Map<String,String> opportunityAttributes,
            final @Nullable @ReadOnly Object[] expectedResults) throws IndexingException {

        final Engine engine = Engine.newDefaultEngine();
        return testIndexRemoveAndQuery(engine, specifications, null, opportunityAttributes, expectedResults);
    }

    private static String fill(
            final @Nonnull @ReadOnly Engine engine,
            final @Nonnull @ReadOnly String template,
            final @Nonnull @ReadOnly Order attributeTypesOrder,
            final @Nonnull @ReadOnly Order attributeValuesOrder,
            final @Nonnull Collection<Map<String,String>> opportunities) {

        if (engine == null) throw new IllegalArgumentException();
        if (template == null) throw new IllegalArgumentException();
        if (template.isEmpty()) throw new IllegalArgumentException();
        if (attributeTypesOrder == null) throw new IllegalArgumentException();
        if (attributeValuesOrder == null) throw new IllegalArgumentException();
        if (opportunities == null) throw new IllegalArgumentException();

        String spec = new String(template); // copy

        final AttrSvcBase attrSvc = engine.getAttrSvc();

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

    static void generateRandomParameters(
            final Engine engine,
            final @Nonnegative long ioId,
            final int maxValuesPerAttr,
            final int maxAttrs,
            final double matchRate,
            final Order attributeTypesOrder,
            final Order attributeValuesOrder,
            final Collection<Weighted<String>> templates,
            final Map<Long,String> specifications,
            final Collection<Map<String,String>> opportunities) {

        if (engine == null) throw new IllegalArgumentException();
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

        final String template = StasovArrays.chooseRandomWeightedValue(templates);

        final String spec = fill(engine, template, attributeTypesOrder, attributeValuesOrder, opportunities);

        specifications.put(ioId, spec);

        engine.prettyPrint();

    }

    static class TestData {
        private Map<Long,String> specifications = new HashMap<Long,String>();
        private List<Map<String,String>> opportunities = new ArrayList<Map<String,String>>();
        private final Engine engine = Engine.newLongEngine();

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

    static TestData buildTest(final Collection<Weighted<String>> templates, final int ordersCount, final int opportunitiesCount, final PrintWriter progressWriter, final int progressEvery) throws IndexingException {
        if (templates == null) throw new IllegalArgumentException("templates was null");
        if (ordersCount < 1) throw new IllegalArgumentException("no orders");
        if (opportunitiesCount < 0) throw new IllegalArgumentException("opportunities less than 0");

        final TestData data = new TestData();

        final Engine engine = data.getEngine();

        for (int i = 1; i <= ordersCount; i++) {
            if (i % progressEvery == 0 && progressWriter != null) {
                progressWriter.println("i=" + i);
                progressWriter.flush();
            }
            generateRandomParameters(engine, i, 100, 100, 1d, Order.RANDOM, Order.RANDOM, templates, data.specifications, data.opportunities);
        }

        index(engine, data.specifications);

        return data;
    }

    static TestData buildTest(final Collection<Weighted<String>> templates, final int ordersCount, final int opportunitiesCount, final PrintWriter progressWriter) throws IndexingException {
        return buildTest(templates, ordersCount, opportunitiesCount, progressWriter, 100);
    }

    static TestData buildTest(final Collection<Weighted<String>> templates, final int ordersCount, final int opportunitiesCount) throws IndexingException {
        return buildTest(templates, ordersCount, opportunitiesCount, null, 100);
    }

    static Object[] randomQuery(final TestData data, final Object[] expectedResults, final Random random) {
        final int r = random.nextInt(data.getOpportunitiesCount());
        final Object[] results = query(data.engine, data.getOpportunityByIndex(r), expectedResults);
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
        private final long threshold;
        private long overThresholdCount = 0;
        private int threadCount = 1;

        private TestResult() {
            this.runnable = null;
            this.threshold = -1;
        }

        public TestResult(final Runnable thread, final long threshold) {
            this.runnable = thread;
            this.threshold = threshold;
        }

        public TestResult(final Runnable thread) {
            this(thread, 10L);
        }

        public void combineThread(final TestResult o) {
            this.invocations = Math.addExact(this.invocations, o.invocations);
            this.overThresholdCount = Math.addExact(this.overThresholdCount, o.overThresholdCount);
            this.endTime = Math.addExact(this.endTime, o.getTime());
            if (o.minTime < this.minTime) this.minTime = o.minTime;
            if (o.maxTime > this.maxTime) this.maxTime = o.maxTime;
            this.threadCount++;
        }

        public static TestResult combineThreads(final Collection<TestResult> results) {
            final TestResult sum = new TestResult();
            sum.threadCount = 0;
            sum.startTime = 0;
            sum.endTime = 0;
            for (final TestResult r : results) {
                sum.combineThread(r);
            }
            return sum;
        }

        public void invoke() {

            // TODO: handle this more elegantly
            if (runnable == null)
                throw new IllegalStateException("this is not an invokable result instance (probably because it's a combination");

            final long start = System.currentTimeMillis();
            runnable.run();
            final long end = System.currentTimeMillis();
            final long time = end - start;
            if (time > maxTime) maxTime = time;
            if (time < minTime) minTime = time;
            if (time > threshold) overThresholdCount++;
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

        public double getInvocationsPerSecond(final int threadCount) {
            if (invocations < 0) throw new IllegalStateException();
            return ((double)invocations) * ( ((double)1000) / getTime() ) * threadCount;
        }

        public double getInvocationsPerSecond() {
            return getInvocationsPerSecond(1);
        }

        public double calcPercentOverThreshold() {
            return (double)overThresholdCount / (double)invocations * 100d;
        }

        @Override
        public String toString() {
            final DecimalFormat percentFormat = new DecimalFormat("###.###");

            return "TestResult{" +
                    "time=" + getTime() + "ms" +
                  //  "endTime=" + endTime +
                  //  ", runnable=" + runnable +
                  //  ", startTime=" + startTime +
                    ", invocations=" + invocations +
                    ", maxTime=" + maxTime + "ms" +
                    ", minTime=" + minTime + "ms" +
                    ", avgTime=" + averageTime() + "ms" +
                    ", overThresholdCount: " + overThresholdCount + " (" + percentFormat.format(calcPercentOverThreshold()) + "%, t=" + threshold + "ms)" + // TODO: Don't show threshold when not applicable.
                    ", invocations/s=" + getInvocationsPerSecond(threadCount) +
                    '}';
        }
    }

}
