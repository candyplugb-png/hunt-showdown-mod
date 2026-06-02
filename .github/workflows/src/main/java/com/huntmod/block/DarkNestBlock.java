package com.huntmod.block;

import com.huntmod.init.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DarkNestBlock extends Block {

    // Track which players have already received the effect (max 2)
    private static final List<UUID> activatedPlayers = new ArrayList<>();
    private static final int MAX_PLAYERS = 2;

    // Effect duration: 99999 ticks (~83 min) — effectively permanent until removed
    private static final int EFFECT_DURATION = 99999;

    public DarkNestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                  Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            UUID playerUUID = player.getUUID();

            // Already has the effect
            if (activatedPlayers.contains(playerUUID)) {
                player.sendSystemMessage(Component.translatable("huntmod.dark_nest.already_activated"));
                return InteractionResult.SUCCESS;
            }

            // Max players reached
            if (activatedPlayers.size() >= MAX_PLAYERS) {
                player.sendSystemMessage(Component.translatable("huntmod.dark_nest.max_players"));
                return InteractionResult.FAIL;
            }

            // Grant Dark Sight effect (amplifier 0, duration near-infinite, no particles)
            player.addEffect(new MobEffectInstance(
                    ModEffects.DARK_SIGHT.get(),
                    EFFECT_DURATION,
                    0,
                    false,
                    false,  // no particles
                    true    // show icon
            ));

            activatedPlayers.add(playerUUID);
            player.sendSystemMessage(Component.translatable("huntmod.dark_sight.granted"));

            // Mark the block as "used" — keep it in world but it won't activate more players
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    /**
     * Called when effect is removed (e.g., player dies or admin removes it).
     * Frees up a slot so another player can use the nest.
     */
    public static void removePlayer(UUID uuid) {
        activatedPlayers.remove(uuid);
    }

    public static List<UUID> getActivatedPlayers() {
        return activatedPlayers;
    }
}
