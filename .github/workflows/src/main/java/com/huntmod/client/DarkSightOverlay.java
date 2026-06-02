package com.huntmod.client;

import com.huntmod.HuntShowdownMod;
import com.huntmod.init.ModBlocks;
import com.huntmod.init.ModEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class DarkSightOverlay {

    private static final ResourceLocation DARK_SIGHT_TEXTURE =
            new ResourceLocation(HuntShowdownMod.MOD_ID, "textures/effect/dark_sight_vignette.png");

    // Distance within which players with the bounty are highlighted (red glow)
    private static final int PLAYER_DETECTION_RANGE = 75;

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new DarkSightOverlay());
    }

    /**
     * Renders the screen-darkening vignette + highlights while key is held.
     */
    @SubscribeEvent
    public void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) return;
        if (!player.hasEffect(ModEffects.DARK_SIGHT.get())) return;
        if (KeyBindings.DARK_SIGHT_KEY == null) return;
        if (!KeyBindings.DARK_SIGHT_KEY.isDown()) return;

        // Draw full-screen darkness overlay (like Darkness effect from Warden)
        renderDarknessOverlay(event.getGuiGraphics().pose(),
                event.getWindow().getGuiScaledWidth(),
                event.getWindow().getGuiScaledHeight());
    }

    /**
     * Draws a very dark semi-transparent black overlay over the entire screen.
     * Alpha ~0.93 mimics Warden darkness effect.
     */
    private void renderDarknessOverlay(PoseStack poseStack, int width, int height) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        // Outer darkness: nearly fully black, 93% opacity
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(poseStack.last().pose(), 0,
