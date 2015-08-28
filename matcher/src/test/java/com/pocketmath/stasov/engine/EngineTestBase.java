package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.attributes.Order;
import com.sun.tools.doclint.HtmlTag;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import org.testng.Assert;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by etucker on 8/27/15.
 */
public class EngineTestBase {

    /**
     *
     * @param specifications The PMTL specification.
     * @param opportunityAttributes The attributes from a simulated impression opportunity.
     * @param expectedResults The expected IO ids.
     * @throws IndexingException
     */
    private static void testIndexAndQuery(
            final Engine engine,
            final Map<Long,String> specifications,
            final Map<String,String> opportunityAttributes,
            final long[] expectedResults) throws IndexingException {

        System.out.println("specs:  " + specifications.toString());
        System.out.println("opp:    " + opportunityAttributes.toString());
        System.out.println("expect: " + Arrays.toString(expectedResults));

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

        if (expectedResults != null) {

            if (expectedResults.length < 1) {
                Assert.assertNull(results);
            } else {
                Assert.assertNotNull(results);
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

    }

    static void testIndexAndQuery(
            final Map<Long,String> specifications,
            final Map<String,String> opportunityAttributes,
            final long[] expectedResults) throws IndexingException {

        final Engine engine = new Engine();

        testIndexAndQuery(engine, specifications, opportunityAttributes, expectedResults);
    }

    static void generateRandomParameters(
            final long ioId,
            final int maxValuesPerAttr,
            final int maxAttrs,
            final Order attributesOrder,
            final Order attributeValuesOrder,
            final String template,
            final Map<Long,String> specifications,
            final Map<String,String> opportunityAttributes) {

        if (ioId < 1) throw new IllegalArgumentException();
        if (maxValuesPerAttr < 1) throw new IllegalArgumentException();
        if (maxAttrs < 1) throw new IllegalArgumentException();
        if (attributesOrder == null) throw new IllegalArgumentException();
        if (attributesOrder != Order.UNDEFINED) throw new UnsupportedOperationException("Attribute orders other than Order.UNDEFINED are not yet supported.  Specified order was: " + attributesOrder);
        if (attributeValuesOrder == null) throw new IllegalArgumentException();
        if (template == null) throw new IllegalArgumentException();
        if (template.isEmpty()) throw new IllegalArgumentException();
        if (specifications == null) throw new IllegalArgumentException();
        if (opportunityAttributes == null) throw new IllegalArgumentException();

        final Engine engine = new Engine();
        final AttrSvcBase attrSvc = engine.attrSvc;

        String spec = template;

        final long[] attrTypeIds = attrSvc.getAttrTypeIds();
        Arrays.sort(attrTypeIds);

        int seq = 1;

        for (final long attrTypeId : attrTypeIds) {
            final Iterable<String> attrValues;
            try {
                attrValues = attrSvc.sampleValues(attrTypeId, attributeValuesOrder);
            } catch (UnsupportedOperationException e) {
                Logger.getAnonymousLogger().log(Level.WARNING, "Sample values were not supported for attrTypeId={0}", attrTypeId);
                continue;
            }

            final String k = attrSvc.getAttrName(attrTypeId);

            if (attrValues == null) throw new UnsupportedOperationException("attribute sample values not found for attrTypeId: " + attrTypeId);
            for (final String attrValue : attrValues) {
                Logger.getAnonymousLogger().log(Level.INFO, "k={0}", k);
                final String v = attrValue;
                spec = spec.replace("${k" + seq + "}", k);
                spec = spec.replace("${v" + seq + "}", v);
                seq++;

                System.out.println(specifications.toString());
            }
        }

        specifications.put(ioId, spec);
    }

}
