package com.pocketmath.stasov.util.multimaps;

import com.pocketmath.stasov.util.PrettyPrintable;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by etucker on 2/3/16.
 */
class Long2Long2ObjectMapUtil {

    static <V> String prettyPrint(final ILong2Long2ObjectMultiValueMap map, final String prefix, final String key1Prefix, final String key2Prefix, final String valuePrefix) {
        if (map.isEmpty()) return "EMPTY\n\r";
        StringWriter sw = new StringWriter();
        PrintWriter w = new PrintWriter(sw);
        final String t1 = prefix + "  " + key1Prefix;
        final String t2 = prefix + "    " + key2Prefix;
        final String t3 = prefix + "      " + valuePrefix;
        w.println(prefix + "{");
        for (final long key1 : map.getKeys1()) {
            w.println(t1 + key1 + " = ");
            for (final long key2 : map.getKeys2(key1)) {
                final ObjectSet<V> entry2Values = map.get(key1, key2);
                w.println(t2 + key2 + " = ");
                for (final V value : entry2Values) {
                    if (value instanceof PrettyPrintable) {
                        PrettyPrintable prettyPrintable = (PrettyPrintable) value;
                        w.println(t3 + prettyPrintable.prettyPrint(t3 + "  "));
                    } else
                        w.println(t3 + value);
                }
            }
        }
        w.println(prefix + "}");

        return sw.toString();
    }
}
