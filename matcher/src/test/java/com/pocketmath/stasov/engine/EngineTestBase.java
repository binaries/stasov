package com.pocketmath.stasov.engine;

import com.google.common.collect.Sets;
import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.attributes.Order;
import com.pocketmath.stasov.util.StasovArrays;
import com.pocketmath.stasov.util.Weighted;
import com.sun.tools.doclint.HtmlTag;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import org.apache.commons.lang.ArrayUtils;
import org.testng.Assert;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            System.out.println("PMTL: " + pmtl);
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

}
