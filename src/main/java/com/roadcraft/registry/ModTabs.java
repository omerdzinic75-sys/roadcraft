package com.roadcraft.registry;

import com.roadcraft.RoadCraft;
import com.roadcraft.sign.SignPresets;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModTabs {
    public static final CreativeModeTab TAB = new CreativeModeTab(RoadCraft.MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.TRAFFIC_LIGHT.get());
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> items) {
            super.fillItemList(items);
            items.add(SignPresets.highway());
            items.add(SignPresets.exit());
            items.add(SignPresets.city());
            items.add(SignPresets.cityLimit());
            items.add(SignPresets.gasStation());
        }
    };
}
