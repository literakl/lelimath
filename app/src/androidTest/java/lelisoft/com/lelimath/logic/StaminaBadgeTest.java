package lelisoft.com.lelimath.logic;

import android.annotation.SuppressLint;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.test.AndroidTestCase;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lelisoft.com.lelimath.data.Badge;
import lelisoft.com.lelimath.data.BadgeAward;
import lelisoft.com.lelimath.data.BadgeEvaluation;
import lelisoft.com.lelimath.data.Game;
import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.PlayRecord;
import lelisoft.com.lelimath.helpers.LeliMathApp;
import lelisoft.com.lelimath.helpers.Misc;
import lelisoft.com.lelimath.logic.badges.StaminaBadgeEvaluator;
import lelisoft.com.lelimath.provider.DatabaseHelper;
import lelisoft.com.lelimath.view.AwardedBadgesCount;

/**
 * Tests stamina badge evaluator
 * Created by Leoš on 29.05.2016.
 */
public class StaminaBadgeTest extends AndroidTestCase {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LeliMathApp.class);

    DatabaseHelper helper;

    public void testSilverBadge() throws Exception {
        Dao<Play, Integer> playDao = helper.getPlayDao();
        Dao<PlayRecord, Integer> recordDao = helper.getPlayRecordDao();
        BadgeEvaluator evaluator = new StaminaBadgeEvaluator();
        Map<Badge, List<BadgeAward>> badges = new HashMap<>();
        badges.put(Badge.RETURNER, Collections.singletonList(new BadgeAward()));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 16);

        PlayRecord record = null;
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.MINUTE, -10);
            Date time = calendar.getTime();
            Play play = new Play(Game.PUZZLE, GameLogic.Level.EASY, 10, true, 3000L, time);
            playDao.create(play);
            for (int j = 0; j < play.getCount(); j++) {
                calendar.add(Calendar.MINUTE, 1);
                time = calendar.getTime();
                record = new PlayRecord(play, time, true, 600L);
                recordDao.create(record);
            }
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        assert record != null;
        record.setCorrect(false);
        recordDao.update(record);

        AwardedBadgesCount result = evaluator.evaluate(badges, getContext());
        assertEquals(0, result.bronze);
        assertEquals(0, result.silver);
        assertEquals(0, result.gold);

        record.setCorrect(true);
        recordDao.update(record);

        result = evaluator.evaluate(badges, getContext());
        assertEquals(0, result.bronze);
        assertEquals(1, result.silver);
        assertEquals(0, result.gold);
    }

/*
1000 plays * 50 records. Generating took 7 minutes, evaluation took 180 ms

06-04 07:47:13.491 31553-31566/lelisoft.com.lelimath I/TestRunner: started: testGoldBadge(lelisoft.com.lelimath.logic.StaminaBadgeTest)
06-04 07:47:13.566 31553-31566/lelisoft.com.lelimath I/TableUtils: clearing table 'badge_eval' with 'DELETE FROM `badge_eval`
06-04 07:54:21.641 31553-31566/lelisoft.com.lelimath D/l.c.l.l.b.StaminaBadgeEvaluator: evaluate starts
06-04 07:54:21.756 31553-31566/lelisoft.com.lelimath D/l.c.l.l.b.StaminaBadgeEvaluator: Starting date is Fri Feb 26 00:00:00 GMT+01:00 2016
06-04 07:54:21.827 31553-31566/lelisoft.com.lelimath D/l.c.l.l.b.StaminaBadgeEvaluator: Badge MARATHON_RUNNER was awarded
06-04 07:54:21.828 31553-31566/lelisoft.com.lelimath D/l.c.l.l.b.StaminaBadgeEvaluator: evaluate finished: AwardedBadgesCount{bronze=0, silver=0, gold=1}
 */
    public void testGoldBadge() throws Exception {
        Dao<Play, Integer> playDao = helper.getPlayDao();
        Dao<PlayRecord, Integer> recordDao = helper.getPlayRecordDao();
        BadgeEvaluator evaluator = new StaminaBadgeEvaluator();
        Map<Badge, List<BadgeAward>> badges = new HashMap<>();
        badges.put(Badge.RETURNER, Collections.singletonList(new BadgeAward()));
        badges.put(Badge.LONG_DISTANCE_RUNNER, Collections.singletonList(new BadgeAward()));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 0);

        for (int i = 0; i < 30; i++) {
            calendar.add(Calendar.MINUTE, -10);
            Date time = calendar.getTime();
            Play play = new Play(Game.PUZZLE, GameLogic.Level.EASY, 10, true, 3000L, time);
            playDao.create(play);
            for (int j = 0; j < play.getCount(); j++) {
                calendar.add(Calendar.MINUTE, 1);
                time = calendar.getTime();
                PlayRecord record = new PlayRecord(play, time, true, 600L);
                recordDao.create(record);
            }
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        AwardedBadgesCount result = evaluator.evaluate(badges, getContext());
        assertEquals(0, result.bronze);
        assertEquals(0, result.silver);
        assertEquals(1, result.gold);
    }

    public void testBronzeBadge() throws Exception {
        Dao<Play, Integer> playDao = helper.getPlayDao();
        Dao<PlayRecord, Integer> recordDao = helper.getPlayRecordDao();
        Calendar calendar = Calendar.getInstance();
        BadgeEvaluator evaluator = new StaminaBadgeEvaluator();
        Map<Badge, List<BadgeAward>> badges = new HashMap<>();

        calendar.add(Calendar.MINUTE, -5);
        Date time = calendar.getTime();
        Play play = new Play(Game.PUZZLE, GameLogic.Level.EASY, 5, true, 3000L, time);
        playDao.create(play);
        for (int i = 0; i < play.getCount(); i++) {
            PlayRecord record = new PlayRecord(play, time, true, 600L);
            recordDao.create(record);
        }

        calendar.add(Calendar.MINUTE, +2);
        time = calendar.getTime();
        play = new Play(Game.PUZZLE, GameLogic.Level.EASY, 5, true, 2000L, time);
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
        play = new Play(Game.PUZZLE, GameLogic.Level.EASY, 8, true, 3000L, time);
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
        play = new Play(Game.PUZZLE, GameLogic.Level.EASY, 5, true, 2000L, time);
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

        TableUtils.clearTable(helper.getConnectionSource(), Play.class);
        TableUtils.clearTable(helper.getConnectionSource(), PlayRecord.class);
        TableUtils.clearTable(helper.getConnectionSource(), BadgeAward.class);
        TableUtils.clearTable(helper.getConnectionSource(), BadgeEvaluation.class);
    }

    @SuppressLint("SetWorldReadable")
    @Override
    protected void tearDown() throws Exception {
        File in = DatabaseHelper.getDatabasePath();
        File out = new File(Environment.getExternalStorageDirectory(), in.getName());
        //noinspection ResultOfMethodCallIgnored
        out.setReadable(true, false);
        Misc.copyFile(in, out);
        MediaScannerConnection.scanFile(getContext(), new String[]{out.getAbsolutePath()}, null, null);
    }
}
