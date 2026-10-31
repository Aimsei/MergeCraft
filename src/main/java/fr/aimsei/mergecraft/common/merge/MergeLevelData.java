package fr.aimsei.mergecraft.common.merge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class MergeLevelData {
    public static final String NBT_KEY = "mergecraft_merge_level";

    private MergeLevelData() {
    }

    public static int getMergeLevel(Entity entity) {
        CompoundTag tag = entity.getPersistentData();
        if (!tag.contains(NBT_KEY)) {
            return 1;
        }
        return Math.max(1, tag.getInt(NBT_KEY));
    }

    public static void setMergeLevel(Entity entity, int level) {
        entity.getPersistentData().putInt(NBT_KEY, Math.max(1, level));
        if (entity instanceof LivingEntity living) {
            MergeStats.applyHealthForLevel(living, level);
        }
    }

    public static CompoundTag save(int level) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(NBT_KEY, level);
        return tag;
    }
}
