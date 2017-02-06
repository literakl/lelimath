package lelisoft.com.lelimath.helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.LevelEndEvent;
import com.crashlytics.android.answers.LevelStartEvent;

import lelisoft.com.lelimath.BuildConfig;
import lelisoft.com.lelimath.data.Game;

/**
 * Answers helper class
 * Created by Leoš on 07.06.2016.
 */
public class Metrics {

    public static void saveGameStarted(Game game) {
        if (BuildConfig.DEBUG)
            return;
        Answers.getInstance().logLevelStart(new LevelStartEvent().putLevelName(game.name()));
    }

    public static void saveGameFinished(Game game) {
        if (BuildConfig.DEBUG)
            return;
        Answers.getInstance().logLevelEnd(new LevelEndEvent().putLevelName(game.name()));
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

    public static void saveFigureDressed(@NonNull String figure, @NonNull String part) {
        if (BuildConfig.DEBUG)
            return;
        CustomEvent event = new CustomEvent("Dress")
                .putCustomAttribute("figure", figure)
                .putCustomAttribute("part", part);
        Answers.getInstance().logCustom(event);
    }

    public static void saveCampaignsDisplayed() {
        if (BuildConfig.DEBUG)
            return;
        CustomEvent event = new CustomEvent("Campaign");
        Answers.getInstance().logCustom(event);
    }

    public static void saveCampaignPlayed(String  id) {
        if (BuildConfig.DEBUG)
            return;
        CustomEvent event = new CustomEvent("Test").putCustomAttribute("id", id);
        Answers.getInstance().logCustom(event);
    }
}
