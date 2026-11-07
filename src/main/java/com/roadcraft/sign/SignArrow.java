package com.roadcraft.sign;

/** Direction of the arrow shown next to a sign row. Angle is counter-clockwise from "up". */
public enum SignArrow {
    NONE("none", 0F),
    UP("up", 0F),
    UP_RIGHT("up-right", -45F),
    RIGHT("right", -90F),
    DOWN_RIGHT("down-right", -135F),
    DOWN("down", 180F),
    DOWN_LEFT("down-left", 135F),
    LEFT("left", 90F),
    UP_LEFT("up-left", 45F);

    private final String label;
    public final float angle;

    SignArrow(String label, float angle) {
        this.label = label;
        this.angle = angle;
    }

    public String label() {
        return label;
    }

    /** Arrows that point left are drawn on the left side of the row, all others on the right. */
    public boolean pointsLeft() {
        return this == LEFT || this == UP_LEFT || this == DOWN_LEFT;
    }

    public SignArrow next() {
        SignArrow[] all = values();
        return all[(ordinal() + 1) % all.length];
    }

    public static SignArrow byId(int id) {
        SignArrow[] all = values();
        return id >= 0 && id < all.length ? all[id] : NONE;
    }
}
