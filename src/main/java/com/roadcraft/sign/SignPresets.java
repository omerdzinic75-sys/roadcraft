package com.roadcraft.sign;

import com.roadcraft.blockentity.RoadSignBlockEntity;
import com.roadcraft.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** Ready-made signs shown in the creative tab. Every value can be changed after placing. */
public class SignPresets {

    private static ItemStack make(String name, SignStyle style, int w, int h, String[] text, SignArrow[] arrows) {
        CompoundTag be = RoadSignBlockEntity.buildTag(text, arrows, 0, style.ordinal(), w, h, 100);
        ItemStack stack = new ItemStack(ModItems.ROAD_SIGN.get());
        stack.getOrCreateTag().put("BlockEntityTag", be);
        stack.setHoverName(Component.literal(name).withStyle(s -> s.withItalic(false)));
        return stack;
    }

    private static String[] rows(String... t) {
        String[] r = {"", "", "", ""};
        for (int i = 0; i < t.length && i < r.length; i++) r[i] = t[i];
        return r;
    }

    private static SignArrow[] arrows(SignArrow... a) {
        SignArrow[] r = {SignArrow.NONE, SignArrow.NONE, SignArrow.NONE, SignArrow.NONE};
        for (int i = 0; i < a.length && i < r.length; i++) r[i] = a[i];
        return r;
    }

    public static ItemStack highway() {
        return make("Highway Sign", SignStyle.HIGHWAY, 5, 2,
                rows("Sarajevo", "Mostar"), arrows(SignArrow.LEFT, SignArrow.RIGHT));
    }

    public static ItemStack exit() {
        return make("Highway Exit Sign", SignStyle.HIGHWAY, 3, 1,
                rows("Exit 12"), arrows(SignArrow.DOWN_RIGHT));
    }

    public static ItemStack city() {
        return make("City Direction Sign", SignStyle.CITY, 3, 2,
                rows("Centar", "Airport"), arrows(SignArrow.UP, SignArrow.RIGHT));
    }

    public static ItemStack cityLimit() {
        return make("City Limit Sign", SignStyle.CITY_LIMIT, 3, 1,
                rows("Sarajevo"), arrows());
    }

    public static ItemStack gasStation() {
        return make("Gas Station Sign", SignStyle.GAS, 3, 1,
                rows("Gas Station"), arrows(SignArrow.RIGHT));
    }
}
