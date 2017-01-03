package lelisoft.com.lelimath.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * One item of test script holding single FormulaDefinition.
 * Created by leos.literak on 26.2.2015.
 */
public class TestItem implements Serializable {
    /** Key that shall never change */
    private String id;
    /** Name of this test, it shall be short */
    private FormulaDefinition definition;
    /** list of allowed games */
    private List<Game> games;
    /** record of finished execution of this item. it must be singleton - overwritten if this test is played again */
    private TestRecord record;

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

    public List<Game> getGames() {
        return games;
    }

    public void add(Game value) {
        if (games == null) {
            games = new ArrayList<>(2);
        }
        games.add(value);
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public TestRecord getRecord() {
        return record;
    }

    public void setRecord(TestRecord record) {
        this.record = record;
    }

    public boolean isFinished() {
        return record != null;
    }

    @Override
    public String toString() {
        return "TestItem{" +
                "id='" + id + '\'' +
                '}';
    }
}
