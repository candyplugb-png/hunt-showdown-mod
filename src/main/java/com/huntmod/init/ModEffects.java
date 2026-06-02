package com.huntmod.init;

import com.huntmod.HuntShowdownMod;
import com.huntmod.effect.DarkSightEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, HuntShowdownMod.MOD_ID);

    public static final RegistryObject<MobEffect> DARK_SIGHT =
            EFFECTS.register("dark_sight",
                    () -> new DarkSightEffect(MobEffectCategory.BENEFICIAL, 0x8B0000));
}
