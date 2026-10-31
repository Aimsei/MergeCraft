package fr.aimsei.mergecraft.common.merge;

import fr.aimsei.mergecraft.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class MergeStats {
    public static final String BASE_HEALTH_KEY = "mergecraft_base_health";

    private MergeStats() {
    }

    public static double healthMultiplier(int level) {
        if (level <= 1) {
            return 1.0;
        }
        double perLevel = Config.HEALTH_MULTIPLIER_PER_LEVEL.get();
        return Math.pow(perLevel, level - 1);
    }

    public static void applyHealthForLevel(LivingEntity living, int level) {
        var maxHealthAttr = living.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr == null) {
            return;
        }
        CompoundTag data = living.getPersistentData();
        if (!data.contains(BASE_HEALTH_KEY)) {
            data.putDouble(BASE_HEALTH_KEY, maxHealthAttr.getBaseValue());
        }
        double base = data.getDouble(BASE_HEALTH_KEY);
        double target = base * healthMultiplier(level);
        float ratio = living.getMaxHealth() > 0 ? living.getHealth() / living.getMaxHealth() : 1.0f;
        maxHealthAttr.setBaseValue(target);
        living.setHealth((float) (target * ratio));
    }
}
