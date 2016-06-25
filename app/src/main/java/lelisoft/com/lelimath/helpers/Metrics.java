package lelisoft.com.lelimath.helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.LevelEndEvent;
import com.crashlytics.android.answers.LevelStartEvent;

import lelisoft.com.lelimath.BuildConfig;
import lelisoft.com.lelimath.data.Game;
import lelisoft.com.lelimath.logic.GameLogic;

/**
 * Answers helper class
 * Created by Leoš on 07.06.2016.
 */
public class Metrics {

    public static void saveGameStarted(Game game, GameLogic.Level level) {
        if (BuildConfig.DEBUG)
            return;
        Answers.getInstance().logLevelStart(new LevelStartEvent().putLevelName(game.name())
                .putCustomAttribute("level", level.name()));
    }

    public static void saveGameFinished(Game game, GameLogic.Level level) {
        if (BuildConfig.DEBUG)
            return;
        Answers.getInstance().logLevelEnd(new LevelEndEvent().putLevelName(game.name())
                .putCustomAttribute("level", level.name()));
    }

    public static void saveContentDisplayed(@NonNull String type, @Nullable String name) {
        if (BuildConfig.DEBUG)
            return;
        ContentViewEvent event = new ContentViewEvent().putContentType(type);
        if (name != null) {
            event.putContentName(name);
        }
        Answers.getInstance().logContentView(event);
    }
}
