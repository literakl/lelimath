package lelisoft.com.lelimath.logic;

/**
 * Implementation for Calc game preferences
 * Created by Leoš on 09.04.2016.
 */
public class CalcLogicImpl extends GameLogicImpl implements CalcLogic {
    TimerType timerType;

    @Override
    public TimerType getTimerType() {
        return timerType;
    }

    @Override
    public void setTimerType(TimerType timerType) {
        this.timerType = timerType;
    }
}
