package fr.aimsei.mergecraft.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.aimsei.mergecraft.MergeCraft;
import fr.aimsei.mergecraft.common.merge.MergeLevelData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;

@EventBusSubscriber(modid = MergeCraft.MODID, value = Dist.CLIENT)
public class MergeLevelRenderHandler {

    @SubscribeEvent
    public static void onRenderNameTag(RenderNameTagEvent event) {
        Entity entity = event.getEntity();
        int level = MergeLevelData.getMergeLevel(entity);
        if (level <= 1) {
            return;
        }
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(0.0, entity.getBbHeight() + 0.35, 0.0);
        float scale = 0.025F;
        poseStack.scale(-scale, -scale, scale);
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        Component text = Component.translatable("render.mergecraft.merge_level", level);
        int width = font.width(text);
        int bg = 0x80000000;
        float x = -width / 2.0F;
        poseStack.translate(0, -font.lineHeight, 0);
        net.minecraft.client.renderer.MultiBufferSource buffer = event.getMultiBufferSource();
        if (buffer != null) {
            font.drawInBatch(text, x, 0, 0xFFD700, false, poseStack.last().pose(),
                    buffer, Font.DisplayMode.NORMAL, bg, event.getPackedLight());
        }
        poseStack.popPose();
    }
}
