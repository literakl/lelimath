package lelisoft.com.lelimath;

import java.util.Random;

/**
 * Random that returns predefined values.
 * Created by Leoš on 15.08.2016.
 */
public class NotSoRandom extends Random {
    int[] values;
    int position;

    public NotSoRandom(int... args) {
        this.values = args;
    }

    @Override
    public int nextInt() {
        return values[position++];
    }

    @Override
    public int nextInt(int n) {
        return nextInt();
    }
}
