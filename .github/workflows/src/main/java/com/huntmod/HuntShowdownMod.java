package com.huntmod;

import com.huntmod.client.KeyBindings;
import com.huntmod.client.DarkSightOverlay;
import com.huntmod.init.ModBlocks;
import com.huntmod.init.ModEffects;
import com.huntmod.init.ModItems;
import com.huntmod.network.ModMessages;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(HuntShowdownMod.MOD_ID)
public class HuntShowdownMod {
    public static final String MOD_ID = "huntmod";
    public static final Logger LOGGER = LogManager.getLogger();

    public HuntShowdownMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(this::clientSetup);
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
        LOGGER.info("[HuntMod] Common setup complete");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            KeyBindings.register();
            DarkSightOverlay.register();
        });
        LOGGER.info("[HuntMod] Client setup complete");
    }
}
