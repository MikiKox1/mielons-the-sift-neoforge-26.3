package mielon.thesift.client;

import mielon.thesift.TheSiftMod;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.client.compat.SiftShaderCompat;
import mielon.thesift.client.entity.BlubRenderer;
import mielon.thesift.client.entity.DarkSnifferRenderer;
import mielon.thesift.client.entity.EchoGolemRenderer;
import mielon.thesift.client.entity.MiniRiftRenderer;
import mielon.thesift.client.entity.RiftRenderer;
import mielon.thesift.client.entity.SifterRenderer;
import mielon.thesift.client.entity.SingerRenderer;
import mielon.thesift.client.loading.SiftClientPortalTransition;
import mielon.thesift.client.loading.SiftLoadingBackgroundRenderer;
import mielon.thesift.client.particle.IchorFluidParticle;
import mielon.thesift.client.particle.IchorSurfaceParticle;
import mielon.thesift.client.particle.SiftNoteParticle;
import mielon.thesift.client.particle.SiftParallaxParticle;
import mielon.thesift.client.particle.SingerSoundWaveParticle;
import mielon.thesift.client.particle.SoulParticle;
import mielon.thesift.client.particle.SoundWaveParticle;
import mielon.thesift.client.render.IchorFluidRendering;
import mielon.thesift.client.render.RiftRenderTypes;
import mielon.thesift.client.render.SiftCloudRenderPipelines;
import mielon.thesift.client.render.SiftPortalRenderer;
import mielon.thesift.client.render.SiftPortalRenderTypes;
import mielon.thesift.client.render.SiftProceduralSkyRenderer;
import mielon.thesift.client.render.SonorousBeamRenderTypes;
import mielon.thesift.client.render.SonorousBeamRenderer;
import mielon.thesift.entity.ModEntities;
import mielon.thesift.network.RiftLoadingPayload;
import mielon.thesift.particle.ModParticles;
import mielon.thesift.world.VanillaPaletteRemapper;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

@EventBusSubscriber(modid = TheSiftMod.MOD_ID, value = Dist.CLIENT)
public final class ClientModEvents {
   private static final ModelLayerLocation OVERGROWN_WILLOW_BOAT_LAYER = new ModelLayerLocation(
      Identifier.fromNamespaceAndPath("the_sift", "boat/overgrown_willow"), "main"
   );
   private static final ModelLayerLocation OVERGROWN_WILLOW_CHEST_BOAT_LAYER = new ModelLayerLocation(
      Identifier.fromNamespaceAndPath("the_sift", "chest_boat/overgrown_willow"), "main"
   );

   private ClientModEvents() {
   }

   // NeoForge 26.1.2 keeps RenderPipelines.register private. Register every
   // mod-specific Blaze3D pipeline through the official client mod event,
   // including the source's own pipelines which Fabric could register directly.
   @SubscribeEvent
   private static void registerCustomPipelines(RegisterRenderPipelinesEvent event) {
      RiftRenderTypes.registerPipelines(event);
      SiftPortalRenderTypes.registerPipelines(event);
      SonorousBeamRenderTypes.registerPipelines(event);
      SiftCloudRenderPipelines.registerPipelines(event);
      SiftProceduralSkyRenderer.registerPipelines(event);
      SiftLoadingBackgroundRenderer.registerPipelines(event);
   }

   @SubscribeEvent
   private static void clientSetup(FMLClientSetupEvent event) {
      event.enqueueWork(() -> {
         VanillaPaletteRemapper.initialize();
         SiftShaderCompat.initialize();
         SonorousBeamRenderTypes.initialize();
         SiftProceduralSkyRenderer.initialize();
         SiftCloudRenderPipelines.initialize();
      });
   }

   @SubscribeEvent
   private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
      event.registerLayerDefinition(OVERGROWN_WILLOW_BOAT_LAYER, BoatModel::createBoatModel);
      event.registerLayerDefinition(OVERGROWN_WILLOW_CHEST_BOAT_LAYER, BoatModel::createChestBoatModel);
   }

   @SubscribeEvent
   private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
      event.registerEntityRenderer(ModEntities.OVERGROWN_WILLOW_BOAT, context -> new BoatRenderer(context, OVERGROWN_WILLOW_BOAT_LAYER));
      event.registerEntityRenderer(ModEntities.OVERGROWN_WILLOW_CHEST_BOAT, context -> new BoatRenderer(context, OVERGROWN_WILLOW_CHEST_BOAT_LAYER));
      event.registerBlockEntityRenderer(ModBlocks.SIFT_PORTAL_BLOCK_ENTITY, context -> new SiftPortalRenderer());
      event.registerBlockEntityRenderer(ModBlocks.SONOROUS_DEEPSLATE_BLOCK_ENTITY, SonorousBeamRenderer::new);
      event.registerEntityRenderer(ModEntities.SINGER, SingerRenderer::new);
      event.registerEntityRenderer(ModEntities.ECHO_GOLEM, EchoGolemRenderer::new);
      event.registerEntityRenderer(ModEntities.DARK_SNIFFER, DarkSnifferRenderer::new);
      event.registerEntityRenderer(ModEntities.BLUB, BlubRenderer::new);
      event.registerEntityRenderer(ModEntities.SIFTER, SifterRenderer::new);
      event.registerEntityRenderer(ModEntities.RIFT, RiftRenderer::new);
      event.registerEntityRenderer(ModEntities.MINI_RIFT, MiniRiftRenderer::new);
      event.registerEntityRenderer(ModEntities.SIFTITE_RETURN, context -> new ThrownItemRenderer(context, 1.0F, true));
   }

   @SubscribeEvent
   private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet(ModParticles.SIFT_PARALLAX, SiftParallaxParticle.Provider::new);
      event.registerSpriteSet(ModParticles.SIFT_NOTE, SiftNoteParticle.Provider::new);
      event.registerSpriteSet(ModParticles.SOUND_WAVE, SoundWaveParticle.Provider::new);
      event.registerSpriteSet(ModParticles.SINGER_SOUND_WAVE, SingerSoundWaveParticle.Provider::new);
      event.registerSpriteSet(ModParticles.CANYON_SOUL, SoulParticle.RiseProvider::new);
      event.registerSpriteSet(ModParticles.SOUL_FRAGMENT, SoulParticle.FragmentProvider::new);
      event.registerSpriteSet(ModParticles.ICHOR_SURFACE_MIST, IchorSurfaceParticle.Provider::new);
      event.registerSpriteSet(ModParticles.ICHOR_BUBBLE, IchorFluidParticle.BubbleProvider::new);
      event.registerSpriteSet(ModParticles.ICHOR_SPLASH, IchorFluidParticle.SplashProvider::new);
      event.registerSpriteSet(ModParticles.ICHOR_RAIN_SPLASH, IchorFluidParticle.RainSplashProvider::new);
   }

   @SubscribeEvent
   private static void clientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
      VanillaPaletteRemapper.isVanillaServer = false;
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
