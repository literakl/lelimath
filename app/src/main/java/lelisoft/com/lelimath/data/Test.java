package lelisoft.com.lelimath.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * One item of campaign holding single FormulaDefinition.
 * Created by leos.literak on 9.12.2016.
 */
public class Test implements Serializable {
    /** Key that shall never change */
    private String id;
    /** Name of this test, it shall be short */
    private FormulaDefinition definition;
    /** list of allowed games */
    private List<Game> games;

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

    @SuppressWarnings("unused")
    public void setGames(List<Game> games) {
        this.games = games;
    }

    @Override
    public String toString() {
        return "Test{" +
                "id='" + id + '\'' +
                '}';
    }
}
