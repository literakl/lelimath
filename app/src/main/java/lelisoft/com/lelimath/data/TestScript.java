package lelisoft.com.lelimath.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Script holding test items.
 * Created by Leoš on 08.12.2016.
 */

public class TestScript {
    /** Key that shall never change */
    String id;
    /** Name of this test, it shall be very short */
    String title;
    /** Drawable for this script */
    String picture;
    /** List of formula items */
    List<TestItem> items;

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

    public void add(TestItem value) {
        if (items == null) {
            items = new ArrayList<>(5);
        }
        items.add(value);
    }

    public void setItems(List<TestItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "TestScript{" +
                "id='" + id + '\'' +
                '}';
    }
}
