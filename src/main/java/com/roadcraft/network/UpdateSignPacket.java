package com.roadcraft.network;

import com.roadcraft.blockentity.RoadSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Client -> server: "I edited the sign at this position, here are the new settings". */
public class UpdateSignPacket {
    private final BlockPos pos;
    private final CompoundTag tag;

    public UpdateSignPacket(BlockPos pos, CompoundTag tag) {
        this.pos = pos;
        this.tag = tag;
    }

    public static void encode(UpdateSignPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeNbt(msg.tag);
    }

    public static UpdateSignPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        CompoundTag tag = buf.readNbt();
        return new UpdateSignPacket(pos, tag == null ? new CompoundTag() : tag);
    }

    public static void handle(UpdateSignPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context c = ctx.get();
        c.enqueueWork(() -> {
            ServerPlayer player = c.getSender();
            if (player == null || !player.mayBuild()) return;
            Level level = player.level;
            if (!level.hasChunkAt(msg.pos)) return;
            if (player.distanceToSqr(Vec3.atCenterOf(msg.pos)) > 64.0D) return;
            if (level.getBlockEntity(msg.pos) instanceof RoadSignBlockEntity sign) {
                sign.applySettings(msg.tag);
            }
        });
        c.setPacketHandled(true);
    }
}
