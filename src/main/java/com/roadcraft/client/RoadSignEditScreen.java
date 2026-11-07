package com.roadcraft.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.roadcraft.blockentity.RoadSignBlockEntity;
import com.roadcraft.network.PacketHandler;
import com.roadcraft.network.UpdateSignPacket;
import com.roadcraft.sign.SignArrow;
import com.roadcraft.sign.SignStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/**
 * Sign editor: one text line + arrow per row, colour style, font, width, height and text size.
 * Closing the screen (Done or Esc) saves.
 */
public class RoadSignEditScreen extends Screen {
    private final RoadSignBlockEntity sign;
    private final String[] texts = new String[RoadSignBlockEntity.ROWS];
    private final SignArrow[] arrows = new SignArrow[RoadSignBlockEntity.ROWS];
    private final EditBox[] boxes = new EditBox[RoadSignBlockEntity.ROWS];
    private int fontIndex, style, widthBlocks, heightBlocks, percent;

    public RoadSignEditScreen(RoadSignBlockEntity sign) {
        super(Component.translatable("screen.roadcraft.edit_sign"));
        this.sign = sign;
        for (int i = 0; i < RoadSignBlockEntity.ROWS; i++) {
            texts[i] = sign.getText(i);
            arrows[i] = sign.getArrow(i);
        }
        fontIndex = sign.getFontIndex();
        style = sign.getStyleIndex();
        widthBlocks = sign.getWidthBlocks();
        heightBlocks = sign.getHeightBlocks();
        percent = sign.getTextPercent();
    }

    private Component arrowLabel(int row) {
        return Component.literal("Arrow: " + arrows[row].label());
    }

    private Component styleLabel() {
        return Component.literal("Style: " + SignStyle.byId(style).label);
    }

    private Component fontLabel() {
        String id = RoadSignBlockEntity.FONTS[fontIndex];
        return Component.literal("Font: " + id.substring(id.indexOf(':') + 1));
    }

    private Component widthLabel() {
        return Component.literal("Width: " + widthBlocks);
    }

    private Component heightLabel() {
        return Component.literal("Height: " + heightBlocks);
    }

    private Component percentLabel() {
        return Component.literal("Text: " + percent + "%");
    }

    @Override
    protected void init() {
        minecraft.keyboardHandler.setSendRepeatsToGui(true);
        int cx = this.width / 2;
        int top = 36;

        for (int i = 0; i < RoadSignBlockEntity.ROWS; i++) {
            final int row = i;
            EditBox box = new EditBox(this.font, cx - 150, top + i * 24, 190, 20, Component.literal("Line " + (i + 1)));
            box.setMaxLength(RoadSignBlockEntity.MAX_TEXT);
            box.setValue(texts[i]);
            boxes[i] = addRenderableWidget(box);

            addRenderableWidget(new Button(cx + 46, top + i * 24, 104, 20, arrowLabel(i), b -> {
                arrows[row] = arrows[row].next();
                b.setMessage(arrowLabel(row));
            }));
        }

        int y = top + RoadSignBlockEntity.ROWS * 24 + 8;
        addRenderableWidget(new Button(cx - 150, y, 148, 20, styleLabel(), b -> {
            style = (style + 1) % SignStyle.values().length;
            b.setMessage(styleLabel());
        }));
        addRenderableWidget(new Button(cx + 2, y, 148, 20, fontLabel(), b -> {
            fontIndex = (fontIndex + 1) % RoadSignBlockEntity.FONTS.length;
            b.setMessage(fontLabel());
        }));

        y += 24;
        addRenderableWidget(new Button(cx - 150, y, 98, 20, widthLabel(), b -> {
            widthBlocks = widthBlocks % RoadSignBlockEntity.MAX_WIDTH + 1;
            b.setMessage(widthLabel());
        }));
        addRenderableWidget(new Button(cx - 49, y, 98, 20, heightLabel(), b -> {
            heightBlocks = heightBlocks % RoadSignBlockEntity.MAX_HEIGHT + 1;
            b.setMessage(heightLabel());
        }));
        addRenderableWidget(new Button(cx + 52, y, 98, 20, percentLabel(), b -> {
            percent += 10;
            if (percent > 100) percent = 30;
            b.setMessage(percentLabel());
        }));

        y += 30;
        addRenderableWidget(new Button(cx - 100, y, 200, 20, CommonComponents.GUI_DONE, b -> onClose()));

        setInitialFocus(boxes[0]);
    }

    private void captureTexts() {
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i] != null) texts[i] = boxes[i].getValue();
        }
    }

    @Override
    public void resize(Minecraft mc, int w, int h) {
        captureTexts();
        super.resize(mc, w, h);
    }

    @Override
    public void tick() {
        for (EditBox box : boxes) {
            if (box != null) box.tick();
        }
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        renderBackground(pose);
        drawCenteredString(pose, this.font, this.title, this.width / 2, 14, 0xFFFFFF);
        super.render(pose, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        minecraft.keyboardHandler.setSendRepeatsToGui(false);
        super.removed();
    }

    @Override
    public void onClose() {
        captureTexts();
        CompoundTag tag = RoadSignBlockEntity.buildTag(texts, arrows, fontIndex, style, widthBlocks, heightBlocks, percent);
        PacketHandler.CHANNEL.sendToServer(new UpdateSignPacket(sign.getBlockPos(), tag));
        sign.read(tag); // instant local feedback; the server then syncs the real value
        super.onClose();
    }
}
