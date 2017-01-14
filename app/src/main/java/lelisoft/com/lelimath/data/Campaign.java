package lelisoft.com.lelimath.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Script holding test items.
 * Created by Leoš on 08.12.2016.
 */

public class Campaign implements Serializable {
    /** Key that shall never change */
    private String id;
    /** Name of this test, it shall be very short */
    private String title;
    /** Drawable for this script */
    private String picture;
    /** List of formula items */
    private List<Test> items;
    /** Computed value, how many items have been finished already */
    private int finished;
    /** Computed value, average score of finished items: 1 is no error, 0 is worst */
    private float score;

    public Campaign() {
    }

    public Campaign(String caption, int finished, float score, String picture) {
        this.title = caption;
        this.finished = finished;
        this.score = score;
        this.picture = picture;
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

    public List<Test> getItems() {
        return items;
    }

    public int getCount() {
        return (items == null) ? finished + 1 : items.size();
    }

    public void add(Test value) {
        if (items == null) {
            items = new ArrayList<>(5);
        }
        items.add(value);
    }

    public void setItems(List<Test> items) {
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
        return "Campaign{" +
                "id='" + id + '\'' +
                '}';
    }
}
