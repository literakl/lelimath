package lelisoft.com.lelimath.event;

import lelisoft.com.lelimath.view.DressPart;

/**
 * DressPart was selected and confirmed by a user.
 * Created by Leoš on 03.09.2016.
 */
public class DressPartBoughtEvent {
    DressPart part;

    public DressPartBoughtEvent(DressPart part) {
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
