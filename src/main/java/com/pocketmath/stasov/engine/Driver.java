/*
package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.OpportunityData;
import com.pocketmath.stasov.attributes.AttrSvcBase;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import javafx.concurrent.Task;
import org.apache.commons.lang.time.StopWatch;

import java.util.HashSet;
import java.util.Set;

*/
/**
 * Created by etucker on 3/30/15.
 *//*

public class Driver {

    public static class Data extends OpportunityData {

        @Override
        protected Set<String> getCity() {
            Set<String> set = new HashSet<String>();
            set.add("Austin");
            return set;
        }

        @Override
        protected Set<String> getDeviceId() {
            return null;
        }

        @Override
        protected Set<String> getDeviceType() {
            Set<String> set = new HashSet<String>();
            set.add("iPhone");
            return set;
        }
    }

    public static class T extends Task<LongSortedSet> {

        volatile OpportunityData data = null;

        final Tree tree;
        final OpportunityQueryBase query;

        public T(final AttrSvcBase attrSvc, final Tree tree) {
            this.tree = tree;
            this.query = new OpportunityQueryBase(attrSvc);
        }

        public void setData(OpportunityData data) {
            this.data = data;
        }

        @Override
        protected LongSortedSet call() throws Exception {
            query.load(data);
            return tree.query(query);
        }

        public void clear() {
            this.data = null;
            query.clear();
        }
    }

    public static void main(String args[]) throws Exception {

        // Instatiate the engine
        Engine engine = new Engine();

        // Add some IOs
        engine.index("City = Austin and DeviceType = iPhone or City = Houston", 1);
        engine.index("City = Singapore and DeviceType = iPhone", 2);
        engine.index("City = \"San Francisco\"", 3);
        engine.index("DeviceType = iPhone", 4);

        // Create a dummy impression opportunity
        MapOpportunityData opp = engine.mapOpportunityData();
        opp.put("city", "singapore");
        opp.put("devicetype", "iphone");

        // Find matching IOs
        LongSortedSet results = engine.query(opp);

        // Show results
        System.out.println("Results=" + results);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < 1024*1024; i++)
            engine.query(opp);
        stopWatch.stop();
        long time = stopWatch.getTime();
        System.out.println(time + "ms");


*/
/*
        Tree tree = new Tree(attrSvc);

        PocketQL pocketQL = new PocketQL(attrSvc);
        pocketQL.index(tree, "City = Austin and DeviceType = iPhone or City = Houston", new long[]{1L});

        System.out.println(tree.prettyPrint());

        System.out.println("Indexed");

        OpportunityData data = new Data();

        T t = new T(attrSvc, tree);
        t.setData(data);
        LongSortedSet result = t.call();

        System.out.println();
        System.out.println("Result = " + result);
        *//*

    }

}
*/
