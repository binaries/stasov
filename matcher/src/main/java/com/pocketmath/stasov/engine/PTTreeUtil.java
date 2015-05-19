package com.pocketmath.stasov.engine;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by etucker on 5/18/15.
 */
public class PTTreeUtil {

    static String idTypesToString(final Collection collection) {
        final StringBuffer sb = new StringBuffer();
        sb.append('{');
        Iterator itr = collection.iterator();
        while (itr.hasNext()) {
            Object o = itr.next();
            if (o instanceof String) {
                sb.append((String)o);
            } else if (o instanceof Integer) {
                sb.append(((Integer)o).intValue());
            } else if (o instanceof Short) {
                sb.append(((Short) o).shortValue());
            } else if (o instanceof Long) {
                sb.append(((Long) o).longValue());
            } else if (o instanceof Float) {
                sb.append(((Float) o).floatValue());
            } else if (o instanceof Double) {
                sb.append(((Double) o).doubleValue());
            } else if (o instanceof Character) {
                sb.append(((Character) o).charValue());
            } else if (o instanceof Byte) {
                sb.append(((Byte) o).byteValue());
            } else throw new UnsupportedOperationException();

            if (itr.hasNext())
                sb.append(", ");
        }

        sb.append('}');

        return sb.toString();
    }

}
