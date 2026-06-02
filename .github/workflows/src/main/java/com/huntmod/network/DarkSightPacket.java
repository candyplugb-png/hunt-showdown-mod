package com.huntmod.network;

import com.huntmod.init.ModEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DarkSightPacket {
    private final boolean hasEffect;

    public DarkSightPacket(boolean hasEffect) {
        this.hasEffect = hasEffect;
    }

    public DarkSightPacket(FriendlyByteBuf buf) {
        this.hasEffect = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(hasEffect);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Clientside: apply or remove effect based on server signal
            Player player = net.minecraftforge.client.ClientHooks.getClientPlayer();
            if (player == null) return;

            if (hasEffect) {
                if (!player.hasEffect(ModEffects.DARK_SIGHT.get())) {
                    player.addEffect(new MobEffectInstance(
                            ModEffects.DARK_SIGHT.get(), 99999, 0, false, false, true));
                }
            } else {
                player.removeEffect(ModEffects.DARK_SIGHT.get());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
