package lelisoft.com.lelimath.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Holder for the reference to the last Play started from TestFolder
 * Created by Leoš on 08.12.2016.
 */

@DatabaseTable(tableName = "test_record")
public class TestRecord {
    @DatabaseField(generatedId = true, columnName = Columns.ID)
    Integer id;

    @DatabaseField(foreign = true, columnName= Columns.PLAY_ID)
    private Play play;

    @DatabaseField(canBeNull = false, columnName = Columns.SCORE)
    private Integer score;

    @DatabaseField(canBeNull = false, columnName = Columns.TEST_ID)
    private String testId;

    @DatabaseField(canBeNull = false, columnName = Columns.SCRIPT_ID)
    private String campaignId;

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

    @SuppressWarnings("unused")
    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    @SuppressWarnings("unused")
    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    @Override
    public String toString() {
        return "TestRecord{" +
                "testId='" + testId + '\'' +
                ", campaignId='" + campaignId + '\'' +
                ", score=" + score +
                '}';
    }
}
