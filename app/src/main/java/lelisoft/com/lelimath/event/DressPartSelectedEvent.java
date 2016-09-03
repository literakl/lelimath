package lelisoft.com.lelimath.event;

import lelisoft.com.lelimath.view.DressPart;

/**
 * Select DressPart event. This is not confirmed yet.
 * Created by Leoš on 03.09.2016.
 */
public class DressPartSelectedEvent {
    DressPart part;

    public DressPartSelectedEvent(DressPart part) {
        this.part = part;
    }

    public DressPart getPart() {
        return part;
    }

    @Override
    public String toString() {
        return "DressPartBoughtEvent{" +
                "part=" + part.getId() +
                '}';
    }
}
