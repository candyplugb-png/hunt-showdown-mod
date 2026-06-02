package com.huntmod.network;

import com.huntmod.HuntShowdownMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(HuntShowdownMod.MOD_ID, "messages"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        // Register DarkSight sync packet (server -> client)
        net.messageBuilder(DarkSightPacket.class, id())
                .decoder(DarkSightPacket::new)
                .encoder(DarkSightPacket::encode)
                .consumerMainThread(DarkSightPacket::handle)
                .add();

        INSTANCE = net;
    }
}
