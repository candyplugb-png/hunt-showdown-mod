package com.huntmod;
import com.huntmod.client.DarkSightOverlay;
import com.huntmod.client.KeyBindings;
import com.huntmod.init.ModBlocks;
import com.huntmod.init.ModEffects;
import com.huntmod.init.ModItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
@Mod(HuntShowdownMod.MOD_ID)
public class HuntShowdownMod {
    public static final String MOD_ID = "huntmod";
    public HuntShowdownMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ModEffects.EFFECTS.register(bus);
        bus.addListener(this::commonSetup);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            bus.addListener(this::clientSetup);
            bus.addListener(KeyBindings::onRegisterKeyMappings);
        }
        MinecraftForge.EVENT_BUS.register(this);
    }
    private void commonSetup(final FMLCommonSetupEvent event) {}
    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> { KeyBindings.register(); DarkSightOverlay.register(); });
    }
}
