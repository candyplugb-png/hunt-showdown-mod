package com.huntmod.item;
import com.huntmod.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
public class DarkNestEggItem extends Item {
    public DarkNestEggItem(Properties p) { super(p); }
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        if (!level.isClientSide()) {
            BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());
            if (level.getBlockState(pos).isAir()) {
                level.setBlock(pos, ModBlocks.DARK_NEST.get().defaultBlockState(), 3);
                if (!ctx.getPlayer().getAbilities().instabuild) ctx.getItemInHand().shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
