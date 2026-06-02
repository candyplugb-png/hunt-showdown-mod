package com.huntmod.init;
import com.huntmod.HuntShowdownMod;
import com.huntmod.item.DarkNestEggItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HuntShowdownMod.MOD_ID);
    public static final RegistryObject<Item> DARK_NEST_EGG = ITEMS.register("dark_nest_egg", () -> new DarkNestEggItem(new Item.Properties().stacksTo(1)));
}
