package lelisoft.com.lelimath.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import lelisoft.com.lelimath.logic.Level;

import static lelisoft.com.lelimath.logic.Level.*;

/**
 * Holder for one play of same game
 * Created by Leoš on 13.05.2016.
 */
@DatabaseTable(tableName = "play")
public class Play implements Parcelable {
    @DatabaseField(generatedId = true, columnName = Columns.ID)
    Integer id;

    @DatabaseField(canBeNull = false)
    private Date date;

    @DatabaseField(persisted=false)
    private Game game;

    @DatabaseField(canBeNull=false, columnName=Columns.GAME, width = 2)
    private String gameStr;

    @DatabaseField(canBeNull = false, columnName = Columns.LEVEL)
    private int level;

    @DatabaseField(canBeNull = false)
    private int count;

    @DatabaseField(canBeNull = false, columnName = Columns.FINISHED)
    private boolean finished = false;

    @DatabaseField(columnName=Columns.SPENT)
    private Long timeSpent;

    public Play() {
    }

    public Play(Game game, Level level, int count, boolean finished, Long timeSpent, Date date) {
        setLevel(level);
        setGame(game);
        setCount(count);
        setFinished(finished);
        setTimeSpent(timeSpent);
        setDate(date);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Game getGame() {
        if (game != null) {
            return game;
        } else if (gameStr != null) {
            game = Game.getValue(gameStr);
        }
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
        gameStr = game.key;
    }

    /**
     * Level.TRIVIAL = 0, Level.GENIUS = 12
     * @return numeric value for GameLogic.Level
     */
    public Level getLevel() {
        switch (level) {
            case 0:
                return TRIVIAL;
            case 3:
                return Level.EASY;
            case 6:
                return Level.NORMAL;
            case 9:
                return Level.HARD;
            case 12:
                return Level.GENIUS;
        }
        return null; // custom or unknown Levels
    }

    public void setLevel(Level level) {
        if (level.equals(TRIVIAL)) {
            this.level = 0;
            return;
        }
        if (level.equals(EASY)) {
            this.level = 3;
            return;
        }
        if (level.equals(NORMAL)) {
            this.level = 6;
            return;
        }
        if (level.equals(HARD)) {
            this.level = 9;
            return;
        }
        if (level.equals(GENIUS)) {
            this.level = 12;
            return;
        }
        // custom
        this.level = 99;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @SuppressWarnings("unused")
    public Long getTimeSpent() {
        return timeSpent;
    }

    @SuppressWarnings("all")
    public void setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public void addTimeSpent(long timeSpent) {
        if (this.timeSpent == null) {
            this.timeSpent = timeSpent;
        } else {
            this.timeSpent += timeSpent;
        }
    }

    @Override
    public String toString() {
        return "Play{" +
                "id=" + id +
                ", game=" + getGame() +
                ", count=" + count +
                ", finished=" + finished +
                ", timeSpent=" + timeSpent +
                '}';
    }

    protected Play(Parcel in) {
        id = in.readInt();
        count = in.readInt();
        timeSpent = (Long) in.readSerializable();
        gameStr = in.readString();
        finished = in.readByte() != 0;
        date = new Date(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(count);
        dest.writeSerializable(timeSpent);
        dest.writeString(gameStr);
        dest.writeByte((byte) (finished ? 1 : 0));
        dest.writeLong(date.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Play> CREATOR = new Creator<Play>() {
        @Override
        public Play createFromParcel(Parcel in) {
            return new Play(in);
        }

        @Override
        public Play[] newArray(int size) {
            return new Play[size];
        }
    };
}
