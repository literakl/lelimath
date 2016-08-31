package lelisoft.com.lelimath.data;

import com.google.gson.Gson;

import junit.framework.TestCase;

import lelisoft.com.lelimath.view.DressPart;
import lelisoft.com.lelimath.view.Figure;

/**
 * Test
 * Created by Leoš on 30.08.2016.
 */
public class DressUpTest extends TestCase {

    public void testGenerate() {
        DressPart figurePart = new DressPart("main", 0);
        figurePart.setCoordinates(10, 20, 300, 400);
        DressPart blondHair = new DressPart("bh", 0);
        blondHair.setCoordinates(15, 0, 30, 50);
        DressPart greenHair = new DressPart("gh", 100);
        greenHair.setCoordinates(15, 0, 30, 50);
        blondHair.setEquivalents(new String[] {greenHair.getId()});
        greenHair.setEquivalents(new String[] {blondHair.getId()});
        DressPart skirt = new DressPart("sk", 100);
        skirt.setCoordinates(15, 0, 30, 50);

        Figure figure = new Figure();
        figure.setId("Lisa");
        figure.setH(330);
        figure.setW(400);
        figure.setMain(figurePart);
        figure.setParts(new DressPart[] {blondHair, greenHair, skirt});

        Gson gson = new Gson();
        String json = gson.toJson(figure);
        System.out.println(json);
        assertEquals(figure.getParts().length, 3);
    }
}
