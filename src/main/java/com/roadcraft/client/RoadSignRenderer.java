package com.roadcraft.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.roadcraft.block.RoadSignBlock;
import com.roadcraft.blockentity.RoadSignBlockEntity;
import com.roadcraft.sign.SignArrow;
import com.roadcraft.sign.SignStyle;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws the sign face: border, background colour, arrows and text.
 * Local coordinates: origin = centre of the sign, +X = viewer's right, +Y = up,
 * +Z = out of the sign face (toward the viewer). 1 unit = 1 block.
 */
public class RoadSignRenderer implements BlockEntityRenderer<RoadSignBlockEntity> {
    private static final float BORDER = 0.04F;
    private static final float PAD = 0.06F;

    private final Font font;

    public RoadSignRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public int getViewDistance() {
        return 96;
    }

    private record Row(int index, float cy, float textLeft, float textRight) {}

    @Override
    public void render(RoadSignBlockEntity be, float partialTick, PoseStack pose,
                       MultiBufferSource buffers, int light, int overlay) {
        BlockState state = be.getBlockState();
        if (!state.hasProperty(RoadSignBlock.FACING)) return;

        Direction facing = state.getValue(RoadSignBlock.FACING);
        SignStyle style = be.getStyle();
        float w = be.getWidthBlocks();
        float h = be.getHeightBlocks();
        float x0 = -w / 2F, x1 = w / 2F, y0 = -h / 2F, y1 = h / 2F;

        pose.pushPose();
        pose.translate(0.5D, 0.5D, 0.5D);
        pose.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
        pose.translate(0.0D, 0.0D, 0.25D); // front plane of the plate

        VertexConsumer vc = buffers.getBuffer(ModRenderTypes.SIGN_FLAT);
        Matrix4f m = pose.last().pose();

        // metal back, then border, then coloured face
        rect(vc, m, x0, y0, x1, y1, -0.126F, 0x6E6E6E);
        rect(vc, m, x0, y0, x1, y1, 0.002F, style.border);
        rect(vc, m, x0 + BORDER, y0 + BORDER, x1 - BORDER, y1 - BORDER, 0.004F, style.bg);

        // lay out the rows that have text or an arrow
        List<Integer> used = new ArrayList<>();
        for (int i = 0; i < RoadSignBlockEntity.ROWS; i++) {
            if (!be.getText(i).isEmpty() || be.getArrow(i) != SignArrow.NONE) used.add(i);
        }

        List<Row> rows = new ArrayList<>();
        if (!used.isEmpty()) {
            float edge = BORDER + PAD;
            float left = x0 + edge, right = x1 - edge, top = y1 - edge, bottom = y0 + edge;
            float rowH = (top - bottom) / used.size();

            for (int k = 0; k < used.size(); k++) {
                int i = used.get(k);
                float cy = top - rowH * (k + 0.5F);
                SignArrow arrow = be.getArrow(i);
                float tl = left, tr = right;
                if (arrow != SignArrow.NONE) {
                    float size = Math.min(rowH * 0.85F, w * 0.4F);
                    boolean leftSide = arrow.pointsLeft();
                    float ax = leftSide ? left + size / 2F : right - size / 2F;
                    drawArrow(vc, pose, ax, cy, size, arrow, style.fg);
                    if (leftSide) tl += size + 0.05F; else tr -= size + 0.05F;
                }
                rows.add(new Row(i, cy, tl, tr));
            }
        }

        // text last (a different render type, so keep it in its own pass)
        for (Row row : rows) {
            drawText(be, pose, buffers, be.getText(row.index()), row.textLeft(), row.textRight(),
                    row.cy(), (y1 - y0 - 2 * (BORDER + PAD)) / rows.size(), style.fg);
        }

        pose.popPose();
    }

    private void drawText(RoadSignBlockEntity be, PoseStack pose, MultiBufferSource buffers, String txt,
                          float tl, float tr, float cy, float rowH, int rgb) {
        if (txt.isEmpty()) return;
        FormattedCharSequence seq = Component.literal(txt)
                .withStyle(Style.EMPTY.withFont(be.getFontId()))
                .getVisualOrderText();
        int tw = font.width(seq);
        if (tw <= 0) return;

        float avail = tr - tl;
        float fit = Math.min(rowH * 0.62F / 8F, avail / tw);   // biggest scale that fits the row
        float scale = fit * be.getTextPercent() / 100F;

        pose.pushPose();
        pose.translate((tl + tr) / 2F, cy, 0.008D);
        pose.scale(scale, -scale, scale);
        font.drawInBatch(seq, -tw / 2F, -4F, 0xFF000000 | rgb, false, pose.last().pose(),
                buffers, false, 0, LightTexture.FULL_BRIGHT);
        pose.popPose();
    }

    private static void drawArrow(VertexConsumer vc, PoseStack pose, float cx, float cy, float size,
                                  SignArrow arrow, int rgb) {
        pose.pushPose();
        pose.translate(cx, cy, 0.006D);
        pose.mulPose(Vector3f.ZP.rotationDegrees(arrow.angle));
        pose.scale(size / 2F, size / 2F, 1F);
        Matrix4f m = pose.last().pose();
        rect(vc, m, -0.14F, -0.9F, 0.14F, 0.2F, 0F, rgb);                        // shaft
        tri(vc, m, -0.62F, 0.15F, 0.62F, 0.15F, 0F, 0.95F, 0F, rgb);              // head
        pose.popPose();
    }

    private static void rect(VertexConsumer vc, Matrix4f m, float x0, float y0, float x1, float y1, float z, int rgb) {
        int r = (rgb >> 16) & 255, g = (rgb >> 8) & 255, b = rgb & 255;
        vc.vertex(m, x0, y0, z).color(r, g, b, 255).endVertex();
        vc.vertex(m, x1, y0, z).color(r, g, b, 255).endVertex();
        vc.vertex(m, x1, y1, z).color(r, g, b, 255).endVertex();
        vc.vertex(m, x0, y1, z).color(r, g, b, 255).endVertex();
    }

    /** Triangle drawn as a degenerate quad. */
    private static void tri(VertexConsumer vc, Matrix4f m, float ax, float ay, float bx, float by,
                            float cx, float cy, float z, int rgb) {
        int r = (rgb >> 16) & 255, g = (rgb >> 8) & 255, b = rgb & 255;
        vc.vertex(m, ax, ay, z).color(r, g, b, 255).endVertex();
        vc.vertex(m, bx, by, z).color(r, g, b, 255).endVertex();
        vc.vertex(m, cx, cy, z).color(r, g, b, 255).endVertex();
        vc.vertex(m, cx, cy, z).color(r, g, b, 255).endVertex();
    }
}
