package com.roadcraft.registry;

import com.roadcraft.RoadCraft;
import com.roadcraft.block.ConcreteBarrierBlock;
import com.roadcraft.block.GasCanopyBlock;
import com.roadcraft.block.GasPumpBlock;
import com.roadcraft.block.RoadMarkingBlock;
import com.roadcraft.block.RoadSignBlock;
import com.roadcraft.block.SignPoleBlock;
import com.roadcraft.block.TrafficLightBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RoadCraft.MODID);

    /** Road surfaces: walking/running on them is 15% faster. */
    private static BlockBehaviour.Properties road() {
        return BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                .strength(2.0F, 6.0F).sound(SoundType.STONE).speedFactor(1.15F);
    }

    private static BlockBehaviour.Properties metal() {
        return BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL)
                .strength(2.0F, 6.0F).sound(SoundType.METAL).noOcclusion();
    }

    public static final RegistryObject<Block> ASPHALT = BLOCKS.register("asphalt", () -> new Block(road()));
    public static final RegistryObject<Block> ASPHALT_SLAB = BLOCKS.register("asphalt_slab", () -> new SlabBlock(road()));
    public static final RegistryObject<Block> ROAD_LINE_WHITE = BLOCKS.register("road_line_white", () -> new RoadMarkingBlock(road()));
    public static final RegistryObject<Block> ROAD_LINE_YELLOW = BLOCKS.register("road_line_yellow", () -> new RoadMarkingBlock(road()));
    public static final RegistryObject<Block> ROAD_CROSSWALK = BLOCKS.register("road_crosswalk", () -> new RoadMarkingBlock(road()));
    public static final RegistryObject<Block> CONCRETE_BARRIER = BLOCKS.register("concrete_barrier", () ->
            new ConcreteBarrierBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE)
                    .strength(2.0F, 6.0F).sound(SoundType.STONE).noOcclusion()));
    public static final RegistryObject<Block> SIGN_POLE = BLOCKS.register("sign_pole", () -> new SignPoleBlock(metal()));
    public static final RegistryObject<Block> ROAD_SIGN = BLOCKS.register("road_sign", () -> new RoadSignBlock(metal()));
    public static final RegistryObject<Block> TRAFFIC_LIGHT = BLOCKS.register("traffic_light", () ->
            new TrafficLightBlock(metal().lightLevel(state -> 7)));
    public static final RegistryObject<Block> GAS_PUMP = BLOCKS.register("gas_pump", () -> new GasPumpBlock(metal()));
    public static final RegistryObject<Block> GAS_CANOPY = BLOCKS.register("gas_canopy", () -> new GasCanopyBlock(metal()));
}
