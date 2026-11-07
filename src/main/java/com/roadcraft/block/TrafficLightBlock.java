package com.roadcraft.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Automatic traffic light: red (10 s) -> green (8 s) -> yellow (2 s) -> red ...
 * Right-click to advance to the next colour (useful to synchronise several lights).
 * Lights are double sided, so they work facing traffic from both directions.
 */
public class TrafficLightBlock extends HorizontalDirectionalBlock {
    public static final EnumProperty<LightState> LIGHT = EnumProperty.create("light", LightState.class);
    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 16, 12);

    public enum LightState implements StringRepresentable {
        RED("red", 200),
        GREEN("green", 160),
        YELLOW("yellow", 40);

        private final String name;
        public final int ticks;

        LightState(String name, int ticks) {
            this.name = name;
            this.ticks = ticks;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public LightState next() {
            LightState[] all = values();
            return all[(ordinal() + 1) % all.length];
        }
    }

    public TrafficLightBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIGHT, LightState.RED));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIGHT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        if (!level.isClientSide && !oldState.is(this)) {
            level.scheduleTick(pos, this, state.getValue(LIGHT).ticks);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState next = state.setValue(LIGHT, state.getValue(LIGHT).next());
        level.setBlock(pos, next, 3);
        level.scheduleTick(pos, this, next.getValue(LIGHT).ticks);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(LIGHT, state.getValue(LIGHT).next()), 3);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
