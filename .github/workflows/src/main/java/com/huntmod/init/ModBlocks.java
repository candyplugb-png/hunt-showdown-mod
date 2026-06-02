package com.huntmod.init;

import com.huntmod.HuntShowdownMod;
import com.huntmod.block.DarkNestBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, HuntShowdownMod.MOD_ID);

    public static final RegistryObject<Block> DARK_NEST =
            BLOCKS.register("dark_nest",
                    () -> new DarkNestBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .strength(0.5f)
                            .noOcclusion()
                            .lightLevel(state -> 0)));

    // Auto-register BlockItem for dark_nest
    static {
        ModItems.ITEMS.register("dark_nest", () ->
                new BlockItem(DARK_NEST.get(), new Item.Properties()));
    }
}
