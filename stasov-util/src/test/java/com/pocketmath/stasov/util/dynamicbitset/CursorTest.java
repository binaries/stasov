package com.pocketmath.stasov.util.dynamicbitset;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by etucker on 5/23/16.
 */
public class CursorTest {
    @BeforeMethod
    public void setUp() throws Exception {

    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

    /*
    @Test
    public void test1() throws Exception {
        SBS3 o = new SBS3();
        o.set(5);
        o.set(10);

        Cursor c = new Cursor(o);
        Assert.assertTrue(c.getCurrentChangeBlock() < 0);
        int nextci = c.nextBlock();
        Assert.assertTrue(nextci >= 0);
    }
    */

    /*
    @Test
    public void test2() throws Exception {
        SBS3 o = new SBS3();
        o.set(5);
        o.set(1024*1024);

        Cursor c = new Cursor(o);
        Assert.assertTrue(c.getCurrentChangeBlock() < 0);
        final int nextci1 = c.nextBlock();
        //Assert.assertTrue(nextci1 >= 0);

        final int nextci2 = c.nextBlock();
        //Assert.assertTrue(nextci2 >= 1);

        final int nextci3 = c.nextBlock();
        //Assert.assertTrue(nextci3 >= 1);

        final int nextci4 = c.nextBlock();
        //Assert.assertTrue(nextci4 >= 1);
    }
    */

}