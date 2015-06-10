package com.pocketmath.stasov.pocketql;

import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by etucker on 4/8/15.
 */
public class PQLRRTest extends PQLRRTestBase {

    @Test
    public void test1() throws Exception {
        assertEqualsIgnoreCase(parse("city=austin"), "city=austin");
    }

    @Test
    public void test2() throws Exception {
        assertEqualsIgnoreCase(parse("not not city=austin"), "city=austin");
    }

    @Test
    public void test3_1() throws Exception {
        assertEqualsIgnoreCase(
                parse("(city=austin or city=singapore) and devicetype=iphone"),
                "city=austin AND devicetype=iphone OR city=singapore AND devicetype=iphone");
    }

    @Test
    public void test3_2() throws Exception {
        assertEqualsIgnoreCase(
                parse("devicetype=iphone and (city=austin or city=singapore)"),
                "city=austin AND devicetype=iphone OR city=singapore AND devicetype=iphone");
    }

    @Test
    public void test3_3() throws Exception {
        assertEqualsIgnoreCase(
                parse("(a=1 or b=2) and (c=3 or d=4)"),
                "a=1 AND c=3 OR a=1 AND d=4 OR b=2 AND c=3 OR b=2 AND d=4");
    }

    @Test
         public void test3_4() throws Exception {
        assertEqualsIgnoreCase(
                parse("a=1 AND b=2 AND c=3"),
                "a=1 AND b=2 AND c=3");
    }

    @Test
    public void test3_5() throws Exception {
        assertEqualsIgnoreCase(
                parse("a=1 AND (b=2 AND c=3)"),
                "a=1 AND b=2 AND c=3");
    }

    @Test
    public void test4() throws Exception {
        assertEqualsIgnoreCase(
                parse("((city=austin))"), "city=austin");
    }

    @Test
    public void test5() throws Exception {
        assertEqualsIgnoreCase(
                parse("(city=austin oR city=singapore or city=vancouver) and devicetype=iphone)"),
                "city=austin AND devicetype=iphone OR city=singapore AND devicetype=iphone OR city=vancouver AND devicetype=iphone");
    }

    @Test
    public void test6() throws Exception {
        final String input =
                "e=g and (w=x)";

        final String expected =
                "e=g and w=x";

        assertEqualsIgnoreCase(parse(input), expected);
    }

    @Test
    public void test7() throws Exception {
        final String input =
                "e=g or (w=x)";

        final String expected =
                "e=g or w=x";

        assertEqualsIgnoreCase(parse(input), expected);
    }

    @Test
    public void test_normal_form_1() throws Exception {
        Assert.assertEquals(
                parse("city=austin AND devicetype=iphone OR city=singapore AND devicetype=iphone OR city=vancouver AND devicetype=iphone"),
                "city=austin AND devicetype=iphone OR city=singapore AND devicetype=iphone OR city=vancouver AND devicetype=iphone");
    }

    @Test
    public void test8() throws Exception {
        Assert.assertNotEquals(
                parse("a b c d e f g"),
                "a b c d e f g");
    }

    @Test
    public void test_lots_of_ors() throws Exception {
        String s = "a=1 OR b=2 OR c=3 OR d=4 OR e=5";
        Assert.assertEquals(parse(s),s);
    }

}