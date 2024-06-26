package com.pocketmath.stasov.engine;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;

/**
 * Created by etucker on 6/9/15.
 */
public class EngineLogicTest extends EngineTestBase {

    @Test
    public void simpleIndexTest1() throws IndexingException {
        Engine engine = Engine.newLongEngine();

        final String spec = "DeviceType in (\"iPhone\")";
        long id = 1;
        engine.index(spec, id);
    }

    @Test
    public void simpleIndexTest2() throws IndexingException {
        Engine engine = Engine.newLongEngine();

        final String spec = "DeviceType in (\"iPhone\", \"Android\")";
        long id = 1;
        engine.index(spec, id);
    }

    @Test
    public void test1_in() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long, String>of(
                        1L, "devicetype in (\"iphone\")"
                ),
                ImmutableMap.<String, String>of(
                        "devicetype", "iphone"
                ),
                new Long[]{1L}
        );
    }

    @Test
    public void test2_in() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "devicetype in (\"iphone\")"
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin"
                ),
                new Long[]{ 1L }
        );
    }

    @Test
    public void test3_in() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "devicetype in (\"iphone\") and city in (\"austin\")"
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin"
                ),
                new Long[]{ 1L }
        );
    }

    @Test
    public void test4_in() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "devicetype in (\"android\")"
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin"
                ),
                new Long[]{}
        );
    }

    @Test
    public void test5_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin"
                ),
                new Long[]{1L}
        );
    }

    @Test
    public void test6_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "devicetype = \"android\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin"
                ),
                new Long[]{}
        );
    }

    @Test
    public void test100_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "devicetype = \"android\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin"
                ),
                new Long[]{2L}
        );
    }

    @Test
    public void test101_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "devicetype = \"android\" AND city = \"london\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin"
                ),
                new Long[]{2L}
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
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{1L,2L}
        );
    }

    @Test
    public void test103_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone"
                ),
                new Long[]{2L}
        );
    }

    @Test
    public void test103b_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{1L,2L}
        );
    }

    @Test
    public void test103c_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{}
        );
    }

    @Test
    public void test103d_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "creativesize", "300x250"
                ),
                new Long[]{}
        );
    }

    @Test
    public void test103e_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        // empty
                ),
                new Long[]{}
        );
    }

    @Test
    public void test103f_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        // empty
                ),
                null
        );
    }

    @Test
    public void test103g_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "asjkdhfkjlwqh3riu23hrfjksadhfaskjdfasdf", // non-existent city
                        "creativesize", "300x250"
                ),
                null
        );
    }

    @Test
    public void test103h_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250",
                        "deviceid", "xxxxxxxxxxxxxxxxxx"  // device id is not mentioned in order specs
                ),
                new Long[]{1L}
        );
    }

    @Test
    public void test103i_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin",
                        "creativesize", "300x250",
                        "deviceid", "xxxxxxxxxxxxxxxxxx"  // device id is not mentioned in order specs
                ),
                new Long[]{1L,2L}
        );
    }

    @Test
    public void test103j_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "windows",
                        "city", "austin",
                        "creativesize", "300x250",
                        "deviceid", "xxxxxxxxxxxxxxxxxx"  // device id is not mentioned in order specs
                ),
                new Long[]{}
        );
    }

    @Test
    public void test103k_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"android\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "windows",
                        "city", "austin",
                        "creativesize", "300x250",
                        "deviceid", "xxxxxxxxxxxxxxxxxx"  // device id is not mentioned in order specs
                ),
                new Long[]{}
        );
    }

    @Test
    public void test103l_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"android\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250",
                        "deviceid", "xxxxxxxxxxxxxxxxxx"  // device id is not mentioned in order specs
                ),
                new Long[]{1L,2L}
        );
    }

    @Test
    public void test103m_eq() throws IndexingException {
        boolean exceptionFound = false;
        try {
            testIndexAndQuery(
                    Engine.newLongEngine(),
                    ImmutableMap.<Long, String>of(
                            1L, "(devicetype = \"android\" OR devicetype = \"iphone\" OR devicetype = \"asdfasdfasdfk4u74365283234545gsf\") AND city = \"austin\" AND creativesize = \"300x250\"",
                            2L, "devicetype = \"android\""
                    ),
                    ImmutableMap.<String, String>of(
                            "devicetype", "android",
                            "city", "austin",
                            "creativesize", "300x250",
                            "deviceid", "xxxxxxxxxxxxxxxxxx"  // device id is not mentioned in order specs
                    ),
                    new Long[]{1L, 2L}
            );
        } catch (Exception e) {
            exceptionFound = true;
        }
        Assert.assertTrue(exceptionFound);
    }

    @Test
    public void test104_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{1L}
        );
    }

    @Test(enabled=false)
    public void test200_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" AND NOT devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{1L}
        );
    }

    @Test(enabled=false)
    public void test201_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(NOT devicetype = \"android\" AND devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{}
        );
    }

    @Test(enabled=false)
    public void test201b_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "NOT (devicetype = \"android\" AND devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{}
        );
    }

    @Test(enabled=false)
    public void test202_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "NOT (devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{}
        );
    }

    @Test(enabled=false)
    public void test203_eq_first_io_of_202() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "NOT (devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{}
        );
    }

    @Test(enabled=false)
         public void test204_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "NOT (NOT devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{1L}
        );
    }

    @Test(enabled=false)
    public void test205_eq() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long, String>of(
                        1L, "NOT (devicetype = \"android\" OR NOT devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String, String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{}
        );
    }

    @Test
    public void test300() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "city IN (\"austin\", \"london\") AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{1L}
        );
    }

    @Test
    public void test301() throws IndexingException {
        System.out.println("hello");
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "city IN (\"austin\", NOT \"london\") AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{1L}
        );
    }

    @Test
    public void test302() throws IndexingException {
        testIndexAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "city IN (\"austin\", NOT \"london\") AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "london",
                        "creativesize", "300x250"
                ),
                new Long[]{}
        );
    }

    @Test
    public void test401() throws IndexingException {
        testIndexRemoveAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long, String>of(
                        1L, "devicetype in (\"iphone\")"
                ),
                ImmutableSet.<Long>of(1L),
                ImmutableMap.<String, String>of(
                        "devicetype", "iphone"
                ),
                new Long[]{}
        );
    }

    @Test
    public void test402_eq() throws IndexingException {
        testIndexRemoveAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableSet.<Long>of(1L),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{2L}
        );
    }

    @Test
    public void test403_eq() throws IndexingException {
        testIndexRemoveAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "devicetype = \"android\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableSet.<Long>of(2L),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{}
        );
    }

    @Test
    public void test404_eq() throws IndexingException {
        testIndexRemoveAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableSet.<Long>of(),
                ImmutableMap.<String,String>of(
                        "devicetype", "iphone",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{1L,2L}
        );
    }

    @Test
    public void test405_eq() throws IndexingException {
        testIndexRemoveAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "(devicetype = \"android\" OR devicetype = \"iphone\") AND city = \"austin\" AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableSet.<Long>of(2L),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "austin",
                        "creativesize", "300x250"
                ),
                new Long[]{1L}
        );
    }

    @Test
    public void test410() throws IndexingException {
        testIndexRemoveAndQuery(
                Engine.newLongEngine(),
                ImmutableMap.<Long,String>of(
                        1L, "city IN (\"austin\", NOT \"london\") AND creativesize = \"300x250\"",
                        2L, "devicetype = \"iphone\""
                ),
                ImmutableSet.<Long>of(1L),
                ImmutableMap.<String,String>of(
                        "devicetype", "android",
                        "city", "london",
                        "creativesize", "300x250"
                ),
                new Long[]{}
        );
    }

}
