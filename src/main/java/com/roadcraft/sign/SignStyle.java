package com.roadcraft.sign;

/** Colour schemes for signs (RGB). */
public enum SignStyle {
    HIGHWAY("Highway (green)", 0x0B6B3A, 0xFFFFFF, 0xFFFFFF),
    CITY("City (blue)", 0x1A4FA0, 0xFFFFFF, 0xFFFFFF),
    CITY_LIMIT("City limit (white)", 0xF4F4F4, 0x111111, 0x111111),
    GUIDE("Tourist (brown)", 0x6B3E1F, 0xFFFFFF, 0xFFFFFF),
    WARNING("Warning (yellow)", 0xF2C200, 0x111111, 0x111111),
    GAS("Gas station", 0x1A4FA0, 0xFFFFFF, 0xF2C200);

    public final String label;
    public final int bg;
    public final int fg;
    public final int border;

    SignStyle(String label, int bg, int fg, int border) {
        this.label = label;
        this.bg = bg;
        this.fg = fg;
        this.border = border;
    }

    public static SignStyle byId(int id) {
        SignStyle[] all = values();
        return id >= 0 && id < all.length ? all[id] : HIGHWAY;
    }
}
