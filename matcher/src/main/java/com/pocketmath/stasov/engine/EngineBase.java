package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.pmtl.dnfconv.DNFConv;
import com.pocketmath.stasov.pmtl.PocketTLLanguageException;
import it.unimi.dsi.fastutil.longs.LongSortedSet;

import java.io.Serializable;
import java.util.BitSet;
import java.util.SortedSet;
import java.util.function.Consumer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by etucker on 4/5/15.
 */
class EngineBase<ObjectType extends Serializable & Comparable> extends Engine<ObjectType> {

    private Logger logger = Logger.getLogger(getClass().getName());
    {
        // shameless hard coded logging setup

        final EngineConfig cfg = EngineConfig.getConfig();
        final Level level = cfg.getLogLevel();

        final ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(level);

        logger.setLevel(level);
        logger.addHandler(consoleHandler);
    }

    protected final AttrSvcBase attrSvc;
    protected final MatchTree tree;

    protected final PocketTLIndexer indexer;
    protected final Tracker tracker;
    protected final IdTranslator<ObjectType> idTranslator;

    protected final boolean safeIndex;

    EngineBase(final boolean safeIndex) {
        if (!safeIndex) {
            logger.warning("Safe indexing mode has been turned off.  This is not recommended.  Will conduct fewer safety checks during indexing in exchange for more performance.");
        }
        this.safeIndex = safeIndex;
        try {
            this.attrSvc = (AttrSvcBase) Class.forName("com.pocketmath.stasov.attributes.AttributeHandlers").newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e); // TODO: Exception handling.
        }
        this.tracker = new Tracker();
        this.idTranslator = new IdTranslator(tracker);
        this.tree = new MatchTree(attrSvc, tracker, idTranslator);
        this.indexer = new PocketTLIndexer(attrSvc);

        checkInvariants();
    }

    EngineBase() {
        this(true);
    }

    private void checkInvariants() {
        if (attrSvc == null) throw new IllegalStateException();
        if (attrSvc.getAttrsComparator() == null) throw new IllegalStateException();
        if (attrSvc.getAttrTypeIds() == null) throw new IllegalStateException();
        if (tracker == null) throw new IllegalStateException();
        if (idTranslator == null) throw new IllegalStateException();
        if (tree == null) throw new IllegalStateException();
        if (indexer == null) throw new IllegalStateException();
    }

    public void index(final String pmtl, final ObjectType id) throws IndexingException {
        if (safeIndex) {
            try {
                if (pmtl == null) throw new IllegalArgumentException("PMTL was null.");
                if (pmtl.isEmpty()) throw new IllegalArgumentException("PMTL was an empty string.");
                if (pmtl.trim().isEmpty()) throw new IllegalArgumentException("PMTL contained only whitespace.");
                if (pmtl.replaceAll("[A-Za-z0-9]", "").equals(pmtl))
                    throw new IllegalArgumentException("PMTL contained no alphanumeric characters.");
                if (id == null) throw new IllegalArgumentException("ID was null.");
                if (id instanceof String) {
                    final String sid = (String) id;
                    if (sid.isEmpty()) throw new IllegalArgumentException("ID was an empty string.");
                    if (sid.trim().isEmpty()) throw new IllegalArgumentException("ID contained only whitespace.");
                } else if (id instanceof Long) {
                    final long lid = ((Long) id).longValue(); // explicit unboxing may aid clarity and prevent deferred implicit unboxing
                    if (lid < 1)
                        throw new IllegalArgumentException("ID was a long integer value of less than 1: " + lid);
                } else if (id instanceof Integer) {
                    final int iid = ((Integer) id).intValue();
                    if (iid < 1) throw new IllegalArgumentException("ID was a integer value of less than 1: " + iid);
                } else if (id instanceof Short) {
                    final int sid = ((Short) id).shortValue();
                    if (sid < 1)
                        throw new IllegalArgumentException("ID was a short integer value of less than 1: " + sid);
                } else if (id instanceof Byte) {
                    final byte bid = ((Byte) id).byteValue();
                    if (bid < 1) throw new IllegalArgumentException("ID was a byte value of less than 1: " + bid);
                } else if (id instanceof Double) {
                    throw new IllegalArgumentException("Floating-point values are not accepted due to rounding that can result in uniqueness issues.");
                } else if (id instanceof Float) {
                    throw new IllegalArgumentException("Floating-point values are not accepted due to rounding that can result in uniqueness issues.");
                }
                if (!(id instanceof Serializable))
                    throw new IllegalArgumentException("ID does not implement interface Serializable.");
                if (!(id instanceof Comparable))
                    throw new IllegalArgumentException("ID does not implement the interface Comparable.");
                checkInvariants();
            } catch (Exception e) {
                throw new IndexingException(e);
            }
        }

        final String dnfSpec;
        try {
            dnfSpec = DNFConv.convertToDNF(pmtl); // BNF --> DNF translation
        } catch (PocketTLLanguageException e) {
           throw new IndexingException(e);
        }
        logger.log(Level.FINE, "DNF converted :: {0}", dnfSpec);
        if (dnfSpec == null) throw new IndexingException("DNF converted string was null.");
        if (dnfSpec.isEmpty()) throw new IndexingException("DNF converted string was empty.");
        if (dnfSpec.trim().isEmpty()) throw new IndexingException("DNF converted string was only whitespace.");

        final long internalId = idTranslator.toId(id);

        indexer.index(tree, dnfSpec, new long[]{internalId});
        idTranslator.refreshIds(1);

        if (safeIndex) {
            try {
                checkInvariants();
            } catch (Exception e) {
                throw new IndexingException(e);
            }
        }
    }

    /*
    public void index(final String pmtl, final long id) throws IndexingException {
        if (id < 1) throw new IllegalArgumentException();
        final Long l = new Long(id);
        final ObjectType o;
        try {
            o = (ObjectType) l;
        } catch (ClassCastException cce) {
            throw new IndexingException(
                    "The type long is not supported by this implementation. (Check the type of Engine<ObjectType>.)",
                    new UnsupportedOperationException(cce));
        }
        doIndex(pmtl, o);
    }
    */

    public void remove(final ObjectType id) throws IndexingException {
        checkInvariants();
        final long internalId = idTranslator.toId(id);
        logger.log(Level.FINE, "Removing id: " + id + " (translated to internal id: " + internalId + ")");
        tree.remove(internalId);
        tracker.diassociate(internalId);
        idTranslator.remove(id);
        checkInvariants();
    }

    public ObjectType[] query(final OpportunityDataBase opportunity) {
        final OpportunityQueryBase query = new OpportunityQueryBase(attrSvc);
        query.load(opportunity);
        final SortedSet<ObjectType> objects = tree.query(query);
        if (objects == null) return null;
        final ObjectType[] results = (ObjectType[]) new Serializable[objects.size()];
        int i = 0;
        for (final ObjectType o : objects) {
            results[i] = o; // use fast, less safe method for fast queries
            i++;
        }
        return results;
    }

    //public MapOpportunityData mapOpportunityData() {
    //    return new MapOpportunityData(attrSvc);
    //}


    @Override
    public String toString() {
        return "Engine{" +
                "attrSvc=" + attrSvc +
                ", tree=" + tree +
                ", indexer=" + indexer +
                ", tracker=" + tracker +
                ", idTranslator=" + idTranslator +
                ", safeIndex=" + safeIndex +
                '}';
    }

    @Override
    public String prettyPrint() {
        return "Engine: tree: " + tree.prettyPrint();
    }

    @Override
    AttrSvcBase getAttrSvc() {
        return attrSvc;
    }
}
