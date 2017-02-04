package lelisoft.com.lelimath.data;

import java.io.Serializable;

/**
 * One item of campaign holding single FormulaDefinition.
 * Created by leos.literak on 9.12.2016.
 */
public class Test implements Serializable {
    /** Key that shall never change */
    private String id;
    /** Name of this test, it shall be short */
    private FormulaDefinition definition;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FormulaDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(FormulaDefinition definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return "Test{" +
                "id='" + id + '\'' +
                '}';
    }
}
