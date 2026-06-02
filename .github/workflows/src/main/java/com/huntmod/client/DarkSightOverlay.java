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
        renderDarknessOverlay(event.getPoseStack(),
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
        RenderSystem.disableTexture();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        // Outer darkness: nearly fully black, 93% opacity
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(poseStack.last().pose(), 0,     0,      0).color(0, 0, 0, 237).endVertex();
        buffer.vertex(poseStack.last().pose(), 0,     height, 0).color(0, 0, 0, 237).endVertex();
        buffer.vertex(poseStack.last().pose(), width, height, 0).color(0, 0, 0, 237).endVertex();
        buffer.vertex(poseStack.last().pose(), width, 0,      0).color(0, 0, 0, 237).endVertex();
        tesselator.end();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    /**
     * Renders glowing outlines on Dark Nest blocks and enemy players during Dark Sight.
     * Uses the RenderLevelStageEvent at AFTER_TRANSLUCENT_BLOCKS stage.
     */
    @SubscribeEvent
    public void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        Player localPlayer = mc.player;
        Level level = mc.level;

        if (localPlayer == null || level == null) return;
        if (!localPlayer.hasEffect(ModEffects.DARK_SIGHT.get())) return;
        if (KeyBindings.DARK_SIGHT_KEY == null) return;
        if (!KeyBindings.DARK_SIGHT_KEY.isDown()) return;

        // ---- 1. Highlight ALL Dark Nest blocks in the world (no distance limit) ----
        List<BlockPos> nestPositions = findDarkNestBlocks(level, localPlayer);
        for (BlockPos pos : nestPositions) {
            renderBlockGlow(event.getPoseStack(), pos, localPlayer,
                    1.0f, 0.84f, 0.0f, 0.9f); // Gold/amber glow — like Hunt's bounty
        }

        // ---- 2. Highlight players with the Dark Sight effect within 75 blocks (red outline) ----
        List<Player> nearbyPlayers = level.getEntitiesOfClass(
                Player.class,
                new AABB(localPlayer.blockPosition()).inflate(PLAYER_DETECTION_RANGE),
                p -> !p.equals(localPlayer) && p.hasEffect(ModEffects.DARK_SIGHT.get())
        );

        for (Player target : nearbyPlayers) {
            double distance = localPlayer.distanceTo(target);
            if (distance <= PLAYER_DETECTION_RANGE) {
                renderEntityGlow(event.getPoseStack(), target, localPlayer,
                        1.0f, 0.0f, 0.0f, 0.85f); // Red glow — enemy highlight
            }
        }
    }

    /**
     * Scans loaded chunks near the player for Dark Nest blocks.
     * No distance limit — visible at any range.
     */
    private List<BlockPos> findDarkNestBlocks(Level level, Player player) {
        List<BlockPos> result = new ArrayList<>();
        BlockPos center = player.blockPosition();
        int searchRadius = 512; // Scan radius in blocks (large but bounded)

        for (int x = -searchRadius; x <= searchRadius; x += 4) {
            for (int z = -searchRadius; z <= searchRadius; z += 4) {
                for (int y = level.getMinBuildHeight(); y <= level.getMaxBuildHeight(); y += 4) {
                    BlockPos pos = center.offset(x, y, z);
                    if (level.isLoaded(pos)) {
                        BlockState state = level.getBlockState(pos);
                        if (state.is(ModBlocks.DARK_NEST.get())) {
                            result.add(pos);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Renders a glowing colored box outline around a block.
     */
    private void renderBlockGlow(PoseStack poseStack, BlockPos pos, Player player,
                                  float r, float g, float b, float alpha) {
        double camX = player.getX();
        double camY = player.getY() + player.getEyeHeight();
        double camZ = player.getZ();

        double dx = pos.getX() - camX;
        double dy = pos.getY() - camY;
        double dz = pos.getZ() - camZ;

        poseStack.pushPose();
        poseStack.translate(dx, dy, dz);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(3.0f);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        int alphaInt = (int)(alpha * 255);
        int rInt = (int)(r * 255);
        int gInt = (int)(g * 255);
        int bInt = (int)(b * 255);

        // Draw glowing box
        AABB box = new AABB(0, 0, 0, 1, 1, 1).inflate(0.05);
        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        drawAABBLines(buffer, poseStack, box, rInt, gInt, bInt, alphaInt);

        tesselator.end();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    /**
     * Renders a glowing outline around an entity (like spectral arrow but red).
     */
    private void renderEntityGlow(PoseStack poseStack, Player target, Player localPlayer,
                                   float r, float g, float b, float alpha) {
        double camX = localPlayer.getX();
        double camY = localPlayer.getY() + localPlayer.getEyeHeight();
        double camZ = localPlayer.getZ();

        double dx = target.getX() - camX;
        double dy = target.getY() - camY;
        double dz = target.getZ() - camZ;

        poseStack.pushPose();
        poseStack.translate(dx, dy, dz);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(2.5f);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        float w = target.getBbWidth() / 2 + 0.1f;
        float h = target.getBbHeight() + 0.1f;

        AABB box = new AABB(-w, 0, -w, w, h, w);

        int alphaInt = (int)(alpha * 255);
        int rInt = (int)(r * 255);
        int gInt = (int)(g * 255);
        int bInt = (int)(b * 255);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        drawAABBLines(buffer, poseStack, box, rInt, gInt, bInt, alphaInt);
        tesselator.end();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    /**
     * Draws wireframe lines for an AABB.
     */
    private void drawAABBLines(BufferBuilder buffer, PoseStack poseStack, AABB box,
                                int r, int g, int b, int a) {
        var m = poseStack.last().pose();
        float x0 = (float) box.minX, y0 = (float) box.minY, z0 = (float) box.minZ;
        float x1 = (float) box.maxX, y1 = (float) box.maxY, z1 = (float) box.maxZ;

        // Bottom face
        buffer.vertex(m, x0, y0, z0).color(r, g, b, a).endVertex();
        buffer.vertex(m, x1, y0, z0).color(r, g, b, a).endVertex();
        buffer.vertex(m, x1, y0, z0).color(r, g, b, a).endVertex();
        buffer.vertex(m, x1, y0, z1).color(r, g, b, a).endVertex();
        buffer.vertex(m, x1, y0, z1).color(r, g, b, a).endVertex();
        buffer.vertex(m, x0, y0, z1).color(r, g, b, a).endVertex();
        buffer.vertex(m, x0, y0, z1).color(r, g, b, a).endVertex();
        buffer.vertex(m, x0, y0, z0).color(r, g, b, a).endVertex();
        // Top face
        buffer.vertex(m, x0, y1, z0).color(r, g, b, a).endVertex();
        buffer.vertex(m, x1, y1, z0).color(r, g, b, a).endVertex();
        buffer.vertex(m, x1, y1, z0).color(r, g, b, a).endVertex();
        buffer.vertex(m, x1, y1, z1).color(r, g, b, a).endVertex();
        buffer.vertex(m, x1, y1, z1).color(r, g, b, a).endVertex();
        buffer.vertex(m, x0, y1, z1).color(r, g, b, a).endVertex();
        buffer.vertex(m, x0, y1, z1).color(r, g, b, a).endVertex();
        buffer.vertex(m, x0, y1, z0).color(r, g, b, a).endVertex();
        // Vertical edges
        buffer.vertex(m, x0, y0, z0).color(r, g, b, a).endVertex();
        buffer.vertex(m, x0, y1, z0).color(r, g, b, a).endVertex();
        buffer.vertex(m, x1, y0, z0).color(r, g, b, a).endVertex();
        buffer.vertex(m, x1, y1, z0).color(r, g, b, a).endVertex();
        buffer.vertex(m, x1, y0, z1).color(r, g, b, a).endVertex();
        buffer.vertex(m, x1, y1, z1).color(r, g, b, a).endVertex();
        buffer.vertex(m, x0, y0, z1).color(r, g, b, a).endVertex();
        buffer.vertex(m, x0, y1, z1).color(r, g, b, a).endVertex();
    }
}
