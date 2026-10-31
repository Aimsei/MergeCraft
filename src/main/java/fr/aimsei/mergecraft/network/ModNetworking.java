package fr.aimsei.mergecraft.network;

import fr.aimsei.mergecraft.MergeCraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MergeCraft.MODID)
public class ModNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MergeCraft.MODID);
        registrar.playToClient(MergeLevelSyncPayload.TYPE, MergeLevelSyncPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> MergeLevelSyncHandler.handle(payload)));
    }
}
