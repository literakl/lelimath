package lelisoft.com.lelimath.logic;

import android.test.AndroidTestCase;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.BadgeEvaluation;
import lelisoft.com.lelimath.data.Game;
import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.data.User;
import lelisoft.com.lelimath.logic.badges.PlayCountBadgeEvaluator;
import lelisoft.com.lelimath.provider.BadgeAwardProvider;
import lelisoft.com.lelimath.provider.DatabaseHelper;
import lelisoft.com.lelimath.view.AwardedBadgesCount;

/**
 * Tests stamina badge evaluator
 * Created by Leoš on 29.05.2016.
 */
public class StaminaBadgeTest extends AndroidTestCase {
    DatabaseHelper helper;
    User user = new User(1);

    public void testBronzeBadge() throws Exception {
        Dao<Play, Integer> playDao = helper.getPlayDao();
        Dao<PlayRecord, Integer> recordDao = helper.getPlayRecordDao();
        Calendar calendar = Calendar.getInstance();
        BadgeEvaluator evaluator = new PlayCountBadgeEvaluator();
        BadgeAwardProvider provider = new BadgeAwardProvider(getContext());
        Map<Badge, BadgeAward> badges = provider.getAll();

        calendar.add(Calendar.MINUTE, -5);
        Date time = calendar.getTime();
        Play play = new Play(Game.PUZZLE, GameLogic.Level.EASY, 5, true, 3000L, time, user);
        playDao.create(play);
        for (int i = 0; i < play.getCount(); i++) {
            PlayRecord record = new PlayRecord(play, time, true, 600L);
            recordDao.create(record);
        }

        calendar.add(Calendar.MINUTE, +2);
        time = calendar.getTime();
        play = new Play(Game.PUZZLE, GameLogic.Level.EASY, 5, true, 2000L, time, user);
        playDao.create(play);
        for (int i = 0; i < play.getCount(); i++) {
            PlayRecord record = new PlayRecord(play, time, true, 400L);
            recordDao.create(record);
        }

        AwardedBadgesCount result = evaluator.evaluate(badges, getContext());
        assertEquals(0, result.bronze);
        assertEquals(0, result.silver);
        assertEquals(0, result.gold);

        calendar.add(Calendar.DATE, -1);
        time = calendar.getTime();
        play = new Play(Game.PUZZLE, GameLogic.Level.EASY, 8, true, 3000L, time, user);
        playDao.create(play);
        for (int i = 0; i < play.getCount(); i++) {
            PlayRecord record = new PlayRecord(play, time, true, 600L);
            recordDao.create(record);
        }

        result = evaluator.evaluate(badges, getContext());
        assertEquals(0, result.bronze);
        assertEquals(0, result.silver);
        assertEquals(0, result.gold);

        calendar.add(Calendar.MINUTE, +2);
        time = calendar.getTime();
        play = new Play(Game.PUZZLE, GameLogic.Level.EASY, 5, true, 2000L, time, user);
        playDao.create(play);
        for (int i = 0; i < play.getCount(); i++) {
            PlayRecord record = new PlayRecord(play, time, true, 400L);
            recordDao.create(record);
        }

        result = evaluator.evaluate(badges, getContext());
        assertEquals(1, result.bronze);
        assertEquals(0, result.silver);
        assertEquals(0, result.gold);
    }

    @Override
    protected void setUp() throws Exception {
        if (helper == null) {
            helper = new DatabaseHelper(getContext(), "test.sqlite");
        }
    }

    @Override
    protected void tearDown() throws Exception {
        TableUtils.clearTable(helper.getConnectionSource(), User.class);
        TableUtils.clearTable(helper.getConnectionSource(), Play.class);
        TableUtils.clearTable(helper.getConnectionSource(), PlayRecord.class);
        TableUtils.clearTable(helper.getConnectionSource(), BadgeAward.class);
        TableUtils.clearTable(helper.getConnectionSource(), BadgeEvaluation.class);
    }
}
