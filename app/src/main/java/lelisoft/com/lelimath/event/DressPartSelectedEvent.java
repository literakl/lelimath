package lelisoft.com.lelimath.event;

import lelisoft.com.lelimath.view.DressPart;

/**
 * Select DressPart event. This is not confirmed yet.
 * Created by Leoš on 03.09.2016.
 */
public class DressPartSelectedEvent {
    DressPart part;
    int position;

    public DressPartSelectedEvent(DressPart part, int position) {
        this.part = part;
        this.position = position;
    }

    public DressPart getPart() {
        return part;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "DressPartBoughtEvent{" +
                "part=" + part.getId() +
                '}';
    }
}
