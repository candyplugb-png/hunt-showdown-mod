package com.huntmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static KeyMapping DARK_SIGHT_KEY;

    public static void register() {
        DARK_SIGHT_KEY = new KeyMapping(
                "key.huntmod.dark_sight",           // Translation key
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,                    // Default key: Z (like in Hunt Showdown)
                "key.categories.huntmod"            // Category name in controls menu
        );
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        if (DARK_SIGHT_KEY == null) {
            register();
        }
        event.register(DARK_SIGHT_KEY);
    }
}
