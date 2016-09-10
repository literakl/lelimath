package lelisoft.com.lelimath.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lelisoft.com.lelimath.provider.PlayRecordProvider;

/**
 * Utility that handles current points balance.
 * Created by Leoš on 10.09.2016.
 */
public class BalanceHelper {
    private static final Logger log = LoggerFactory.getLogger(BalanceHelper.class);

    public static final String KEY_POINTS_BALANCE = "points.balance";

    SharedPreferences sharedPref;
    boolean readOnly;
    int balance;

    public BalanceHelper(Context context) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        balance = sharedPref.getInt(KEY_POINTS_BALANCE, -1);
        if (balance < 0) {
            initializeBalance(context);
        }
        log.debug("Current points balance: {}", balance);
    }

    public int getBalance() {
        return balance;
    }

    /**
     * Adds specified amoount to current balance and persist it.
     * @param points amount to be modified
     * @return current balance
     */
    public int add(int points) {
        balance += points;
        if (! readOnly) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(KEY_POINTS_BALANCE, balance);
            editor.apply();
        }
        return balance;
    }

    /**
     * For debugging purposes only
     * @param readOnly changes will be not persisted
     * @param balance new balance
     */
    @SuppressWarnings("unused")
    public void setDeveloperMode(boolean readOnly, int balance) {
        this.readOnly = readOnly;
        this.balance = balance;
    }

    private void initializeBalance(Context context) {
        PlayRecordProvider provider = new PlayRecordProvider(context);
        balance = provider.getPoints();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_POINTS_BALANCE, balance);
        editor.apply();
        log.debug("Persisted current balance to: {}", balance);
    }
}
