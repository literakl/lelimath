package lelisoft.com.lelimath.logic;

/**
 * Calc game preferences
 * Created by Leoš on 09.04.2016.
 */
public interface CalcLogic extends GameLogic {

    /**
     * Get type of time to be displayed
     * @return type of timer or null if timer shall be not displayed
     */
    TimerType getTimerType();

    /**
     * Sets type of timer to be displayed in an action bar. Use null to disable displaying a timer.
     * @param timerType type
     */
    void setTimerType(TimerType timerType);

    enum TimerType {
        STOP_WATCH, COUNT_DOWN
    }
}
