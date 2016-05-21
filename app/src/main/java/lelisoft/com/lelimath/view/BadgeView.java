package lelisoft.com.lelimath.view;

import lelisoft.com.lelimath.data.Badge;

/**
 * Fragment data holder for badges decorated with information if it was awarded or not.
 * Created by Leoš on 21.05.2016.
 */
public class BadgeView {
    public Badge badge;
    public boolean awarded;

    public BadgeView(Badge badge) {
        this.badge = badge;
    }
}
