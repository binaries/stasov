package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.OpportunityData;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import org.testng.Assert;
import org.testng.Assert.*;
import org.testng.annotations.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
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

    @Test
    public void simpleIndexAndQueryTest1() throws IndexingException {
        Engine engine = new Engine();

        String spec = "DeviceType in (\"iPhone\")";
        long id = 1;
        engine.index(spec, id);

        OpportunityData opp = new OpportunityData() {
            @Override
            protected Set<String> getCity() {
                return new HashSet<String>(Arrays.asList(new String[]{"Austin"}));
            }

            @Override
            protected Set<String> getDeviceId() {
                return new HashSet<String>(Arrays.asList(new String[]{"abc123"}));
            }

            @Override
            protected Set<String> getDeviceType() {
                return new HashSet<String>(Arrays.asList(new String[]{"iPhone"}));
            }
        };

        final LongSortedSet results = engine.query(opp);

        Assert.assertEquals(results.size(), 1);
        Assert.assertTrue(results.contains(1L));
    }

    @Test
    public void ssimpleIndexTest2() throws IndexingException {
        Engine engine = new Engine();

        String spec = "DeviceType = \"iPhone\"";
        long id = 1;
        engine.index(spec, id);
    }

}
