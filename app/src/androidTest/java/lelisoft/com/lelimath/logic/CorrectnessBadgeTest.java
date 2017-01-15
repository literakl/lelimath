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
import lelisoft.com.lelimath.logic.badges.CorrectnessBadgeEvaluator;
import lelisoft.com.lelimath.provider.DatabaseHelper;
import lelisoft.com.lelimath.view.AwardedBadgesCount;

import static lelisoft.com.lelimath.data.FormulaPart.*;
import static lelisoft.com.lelimath.data.Operator.*;

/**
 * Tests rows of correct formulas.
 * Created by Leoš on 04.06.2016.
 */
public class CorrectnessBadgeTest extends AndroidTestCase {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LeliMathApp.class);

    DatabaseHelper helper;

    public void testPlus() throws Exception {
        Dao<Play, Integer> playDao = helper.getPlayDao();
        Dao<PlayRecord, Integer> recordDao = helper.getPlayRecordDao();
        BadgeEvaluator evaluator = new CorrectnessBadgeEvaluator();
        Map<Badge, List<BadgeAward>> badges = new HashMap<>();

        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, 16);
        time.set(Calendar.MINUTE, 0);

        Play play = new Play(Game.FAST_CALC, Level.HARD, 10, true, 3000L, time.getTime());
        playDao.create(play);
        for (int i = 0; i < 8; i++) {
            PlayRecord record = new PlayRecord(play, time.getTime(), true, 600L);
            recordDao.create(record.setFormula(1, PLUS, i, 1 + i, FIRST_OPERAND));
            record = new PlayRecord(play, time.getTime(), true, 600L);
            recordDao.create(record.setFormula(i, MINUS, 0, i, RESULT)); // ignored
        }

        // insert duplicate formulas
        PlayRecord recordDup1 = new PlayRecord(play, time.getTime(), true, 600L);
        recordDao.create(recordDup1.setFormula(1, PLUS, 1, 1, RESULT));
        PlayRecord recordDup2 = new PlayRecord(play, time.getTime(), true, 600L);
        recordDao.create(recordDup2.setFormula(1, PLUS, 2, 3, RESULT));

        AwardedBadgesCount result = evaluator.evaluate(getContext());
        assertEquals(0, result.bronze);
        assertEquals(0, result.silver);
        assertEquals(0, result.gold);

        // make duplicate formulas unique to match missing pieces
        recordDup1.setSecondOperand(9);
        recordDao.update(recordDup1);
        recordDup2.setSecondOperand(10);
        recordDao.update(recordDup2);

        result = evaluator.evaluate(getContext());
        assertEquals(1, result.bronze);
        assertEquals(0, result.silver);
        assertEquals(0, result.gold);

        for (int i = 11; i < 101; i++) {
            PlayRecord record = new PlayRecord(play, time.getTime(), true, 600L);
            recordDao.create(record.setFormula(1, PLUS, i, 1 + i, FIRST_OPERAND));
        }

        result = evaluator.evaluate(getContext());
        assertEquals(0, result.bronze);
        assertEquals(1, result.silver);
        assertEquals(1, result.gold);

        // break the strike
        PlayRecord badRecord = new PlayRecord(play, time.getTime(), false, 600L);
        recordDao.create(badRecord.setFormula(1, PLUS, 1, 0, RESULT));

        for (int i = 102; i < 203; i++) {
            PlayRecord record = new PlayRecord(play, time.getTime(), true, 600L);
            recordDao.create(record.setFormula(1, PLUS, i, 1 + i, FIRST_OPERAND));
        }

        result = evaluator.evaluate(getContext());
        assertEquals(1, result.bronze);
        assertEquals(1, result.silver);
        assertEquals(1, result.gold);
    }

    public void testMinusMultiplyDivide() throws Exception {
        Dao<Play, Integer> playDao = helper.getPlayDao();
        Dao<PlayRecord, Integer> recordDao = helper.getPlayRecordDao();
        BadgeEvaluator evaluator = new CorrectnessBadgeEvaluator();
        Map<Badge, List<BadgeAward>> badges = new HashMap<>();

        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, 16);
        time.set(Calendar.MINUTE, 0);

        Play play = new Play(Game.FAST_CALC, Level.HARD, 1000, true, 3000L, time.getTime());
        playDao.create(play);
        for (int i = 0; i < 10; i++) {
            PlayRecord record = new PlayRecord(play, time.getTime(), true, 600L);
            recordDao.create(record.setFormula(i, MINUS, 1, i, FIRST_OPERAND));
            record = new PlayRecord(play, time.getTime(), true, 600L);
            recordDao.create(record.setFormula(i, MULTIPLY, 1, i, SECOND_OPERAND));
            record = new PlayRecord(play, time.getTime(), true, 600L);
            recordDao.create(record.setFormula(i, DIVIDE, 1, i, RESULT));
        }

        AwardedBadgesCount result = evaluator.evaluate(getContext());
        assertEquals(3, result.bronze);
        assertEquals(0, result.silver);
        assertEquals(0, result.gold);

        // break the strike
        PlayRecord badRecord = new PlayRecord(play, time.getTime(), false, 600L);
        recordDao.create(badRecord.setFormula(1, MINUS, 1, 0, RESULT));
        badRecord = new PlayRecord(play, time.getTime(), false, 600L);
        recordDao.create(badRecord.setFormula(1, MULTIPLY, 1, 1, RESULT));
        badRecord = new PlayRecord(play, time.getTime(), false, 600L);
        recordDao.create(badRecord.setFormula(1, DIVIDE, 1, 1, RESULT));

        for (int i = 0; i < 26; i++) {
            PlayRecord record = new PlayRecord(play, time.getTime(), true, 600L);
            recordDao.create(record.setFormula(i, MINUS, 1, i, FIRST_OPERAND));
            record = new PlayRecord(play, time.getTime(), true, 600L);
            recordDao.create(record.setFormula(i, MULTIPLY, 1, i, SECOND_OPERAND));
            record = new PlayRecord(play, time.getTime(), true, 600L);
            recordDao.create(record.setFormula(i, DIVIDE, 1, i, RESULT));
        }

        result = evaluator.evaluate(getContext());
        assertEquals(3, result.bronze);
        assertEquals(3, result.silver);
        assertEquals(0, result.gold);

        for (int i = 26; i < 101; i++) {
            PlayRecord record = new PlayRecord(play, time.getTime(), true, 600L);
            recordDao.create(record.setFormula(i, MINUS, 1, i, FIRST_OPERAND));
            record = new PlayRecord(play, time.getTime(), true, 600L);
            recordDao.create(record.setFormula(i, MULTIPLY, 1, i, SECOND_OPERAND));
            record = new PlayRecord(play, time.getTime(), true, 600L);
            recordDao.create(record.setFormula(i, DIVIDE, 1, i, RESULT));
        }

        result = evaluator.evaluate(getContext());
        assertEquals(0, result.bronze);
        assertEquals(0, result.silver);
        assertEquals(3, result.gold);

        for (int i = 101; i < 202; i++) {
            PlayRecord record = new PlayRecord(play, time.getTime(), true, 600L);
            recordDao.create(record.setFormula(i, MINUS, 1, i, FIRST_OPERAND));
            record = new PlayRecord(play, time.getTime(), true, 600L);
            recordDao.create(record.setFormula(i, MULTIPLY, 1, i, SECOND_OPERAND));
            record = new PlayRecord(play, time.getTime(), true, 600L);
            recordDao.create(record.setFormula(i, DIVIDE, 1, i, RESULT));
        }

        result = evaluator.evaluate(getContext());
        assertEquals(0, result.bronze);
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
