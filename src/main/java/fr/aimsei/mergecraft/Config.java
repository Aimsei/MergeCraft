package fr.aimsei.mergecraft;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue SELECTION_DURATION_SECONDS = BUILDER
            .comment("Durée de persistance d'une sélection de mob (en secondes)")
            .defineInRange("selectionDurationSeconds", 5, 1, 600);

    public static final ModConfigSpec.IntValue WAND_DURABILITY = BUILDER
            .comment("Nombre d'utilisations de la Baguette de Merge (1 par sélection)")
            .defineInRange("wandDurability", 30, 1, 100000);

    public static final ModConfigSpec.IntValue MAX_MERGE_LEVEL = BUILDER
            .comment("Niveau de merge maximum atteignable")
            .defineInRange("maxMergeLevel", 10, 1, 100);

    public static final ModConfigSpec.DoubleValue HEALTH_MULTIPLIER_PER_LEVEL = BUILDER
            .comment("Multiplicateur de PV max par niveau au-delà du niveau 1 (ex: 2.0 = PV x2 par niveau)")
            .defineInRange("healthMultiplierPerLevel", 2.0, 1.0, 1000.0);

    public static final ModConfigSpec.IntValue FUSION_ANIMATION_TICKS = BUILDER
            .comment("Durée de l'animation de fusion (en ticks)")
            .defineInRange("fusionAnimationTicks", 30, 1, 600);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
