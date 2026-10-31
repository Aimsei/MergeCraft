package fr.aimsei.mergecraft.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.aimsei.mergecraft.MergeCraft;
import fr.aimsei.mergecraft.common.merge.MergeLevelData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MergeLevelCondition implements LootItemCondition {
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES =
            DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, MergeCraft.MODID);

    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> MERGE_LEVEL =
            LOOT_CONDITION_TYPES.register("merge_level", () -> new LootItemConditionType(MergeLevelCondition.CODEC));

    public static final MapCodec<MergeLevelCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            net.minecraft.world.level.storage.loot.IntRange.CODEC.fieldOf("level").forGetter(c -> c.level)
    ).apply(instance, MergeLevelCondition::new));

    private final net.minecraft.world.level.storage.loot.IntRange level;

    public MergeLevelCondition(net.minecraft.world.level.storage.loot.IntRange level) {
        this.level = level;
    }

    @Override
    public LootItemConditionType getType() {
        return MERGE_LEVEL.get();
    }

    @Override
    public boolean test(LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity == null) {
            return false;
        }
        return level.test(MergeLevelData.getMergeLevel(entity));
    }

    public static Builder builder(net.minecraft.world.level.storage.loot.IntRange range) {
        return () -> new MergeLevelCondition(range);
    }
}
