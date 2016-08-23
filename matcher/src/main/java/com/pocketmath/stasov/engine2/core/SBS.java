package com.pocketmath.stasov.engine2.core;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrays;

/**
 * Created by etucker on 3/22/16.
 */
public class SBS {

    private long[] ranges;
    private final Int2ObjectMap<long[]> map = new Int2ObjectOpenHashMap<>();
    private final int chunkSizeBits;
    private final int chunkSizeLongs;

    public SBS(final int chunkSize) {
        if (chunkSize <= 0) throw new IllegalArgumentException();
        this.chunkSizeLongs = Math.min(1, chunkSize / 64);
        this.chunkSizeBits = chunkSizeLongs * 64;
    }

    public SBS() {
        this(512);
    }

    private boolean mapGet(final int index) {
        assert(index >= 0);
        final int i = index % chunkSizeLongs;
        final long[] array = map.get(i);
        final long l = array[i];
        final int j = index % chunkSizeBits;
        assert(j <= 64);
        if (l < 0)
            return !( ((-l >> j) & 0x1) == 1 );
        else
            return ((l >> j) & 0x1) == 1;
    }

    public boolean get(final int index) {
        assert(index >= 0);
        final long k = index << 32;
        final int x = LongArrays.binarySearch(ranges, k);
        if (x < 0) {
            if (if (-x != ranges.length) {
                // near match
                final long y = -ranges[-x - 1];
                final int startPos = (int) (y >> 32);
                if (startPos <= index) {
                    final int len = (int) y;
                    if (len < 0) return !(index < -len);
                    else return index < len;
                } else {
                    return mapGet(index);
                }
            } else {
                // not found in ranges, try the map
                return mapGet(index);
            }
        } else {
            // exact match (should be rare)
            final long y = ranges[x];
            assert((int)(y >> 32) == index);
            final int len = (int) y;
            if (len < 0) return ! (index < -len);
            else return index < len;
        }
    }

    public static void and(final SBS a, final SBS b) {
        for (int i = 0; i < ranges.length; i++) {
            final long
        }
    }

    public void and1(final SBS o) {
        for (int i = 0; i < ranges.length; i++) {
            final long x = ranges[i];
            final long k = x >> 32;
            final int len = (int)x;
            int ox = LongArrays.binarySearch(o.ranges, k);
            oloop: while (true) {
                if (ox < 0) {
                    // TODO
                } else {
                    final int olen = (int) ox;

                    final int _len = Math.abs(len);
                    final int _olen = Math.abs(olen);

                    if (_len < _olen) {

                        if (len >= 0) {
                            if (olen < 0) {
                                ranges[i] = k << 32 + olen;
                                set(false,k,olen);
                            }
                        }
                    }

                    if (len < 0) { // this range is negative
                        // set to zero from k to shorter of len or olen
                        // TODO
                        // then from the shorter of len or olen do further eval
                        if (olen < 0) {


                            if (len > -olen && (o.ranges[++ox] >> 32) >= k - len) break oloop;
                        } else {
                            // TODO
                            if (len > olen && (o.ranges[++ox] >> 32) >= k - len) break oloop;
                        }
                    } else { // this range is positive
                        // do nothing from k to the shorter of len and olen
                        // then from the shorter of len or olen do further eval
                        if (olen < 0) {
                            if (len < -olen) {
                                set(false, k, olen);
                            }
                            if (len > -olen && (o.ranges[++ox] >> 32) >= k + len) break oloop;
                        } else
                            if (len > olen && (o.ranges[++ox] >> 32) >= k + len) break oloop;
                    }
                }
            }
        }
    }

    public void or(final SBS o) {

    }
/*
    private static void and(final SBS o1, final SBS o2) {
        for(final Int2ObjectMap.Entry<LongArrayList> entry : o1.segments.int2ObjectEntrySet()) {
            final int key = entry.getIntKey();
            final LongArrayList segment2 = o2.segments.get(key);
            if (segment2 == null) {
                if (!o2.emptyIndicatesTrue) {
                    if (!o1.emptyIndicatesTrue) {
                        o1.segments.remove(key);
                    }
                }
            }
        }

    }
*/
}
