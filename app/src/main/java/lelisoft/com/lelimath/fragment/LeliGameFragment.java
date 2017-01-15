package lelisoft.com.lelimath.fragment;

import lelisoft.com.lelimath.data.Play;
import lelisoft.com.lelimath.data.PlayRecord;

/**
 * Ancestor for games.
 * Created by Leoš on 15.01.2017.
 */

public class LeliGameFragment extends LeliBaseFragment {

    public interface GameBridge {
        void gameFinished();
        void savePlayRecord(Play play, PlayRecord record);
    }
}
