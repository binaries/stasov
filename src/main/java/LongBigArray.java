/**
 * Created by etucker on 3/16/15.
 */
public class LongBigArray {

    public static long get(long[][] array, final long index) {
        return array[(int)(index<<32)][(int)index];
    }

    public static void set(long[][] array, final long index, final long value) {
        array[(int)(index<<32)][(int)index] = value;
    }

}
