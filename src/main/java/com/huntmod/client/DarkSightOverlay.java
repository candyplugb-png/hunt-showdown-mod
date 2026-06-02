package com.huntmod.client;
import com.huntmod.init.ModBlocks;
import com.huntmod.init.ModEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
    public static void register() { MinecraftForge.EVENT_BUS.register(new DarkSightOverlay()); }
    private boolean isActive() {
        Minecraft mc = Minecraft.getInstance();
        Player p = mc.player;
        return p != null && p.hasEffect(ModEffects.DARK_SIGHT.get()) && KeyBindings.DARK_SIGHT_KEY != null && KeyBindings.DARK_SIGHT_KEY.isDown();
    }
    @SubscribeEvent
    public void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (!isActive()) return;
        int w = event.getWindow().getGuiScaledWidth(), h = event.getWindow().getGuiScaledHeight();
        PoseStack ps = event.getPoseStack();
        RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader); RenderSystem.disableTexture();
        Tesselator t = Tesselator.getInstance(); BufferBuilder buf = t.getBuilder();
        buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buf.vertex(ps.last().pose(),0,0,0).color(0,0,0,237).endVertex();
        buf.vertex(ps.last().pose(),0,h,0).color(0,0,0,237).endVertex();
        buf.vertex(ps.last().pose(),w,h,0).color(0,0,0,237).endVertex();
        buf.vertex(ps.last().pose(),w,0,0).color(0,0,0,237).endVertex();
        t.end(); RenderSystem.enableTexture(); RenderSystem.disableBlend();
    }
    @SubscribeEvent
    public void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        if (!isActive()) return;
        Minecraft mc = Minecraft.getInstance();
        Player local = mc.player; Level level = mc.level;
        if (local == null || level == null) return;
        for (BlockPos pos : findNests(level, local)) drawBlockBox(event.getPoseStack(), pos, local, 1f,0.84f,0f,0.9f);
        level.getEntitiesOfClass(Player.class, new AABB(local.blockPosition()).inflate(75),
            p -> !p.equals(local) && p.hasEffect(ModEffects.DARK_SIGHT.get()))
            .forEach(p -> drawEntityBox(event.getPoseStack(), p, local, 1f,0f,0f,0.85f));
    }
    private List<BlockPos> findNests(Level level, Player player) {
        List<BlockPos> list = new ArrayList<>();
        BlockPos c = player.blockPosition();
        for (int x=-256;x<=256;x+=4) for (int z=-256;z<=256;z+=4)
            for (int y=level.getMinBuildHeight();y<=level.getMaxBuildHeight();y+=4) {
                BlockPos p = c.offset(x,y,z);
                if (level.isLoaded(p) && level.getBlockState(p).is(ModBlocks.DARK_NEST.get())) list.add(p);
            }
        return list;
    }
    private void drawBlockBox(PoseStack ps, BlockPos pos, Player cam, float r,float g,float b,float a) {
        ps.pushPose();
        ps.translate(pos.getX()-cam.getX(), pos.getY()-cam.getY()-cam.getEyeHeight(), pos.getZ()-cam.getZ());
        RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); RenderSystem.disableTexture();
        RenderSystem.lineWidth(3f); RenderSystem.setShader(GameRenderer::getPositionColorShader);
        drawBox(ps, new AABB(0,0,0,1,1,1).inflate(0.05), r,g,b,a);
        RenderSystem.enableTexture(); RenderSystem.disableBlend(); ps.popPose();
    }
    private void drawEntityBox(PoseStack ps, Player target, Player cam, float r,float g,float b,float a) {
        ps.pushPose();
        ps.translate(target.getX()-cam.getX(), target.getY()-cam.getY()-cam.getEyeHeight(), target.getZ()-cam.getZ());
        RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); RenderSystem.disableTexture();
        RenderSystem.lineWidth(2.5f); RenderSystem.setShader(GameRenderer::getPositionColorShader);
        float w=target.getBbWidth()/2+0.1f, h=target.getBbHeight()+0.1f;
        drawBox(ps, new AABB(-w,0,-w,w,h,w), r,g,b,a);
        RenderSystem.enableTexture(); RenderSystem.disableBlend(); ps.popPose();
    }
    private void drawBox(PoseStack ps, AABB box, float r,float g,float b,float a) {
        Matrix4f m = ps.last().pose();
        float x0=(float)box.minX,y0=(float)box.minY,z0=(float)box.minZ,x1=(float)box.maxX,y1=(float)box.maxY,z1=(float)box.maxZ;
        int ri=(int)(r*255),gi=(int)(g*255),bi=(int)(b*255),ai=(int)(a*255);
        Tesselator t=Tesselator.getInstance(); BufferBuilder buf=t.getBuilder();
        buf.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        ln(buf,m,x0,y0,z0,x1,y0,z0,ri,gi,bi,ai); ln(buf,m,x1,y0,z0,x1,y0,z1,ri,gi,bi,ai);
        ln(buf,m,x1,y0,z1,x0,y0,z1,ri,gi,bi,ai); ln(buf,m,x0,y0,z1,x0,y0,z0,ri,gi,bi,ai);
        ln(buf,m,x0,y1,z0,x1,y1,z0,ri,gi,bi,ai); ln(buf,m,x1,y1,z0,x1,y1,z1,ri,gi,bi,ai);
        ln(buf,m,x1,y1,z1,x0,y1,z1,ri,gi,bi,ai); ln(buf,m,x0,y1,z1,x0,y1,z0,ri,gi,bi,ai);
        ln(buf,m,x0,y0,z0,x0,y1,z0,ri,gi,bi,ai); ln(buf,m,x1,y0,z0,x1,y1,z0,ri,gi,bi,ai);
        ln(buf,m,x1,y0,z1,x1,y1,z1,ri,gi,bi,ai); ln(buf,m,x0,y0,z1,x0,y1,z1,ri,gi,bi,ai);
        t.end();
    }
    private void ln(BufferBuilder b, Matrix4f m, float x0,float y0,float z0,float x1,float y1,float z1,int r,int g,int bi,int a) {
        b.vertex(m,x0,y0,z0).color(r,g,bi,a).endVertex();
        b.vertex(m,x1,y1,z1).color(r,g,bi,a).endVertex();
    }
}
