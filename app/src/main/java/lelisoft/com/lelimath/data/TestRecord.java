package lelisoft.com.lelimath.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Holder for the reference to the last Play started from TestFolder
 * Created by Leoš on 08.12.2016.
 */

@DatabaseTable(tableName = "test_record")
public class TestRecord {
    @DatabaseField(generatedId = true, columnName = Columns.ID)
    Integer id;

    @DatabaseField(foreign = true, columnName= Columns.PLAY_ID)
    Play play;

    @DatabaseField(canBeNull = false, columnName = Columns.SCORE)
    Integer score;

    @DatabaseField(canBeNull = false, columnName = Columns.TEST_ID)
    String testId;

    @DatabaseField(canBeNull = false, columnName = Columns.SCRIPT_ID)
    String scriptId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Play getPlay() {
        return play;
    }

    public void setPlay(Play play) {
        this.play = play;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    @Override
    public String toString() {
        return "TestRecord{" +
                "testId='" + testId + '\'' +
                ", scriptId='" + scriptId + '\'' +
                ", score=" + score +
                '}';
    }
}
