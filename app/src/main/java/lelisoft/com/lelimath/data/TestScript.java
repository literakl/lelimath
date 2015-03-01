package lelisoft.com.lelimath.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Test script holds a group of FormulaDefinitions.
 * Created by leos.literak on 26.2.2015.
 */
public class TestScript {
    /** Primary key that shall never change */
    String id;
    /** Name of this test, it shall be short */
    String title;
    /** Description of this test */
    String description;
    /** List of formula definitions */
    List<FormulaDefinition> definitions;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FormulaDefinition> getDefinitions() {
        return definitions;
    }

    public void add(FormulaDefinition value) {
        if (definitions == null) {
            definitions = new ArrayList<>(5);
        }
        definitions.add(value);
    }

    public void setDefinitions(List<FormulaDefinition> definitions) {
        this.definitions = definitions;
    }
}
