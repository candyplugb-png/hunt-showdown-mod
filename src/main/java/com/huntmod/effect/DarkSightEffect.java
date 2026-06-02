package com.huntmod.effect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
public class DarkSightEffect extends MobEffect {
    public DarkSightEffect(MobEffectCategory cat, int color) { super(cat, color); }
    @Override public void applyEffectTick(LivingEntity e, int amp) {}
    @Override public boolean isDurationEffectTick(int dur, int amp) { return false; }
}
