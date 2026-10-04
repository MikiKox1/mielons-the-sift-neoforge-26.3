package mielon.thesift.client;

import mielon.thesift.TheSiftMod;
import mielon.thesift.client.loading.SiftClientPortalTransition;
import mielon.thesift.client.render.IchorFluidRendering;
import mielon.thesift.network.RiftLoadingPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

@EventBusSubscriber(modid = TheSiftMod.MOD_ID, value = Dist.CLIENT)
public final class ClientModEvents {
   private ClientModEvents() {
   }

   @SubscribeEvent
   private static void registerPayloadHandlers(RegisterClientPayloadHandlersEvent event) {
      event.register(RiftLoadingPayload.TYPE, (payload, context) -> SiftClientPortalTransition.handle(payload));
   }

   @SubscribeEvent
   private static void registerFluidModels(RegisterFluidModelsEvent event) {
      IchorFluidRendering.register(event);
   }
}
