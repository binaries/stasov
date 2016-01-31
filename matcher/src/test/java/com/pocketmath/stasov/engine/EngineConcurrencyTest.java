package com.pocketmath.stasov.engine;


// TODO: Stub to implement.

import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

/**
 * Created by etucker on 8/27/15.
 */
public class EngineConcurrencyTest extends EngineTestBase {

    @Test
    public void simpleIndexTest1() throws IndexingException {
        Engine engine = Engine.newConcurrentEngine();

        String spec = "DeviceType in (\"iPhone\")";
        long id = 1;
        engine.index(spec, id);
    }

    @Test
    public void test1_in() throws IndexingException {
        testIndexAndQuery(
                Engine.newConcurrentEngine(),
                ImmutableMap.<Long, String>of(
                        1L, "devicetype in (\"iphone\")"
                ),
                ImmutableMap.<String, String>of(
                        "devicetype", "iphone"
                ),
                new long[]{1L}
        );
    }

    @Test
    public void test2_in() throws IndexingException {
        testIndexAndQuery(
                Engine.newConcurrentEngine(),
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
                Engine.newConcurrentEngine(),
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
                Engine.newConcurrentEngine(),
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
                Engine.newConcurrentEngine(),
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
                Engine.newConcurrentEngine(),
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
                Engine.newConcurrentEngine(),
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
                Engine.newConcurrentEngine(),
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

        // (creativesize IN ("300x250") AND devicetype IN ("android")
        // OR city IN ("austin") AND devicetype IN ("iphone")
        // OR devicetype IN ("iphone") AND creativesize IN ("300x250")
        // OR city IN ("austin") AND devicetype IN ("android"))

        // output 1:
        // (city IN ("austin") AND devicetype IN ("android")
        // OR creativesize IN ("300x250") AND devicetype IN ("android")
        // OR city IN ("austin") AND devicetype IN ("iphone")
        // OR creativesize IN ("300x250") AND devicetype IN ("iphone"))

        testIndexAndQuery(
                Engine.newConcurrentEngine(),
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
                Engine.newConcurrentEngine(),
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
                Engine.newConcurrentEngine(),
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

    @Test(enabled=false)
    public void test200_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newConcurrentEngine(),
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

    @Test(enabled=false)
    public void test201_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newConcurrentEngine(),
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

    @Test(enabled=false)
    public void test201b_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newConcurrentEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "NOT (devicetype = \"android\" AND devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
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

    @Test(enabled=false)
    public void test202_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newConcurrentEngine(),
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

    @Test(enabled=false)
    public void test203_eq_first_io_of_202() throws IndexingException {
        testIndexAndQuery(
                Engine.newConcurrentEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "NOT (devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new long[]{}
        );
    }

    @Test(enabled=false)
    public void test204_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newConcurrentEngine(),
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

    @Test(enabled=false)
    public void test205_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newConcurrentEngine(),
                ImmutableMap.<Long, String>of(
                        1L, "NOT (devicetype = \"android\" OR NOT devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String, String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new long[]{}
        );
    }

    @Test
    public void test300() throws IndexingException {
        testIndexAndQuery(
                Engine.newConcurrentEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "city IN (\"austin\", \"london\") AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new long[]{1L}
        );
    }

    @Test
    public void test301() throws IndexingException {
        System.out.println("hello");
        testIndexAndQuery(
                Engine.newConcurrentEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "city IN (\"austin\", NOT \"london\") AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new long[]{1L}
        );
    }

    @Test
    public void test302() throws IndexingException {
        testIndexAndQuery(
                Engine.newConcurrentEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "city IN (\"austin\", NOT \"london\") AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "london",
                        "creativesize", "300x250"
                ),
                new long[]{}
        );
    }

}
