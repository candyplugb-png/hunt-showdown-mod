package com.huntmod.item;

import com.huntmod.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DarkNestEggItem extends Item {
    public DarkNestEggItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            BlockPos placePos = context.getClickedPos().relative(context.getClickedFace());
            if (level.getBlockState(placePos).isAir()) {
                level.setBlock(placePos, ModBlocks.DARK_NEST.get().defaultBlockState(), 3);
                if (!context.getPlayer().getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
