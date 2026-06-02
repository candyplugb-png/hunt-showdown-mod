package com.huntmod.client;

import com.huntmod.init.ModBlocks;
import com.huntmod.init.ModEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
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

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new DarkSightOverlay());
    }

    @SubscribeEvent
    public void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        if (!player.hasEffect(ModEffects.DARK_SIGHT.get())) return;
        if (KeyBindings.DARK_SIGHT_KEY == null || !KeyBindings.DARK_SIGHT_KEY.isDown()) return;
        renderDarkness(event.getPoseStack(),
                event.getWindow().getGuiScaledWidth(),
                event.getWindow().getGuiScaledHeight());
    }

    private void renderDarkness(PoseStack poseStack, int width, int height) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(poseStack.last().pose(), 0,     0,      0).color(0, 0, 0, 237).endVertex();
        buffer.vertex(poseStack.last().pose(), 0,     height, 0).color(0, 0, 0, 237).endVertex();
        buffer.vertex(poseStack.last().pose(), width, height, 0).color(0, 0, 0, 237).endVertex();
        buffer.vertex(poseStack.last().pose(), width, 0,      0).color(0, 0, 0, 237).endVertex();
        tesselator.end();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    @SubscribeEvent
    public void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        Minecraft mc = Minecraft.getInstance();
        Player localPlayer = mc.player;
        Level level = mc.level;
        if (localPlayer == null || level == null) return;
        if (!localPlayer.hasEffect(ModEffects.DARK_SIGHT.get())) return;
        if (KeyBindings.DARK_SIGHT_KEY == null || !KeyBindings.DARK_SIGHT_KEY.isDown()) return;

        for (BlockPos pos : findDarkNestBlocks(level, localPlayer)) {
            renderBlockGlow(event.getPoseStack(), pos, localPlayer, 1.0f, 0.84f, 0.0f);
        }

        level.getEntitiesOfClass(Player.class,
                new AABB(localPlayer.blockPosition()).inflate(75),
                p -> !p.equals(localPlayer) && p.hasEffect(ModEffects.DARK_SIGHT.get())
        ).forEach(target -> renderEntityGlow(event.getPoseStack(), target, localPlayer));
    }

    private List<BlockPos> findDarkNestBlocks(Level level, Player player) {
        List<BlockPos> result = new ArrayList<>();
        BlockPos center = player.blockPosition();
        int r = 256;
        for (int x = -r; x <= r; x += 4) {
            for (int z = -r; z <= r; z += 4) {
                for (int y = level.getMinBuildHeight(); y <= level.getMaxBuildHeight(); y += 4) {
                    BlockPos pos = center.offset(x, y, z);
                    if (level.isLoaded(pos)) {
                        BlockState state = level.getBlockState(pos);
                        if (state.is(ModBlocks.DARK_NEST.get())) result.add(pos);
                    }
                }
            }
        }
        return result;
    }

    private void renderBlockGlow(PoseStack ps, BlockPos pos, Player player, float r, float g, float b) {
        double cx = player.getX(), cy = player.getY() + player.getEyeHeight(), cz = player.getZ();
        ps.pushPose();
        ps.translate(pos.getX() - cx, pos.getY() - cy, pos.getZ() - cz);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(3.0f);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        drawBox(ps, new AABB(0,0,0,1,1,1).inflate(0.05), r, g, b, 0.9f);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        ps.popPose();
    }

    private void renderEntityGlow(PoseStack ps, Player target, Player local) {
        double cx = local.getX(), cy = local.getY() + local.getEyeHeight(), cz = local.getZ();
        ps.pushPose();
        ps.translate(target.getX() - cx, target.getY() - cy, target.getZ() - cz);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(2.5f);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        float w = target.getBbWidth() / 2 + 0.1f;
        float h = target.getBbHeight() + 0.1f;
        drawBox(ps, new AABB(-w, 0, -w, w, h, w), 1.0f, 0.0f, 0.0f, 0.85f);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        ps.popPose();
    }

    private void drawBox(PoseStack ps, AABB box, float r, float g, float b, float a) {
        Tesselator t = Tesselator.getInstance();
        BufferBuilder buf = t.getBuilder();
        var m = ps.last().pose();
        float x0=(float)box.minX, y0=(float)box.minY, z0=(float)box.minZ;
        float x1=(float)box.maxX, y1=(float)box.maxY, z1=(float)box.maxZ;
        int ri=(int)(r*255), gi=(int)(g*255), bi=(int)(b*255), ai=(int)(a*255);
        buf.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        // Bottom
        buf.vertex(m,x0,y0,z0).color(ri,gi,bi,ai).endVertex(); buf.vertex(m,x1,y0,z0).color(ri,gi,bi,ai).endVertex();
        buf.vertex(m,x1,y0,z0).color(ri,gi,bi,ai).endVertex(); buf.vertex(m,x1,y0,z1).color(ri,gi,bi,ai).endVertex();
        buf.vertex(m,x1,y0,z1).color(ri,gi,bi,ai).endVertex(); buf.vertex(m,x0,y0,z1).color(ri,gi,bi,ai).endVertex();
        buf.vertex(m,x0,y0,z1).color(ri,gi,bi,ai).endVertex(); buf.vertex(m,x0,y0,z0).color(ri,gi,bi,ai).endVertex();
        // Top
        buf.vertex(m,x0,y1,z0).color(ri,gi,bi,ai).endVertex(); buf.vertex(m,x1,y1,z0).color(ri,gi,bi,ai).endVertex();
        buf.vertex(m,x1,y1,z0).color(ri,gi,bi,ai).endVertex(); buf.vertex(m,x1,y1,z1).color(ri,gi,bi,ai).endVertex();
        buf.vertex(m,x1,y1,z1).color(ri,gi,bi,ai).endVertex(); buf.vertex(m,x0,y1,z1).color(ri,gi,bi,ai).endVertex();
        buf.vertex(m,x0,y1,z1).color(ri,gi,bi,ai).endVertex(); buf.vertex(m,x0,y1,z0).color(ri,gi,bi,ai).endVertex();
        // Verticals
        buf.vertex(m,x0,y0,z0).color(ri,gi,bi,ai).endVertex(); buf.vertex(m,x0,y1,z0).color(ri,gi,bi,ai).endVertex();
        buf.vertex(m,x1,y0,z0).color(ri,gi,bi,ai).endVertex(); buf.vertex(m,x1,y1,z0).color(ri,gi,bi,ai).endVertex();
        buf.vertex(m,x1,y0,z1).color(ri,gi,bi,ai).endVertex(); buf.vertex(m,x1,y1,z1).color(ri,gi,bi,ai).endVertex();
        buf.vertex(m,x0,y0,z1).color(ri,gi,bi,ai).endVertex(); buf.vertex(m,x0,y1,z1).color(ri,gi,bi,ai).endVertex();
        t.end();
    }
}
