package com.roadcraft.registry;

import com.roadcraft.RoadCraft;
import com.roadcraft.blockentity.RoadSignBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RoadCraft.MODID);

    public static final RegistryObject<BlockEntityType<RoadSignBlockEntity>> ROAD_SIGN =
            BLOCK_ENTITIES.register("road_sign", () ->
                    BlockEntityType.Builder.of(RoadSignBlockEntity::new, ModBlocks.ROAD_SIGN.get()).build(null));
}
