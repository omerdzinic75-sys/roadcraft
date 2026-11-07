package com.roadcraft.blockentity;

import com.roadcraft.registry.ModBlockEntities;
import com.roadcraft.sign.SignArrow;
import com.roadcraft.sign.SignStyle;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class RoadSignBlockEntity extends BlockEntity {
    public static final int ROWS = 4;
    public static final int MAX_TEXT = 32;
    public static final int MAX_WIDTH = 8;
    public static final int MAX_HEIGHT = 4;

    /**
     * Fonts the editor can cycle through. Add your own font ids at the END of this list
     * (see README: "Adding a custom font").
     */
    public static final String[] FONTS = {
            "minecraft:default",
            "minecraft:uniform",
            "minecraft:alt",
            "minecraft:illageralt"
    };

    private final String[] text = {"City name", "", "", ""};
    private final SignArrow[] arrow = {SignArrow.UP, SignArrow.NONE, SignArrow.NONE, SignArrow.NONE};
    private int fontIndex = 0;
    private int style = 0;
    private int width = 3;
    private int height = 1;
    private int textPercent = 100;
    private ResourceLocation fontId = new ResourceLocation(FONTS[0]);

    public RoadSignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROAD_SIGN.get(), pos, state);
    }

    // ---- accessors -------------------------------------------------------------------------

    public String getText(int row) { return text[row]; }
    public SignArrow getArrow(int row) { return arrow[row]; }
    public int getFontIndex() { return fontIndex; }
    public ResourceLocation getFontId() { return fontId; }
    public int getStyleIndex() { return style; }
    public SignStyle getStyle() { return SignStyle.byId(style); }
    public int getWidthBlocks() { return width; }
    public int getHeightBlocks() { return height; }
    public int getTextPercent() { return textPercent; }

    // ---- NBT -------------------------------------------------------------------------------

    /** Builds the tag format used for saving, networking and item presets. */
    public static CompoundTag buildTag(String[] text, SignArrow[] arrows, int font, int style, int w, int h, int pct) {
        CompoundTag tag = new CompoundTag();
        for (int i = 0; i < ROWS; i++) {
            tag.putString("t" + i, text[i] == null ? "" : text[i]);
            tag.putInt("a" + i, arrows[i].ordinal());
        }
        tag.putInt("font", font);
        tag.putInt("style", style);
        tag.putInt("w", w);
        tag.putInt("h", h);
        tag.putInt("pct", pct);
        return tag;
    }

    private static String clean(String s) {
        s = s.replaceAll("\\p{Cntrl}", "");
        return s.length() > MAX_TEXT ? s.substring(0, MAX_TEXT) : s;
    }

    /** Reads (and validates) settings. Missing keys keep their current value. */
    public void read(CompoundTag tag) {
        for (int i = 0; i < ROWS; i++) {
            if (tag.contains("t" + i)) text[i] = clean(tag.getString("t" + i));
            if (tag.contains("a" + i)) arrow[i] = SignArrow.byId(tag.getInt("a" + i));
        }
        if (tag.contains("font")) fontIndex = Mth.clamp(tag.getInt("font"), 0, FONTS.length - 1);
        if (tag.contains("style")) style = Mth.clamp(tag.getInt("style"), 0, SignStyle.values().length - 1);
        if (tag.contains("w")) width = Mth.clamp(tag.getInt("w"), 1, MAX_WIDTH);
        if (tag.contains("h")) height = Mth.clamp(tag.getInt("h"), 1, MAX_HEIGHT);
        if (tag.contains("pct")) textPercent = Mth.clamp(tag.getInt("pct"), 30, 100);
        fontId = new ResourceLocation(FONTS[fontIndex]);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.merge(buildTag(text, arrow, fontIndex, style, width, height, textPercent));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        read(tag);
    }

    /** Called on the server when a player confirms the edit screen. */
    public void applySettings(CompoundTag tag) {
        read(tag);
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // ---- client sync -----------------------------------------------------------------------

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /** Wide signs are drawn outside their own block, so the renderer needs a bigger box. */
    @Override
    public AABB getRenderBoundingBox() {
        double r = Math.max(width, height) / 2.0 + 1.0;
        return new AABB(worldPosition).inflate(r);
    }
}
