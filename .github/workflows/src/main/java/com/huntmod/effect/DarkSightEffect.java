package com.huntmod.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class DarkSightEffect extends MobEffect {

    public DarkSightEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // Server-side logic runs here if needed
        // Most of the Dark Sight logic is client-side in the overlay + key handler
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Don't apply every tick on server side — we handle client rendering
        return false;
    }
}
