package lelisoft.com.lelimath.helpers;

import android.app.Application;
import android.util.Log;

/**
 * Application handler
 * Created by Leoš on 27.02.2016.
 */
public class LeliMathApp extends Application implements Thread.UncaughtExceptionHandler {
    private static final String logTag = LeliMathApp.class.getSimpleName();

    private Thread.UncaughtExceptionHandler androidExceptionHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        androidExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            Log.e(logTag, "UncaughtExceptionHandler just caught " + ex, ex);
        } catch (Throwable ignored) {
            //..avoid infinite loop
        }

        androidExceptionHandler.uncaughtException(thread, ex);
    }
}
