package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.AttrSvcBase;
import it.unimi.dsi.fastutil.longs.LongSortedSet;

/**
 * Created by etucker on 4/5/15.
 */
public class Engine {

    protected final AttrSvcBase attrSvc;
    protected final Tree tree;

    protected final PocketQL pocketQL;

    public Engine() {
        try {
            this.attrSvc = (AttrSvcBase) Class.forName("com.pocketmath.stasov.attributes.AttributeHandlers").newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e); // TODO: Exception handling.
        }
        this.tree = new Tree(attrSvc);
        this.pocketQL = new PocketQL(attrSvc);
    }

    public void index(final String specification, final long id) throws IndexingException {
        pocketQL.index(tree, specification, new long[]{id});
    }

    public LongSortedSet query(final OpportunityDataBase opportunity) {
        final OpportunityQueryBase query = new OpportunityQueryBase(attrSvc);
        query.load(opportunity);
        return tree.query(query);
    }

    public MapOpportunityData mapOpportunityData() {
        return new MapOpportunityData(attrSvc);
    }

}
