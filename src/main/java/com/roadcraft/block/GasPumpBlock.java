package com.roadcraft.block;

import com.roadcraft.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Right-click with an empty Fuel Can and pay 1 emerald to get a full one.
 * A full Fuel Can burns for 6400 ticks in a furnace and returns the empty can.
 */
public class GasPumpBlock extends HorizontalDirectionalBlock {
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);

    public GasPumpBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    private static boolean takeEmerald(Player player) {
        if (player.getAbilities().instabuild) return true;
        for (ItemStack s : player.getInventory().items) {
            if (s.is(Items.EMERALD)) {
                s.shrink(1);
                return true;
            }
        }
        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        ItemStack held = player.getItemInHand(hand);
        if (held.is(ModItems.FUEL_CAN_FULL.get())) {
            player.displayClientMessage(Component.translatable("message.roadcraft.can_full"), true);
            return InteractionResult.CONSUME;
        }
        if (!held.is(ModItems.FUEL_CAN.get())) {
            player.displayClientMessage(Component.translatable("message.roadcraft.pump_hint"), true);
            return InteractionResult.CONSUME;
        }
        if (!takeEmerald(player)) {
            player.displayClientMessage(Component.translatable("message.roadcraft.need_emerald"), true);
            return InteractionResult.CONSUME;
        }

        ItemStack full = new ItemStack(ModItems.FUEL_CAN_FULL.get());
        held.shrink(1);
        if (held.isEmpty()) {
            player.setItemInHand(hand, full);
        } else if (!player.getInventory().add(full)) {
            player.drop(full, false);
        }
        level.playSound(null, pos, SoundEvents.BUCKET_FILL_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.CONSUME;
    }
}
