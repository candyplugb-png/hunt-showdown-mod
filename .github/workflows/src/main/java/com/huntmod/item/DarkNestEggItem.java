package com.huntmod.item;

import com.huntmod.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DarkNestEggItem extends Item {

    public DarkNestEggItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (!level.isClientSide()) {
            // Place the Dark Nest one block above where we clicked
            BlockPos clickedPos = context.getClickedPos();
            BlockPos placePos = clickedPos.relative(context.getClickedFace());

            BlockState nestState = ModBlocks.DARK_NEST.get().defaultBlockState();

            if (level.getBlockState(placePos).isAir()) {
                level.setBlock(placePos, nestState, 3);

                // Consume item if not in creative
                if (!context.getPlayer().getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
