package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.pmtl.dnfconv.DNFConv;
import com.pocketmath.stasov.pmtl.PocketTLLanguageException;
import it.unimi.dsi.fastutil.longs.LongSortedSet;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by etucker on 4/5/15.
 */
public class Engine {

    private Logger logger = Logger.getLogger(getClass().getName());
    {
        // shameless hard coded logging setup

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINEST);

        logger.setLevel(Level.FINEST);
        logger.addHandler(consoleHandler);
    }

    protected final AttrSvcBase attrSvc;
    protected final MatchTree tree;

    protected final PocketTLIndexer pocketQL;

    public Engine() {
        try {
            this.attrSvc = (AttrSvcBase) Class.forName("com.pocketmath.stasov.attributes.AttributeHandlers").newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e); // TODO: Exception handling.
        }
        this.tree = new MatchTree(attrSvc);
        this.pocketQL = new PocketTLIndexer(attrSvc);
    }

    public void index(final String specification, final long id) throws IndexingException {
        final String dnfSpec;
        try {
            dnfSpec = DNFConv.convertToDNF(specification);
        } catch (PocketTLLanguageException e) {
           throw new IndexingException(e);
        }
        logger.log(Level.FINE, "DNF converted: {0}", dnfSpec);
        if (dnfSpec == null) throw new IndexingException("DNF converted string was null.");
        if (dnfSpec.isEmpty()) throw new IndexingException("DNF converted string was empty.");
        if (dnfSpec.trim().isEmpty()) throw new IndexingException("DNF converted string was only whitespace.");
        pocketQL.index(tree, dnfSpec, new long[]{id});
    }

    public LongSortedSet query(final OpportunityDataBase opportunity) {
        final OpportunityQueryBase query = new OpportunityQueryBase(attrSvc);
        query.load(opportunity);
        return tree.query(query);
    }

    public MapOpportunityData mapOpportunityData() {
        return new MapOpportunityData(attrSvc);
    }

    @Override
    public String toString() {
        return "Engine{" +
                "attrSvc=" + attrSvc +
                ", tree=" + tree +
                ", pocketQL=" + pocketQL +
                '}';
    }

    public String prettyPrint() {
        return "Engine: tree: " + tree.prettyPrint();
    }
}
