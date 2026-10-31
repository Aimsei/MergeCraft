package fr.aimsei.mergecraft.network;

import fr.aimsei.mergecraft.MergeCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MergeLevelSyncPayload(int entityId, int mergeLevel) implements CustomPacketPayload {
    public static final Type<MergeLevelSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MergeCraft.MODID, "merge_level_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MergeLevelSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    net.minecraft.network.codec.ByteBufCodecs.VAR_INT, MergeLevelSyncPayload::entityId,
                    net.minecraft.network.codec.ByteBufCodecs.VAR_INT, MergeLevelSyncPayload::mergeLevel,
                    MergeLevelSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
