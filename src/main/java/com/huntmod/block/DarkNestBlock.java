package com.huntmod.block;
import com.huntmod.init.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
public class DarkNestBlock extends Block {
    private static final List<UUID> activatedPlayers = new ArrayList<>();
    private static final int MAX_PLAYERS = 2;
    public DarkNestBlock(Properties p) { super(p); }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            UUID uuid = player.getUUID();
            if (activatedPlayers.contains(uuid)) { player.sendSystemMessage(Component.translatable("huntmod.dark_nest.already_activated")); return InteractionResult.SUCCESS; }
            if (activatedPlayers.size() >= MAX_PLAYERS) { player.sendSystemMessage(Component.translatable("huntmod.dark_nest.max_players")); return InteractionResult.FAIL; }
            player.addEffect(new MobEffectInstance(ModEffects.DARK_SIGHT.get(), 99999, 0, false, false, true));
            activatedPlayers.add(uuid);
            player.sendSystemMessage(Component.translatable("huntmod.dark_sight.granted"));
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
    public static void removePlayer(UUID uuid) { activatedPlayers.remove(uuid); }
}
