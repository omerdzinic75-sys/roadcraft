package com.roadcraft.registry;

import com.roadcraft.RoadCraft;
import com.roadcraft.item.FullFuelCanItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RoadCraft.MODID);

    private static RegistryObject<Item> blockItem(String name, RegistryObject<Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(ModTabs.TAB)));
    }

    public static final RegistryObject<Item> ASPHALT = blockItem("asphalt", ModBlocks.ASPHALT);
    public static final RegistryObject<Item> ASPHALT_SLAB = blockItem("asphalt_slab", ModBlocks.ASPHALT_SLAB);
    public static final RegistryObject<Item> ROAD_LINE_WHITE = blockItem("road_line_white", ModBlocks.ROAD_LINE_WHITE);
    public static final RegistryObject<Item> ROAD_LINE_YELLOW = blockItem("road_line_yellow", ModBlocks.ROAD_LINE_YELLOW);
    public static final RegistryObject<Item> ROAD_CROSSWALK = blockItem("road_crosswalk", ModBlocks.ROAD_CROSSWALK);
    public static final RegistryObject<Item> CONCRETE_BARRIER = blockItem("concrete_barrier", ModBlocks.CONCRETE_BARRIER);
    public static final RegistryObject<Item> SIGN_POLE = blockItem("sign_pole", ModBlocks.SIGN_POLE);
    public static final RegistryObject<Item> ROAD_SIGN = blockItem("road_sign", ModBlocks.ROAD_SIGN);
    public static final RegistryObject<Item> TRAFFIC_LIGHT = blockItem("traffic_light", ModBlocks.TRAFFIC_LIGHT);
    public static final RegistryObject<Item> GAS_PUMP = blockItem("gas_pump", ModBlocks.GAS_PUMP);
    public static final RegistryObject<Item> GAS_CANOPY = blockItem("gas_canopy", ModBlocks.GAS_CANOPY);

    public static final RegistryObject<Item> FUEL_CAN = ITEMS.register("fuel_can",
            () -> new Item(new Item.Properties().tab(ModTabs.TAB).stacksTo(16)));
    public static final RegistryObject<Item> FUEL_CAN_FULL = ITEMS.register("fuel_can_full",
            () -> new FullFuelCanItem(new Item.Properties().tab(ModTabs.TAB).stacksTo(1)));
}
