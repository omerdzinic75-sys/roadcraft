package com.roadcraft.client;

import com.roadcraft.blockentity.RoadSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

/** Client-only helpers; only ever referenced from inside DistExecutor lambdas. */
public class ClientHooks {
    public static void openSignScreen(BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.level.getBlockEntity(pos) instanceof RoadSignBlockEntity sign) {
            mc.setScreen(new RoadSignEditScreen(sign));
        }
    }
}
