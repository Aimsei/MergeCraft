package fr.aimsei.mergecraft.client;

import fr.aimsei.mergecraft.common.merge.MergeLevelData;
import fr.aimsei.mergecraft.network.MergeLevelSyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public final class MergeLevelSyncHandler {
    private MergeLevelSyncHandler() {
    }

    public static void handle(MergeLevelSyncPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity entity = mc.level.getEntity(payload.entityId());
        if (entity != null) {
            entity.getPersistentData().putInt(MergeLevelData.NBT_KEY, payload.mergeLevel());
        }
    }
}
