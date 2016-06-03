package lelisoft.com.lelimath.logic;

import android.media.MediaScannerConnection;
import android.os.Environment;
import android.test.AndroidTestCase;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import org.slf4j.LoggerFactory;

import java.io.File;
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
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.helpers.Misc;
import lelisoft.com.lelimath.logic.badges.StaminaBadgeEvaluator;
import lelisoft.com.lelimath.provider.BadgeAwardProvider;
import lelisoft.com.lelimath.provider.DatabaseHelper;
import lelisoft.com.lelimath.view.AwardedBadgesCount;

/**
 * Tests stamina badge evaluator
 * Created by Leoš on 29.05.2016.
 */
public class StaminaBadgeTest extends AndroidTestCase {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LeliMathApp.class);

    DatabaseHelper helper;
    User user = new User(1);

    public void testBronzeBadge() throws Exception {
        Dao<Play, Integer> playDao = helper.getPlayDao();
        Dao<PlayRecord, Integer> recordDao = helper.getPlayRecordDao();
        Calendar calendar = Calendar.getInstance();
        BadgeEvaluator evaluator = new StaminaBadgeEvaluator();
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
            DatabaseHelper.setDatabaseName("test.sqlite");
            helper = new DatabaseHelper(getContext());
            log.debug("Helper path is {}", DatabaseHelper.getDatabasePath());
        }
    }

    @Override
    protected void tearDown() throws Exception {
        File in = DatabaseHelper.getDatabasePath();
        File out = new File(Environment.getExternalStorageDirectory(), in.getName());
        //noinspection ResultOfMethodCallIgnored
        out.setReadable(true, false);
        boolean result = Misc.copyFile(in, out);
        MediaScannerConnection.scanFile(getContext(), new String[]{out.getAbsolutePath()}, null, null);

        TableUtils.clearTable(helper.getConnectionSource(), Play.class);
        TableUtils.clearTable(helper.getConnectionSource(), PlayRecord.class);
        TableUtils.clearTable(helper.getConnectionSource(), BadgeAward.class);
        TableUtils.clearTable(helper.getConnectionSource(), BadgeEvaluation.class);
    }
}
