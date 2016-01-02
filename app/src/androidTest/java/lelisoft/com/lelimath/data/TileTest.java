package lelisoft.com.lelimath.data;

import junit.framework.TestCase;

import lelisoft.com.lelimath.view.Tile;

/**
 * Created by Leoš on 28.12.2015.
 */
public class TileTest extends TestCase {

    public void testInside() {
        Tile tile = new Tile(492.0f, 57.0f, 709.0f, 207.0f);
        assertTrue(tile.isInside(522.1876f, 149.15622f, 20));
    }
}
