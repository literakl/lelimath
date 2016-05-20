package lelisoft.com.lelimath.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import lelisoft.com.lelimath.logic.GameLogic;

/**
 * Holder for one play of same game
 * Created by Leoš on 13.05.2016.
 */
@DatabaseTable(tableName = "play")
public class Play implements Parcelable {
    @DatabaseField(generatedId = true)
    Integer id;

    @DatabaseField(canBeNull = false)
    Date date;

    @DatabaseField(canBeNull = false, foreign = true, columnName="user_id")
    User user;

    @DatabaseField(persisted=false)
    Game game;

    @DatabaseField(canBeNull=false, columnName="game", width = 2)
    String gameStr;

    public static final String LEVEL_COLUMN_NAME = "level";
    @DatabaseField(canBeNull = false, columnName = LEVEL_COLUMN_NAME)
    int level;

    @DatabaseField(canBeNull = false)
    int count;

    public static final String FINISHED_COLUMN_NAME = "finished";
    @DatabaseField(canBeNull = false, columnName = FINISHED_COLUMN_NAME)
    boolean finished = false;

    @DatabaseField(canBeNull=true, columnName="spent")
    Long timeSpent;

    public Play() {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
    public GameLogic.Level getLevel() {
        switch (level) {
            case 0:
                return GameLogic.Level.TRIVIAL;
            case 3:
                return GameLogic.Level.EASY;
            case 6:
                return GameLogic.Level.NORMAL;
            case 9:
                return GameLogic.Level.HARD;
            case 12:
                return GameLogic.Level.GENIUS;
        }
        return null;
    }

    public void setLevel(GameLogic.Level level) {
        switch (level) {
            case TRIVIAL:
                this.level = 0; break;
            case EASY:
                this.level = 3; break;
            case NORMAL:
                this.level = 6; break;
            case HARD:
                this.level = 9; break;
            case GENIUS:
                this.level = 12; break;
        }
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

    public Long getTimeSpent() {
        return timeSpent;
    }

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
        timeSpent = in.readLong();
        gameStr = in.readString();
        finished = in.readByte() != 0;
        date = new Date(in.readLong());
        user = new User(in.readInt());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(count);
        dest.writeLong(timeSpent);
        dest.writeString(gameStr);
        dest.writeByte((byte) (finished ? 1 : 0));
        dest.writeLong(date.getTime());
        dest.writeInt(user.getId());
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
