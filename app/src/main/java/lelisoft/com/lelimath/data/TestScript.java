package lelisoft.com.lelimath.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Script holding test items.
 * Created by Leoš on 08.12.2016.
 */

public class TestScript {
    /** Key that shall never change */
    private String id;
    /** Name of this test, it shall be very short */
    private String title;
    /** Drawable for this script */
    private String picture;
    /** List of formula items */
    private List<TestItem> items;
    /** Computed value, how many items have been finished already */
    private int finished;
    /** Computed value, average score of finished items: 1 is no error, 0 is worst */
    private float score;

    public TestScript() {
    }

    public TestScript(String caption, int finished, int count, float score) {
        this.title = caption;
        this.finished = finished;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<TestItem> getItems() {
        return items;
    }

    public int getCount() {
        return (items == null) ? 0 : items.size();
    }

    public void add(TestItem value) {
        if (items == null) {
            items = new ArrayList<>(5);
        }
        items.add(value);
    }

    public void setItems(List<TestItem> items) {
        this.items = items;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "TestScript{" +
                "id='" + id + '\'' +
                '}';
    }
}
