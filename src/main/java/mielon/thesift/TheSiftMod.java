package mielon.thesift;

import mielon.thesift.advancement.ModAdvancements;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.block.SiftPlantInteractions;
import mielon.thesift.entity.BlubEntity;
import mielon.thesift.entity.DarkSnifferEntity;
import mielon.thesift.entity.EchoGolemEntity;
import mielon.thesift.entity.ModEntities;
import mielon.thesift.entity.SifterEntity;
import mielon.thesift.entity.SingerEntity;
import mielon.thesift.entity.SingerSummoner;
import mielon.thesift.fluid.IchorWaterlogging;
import mielon.thesift.fluid.ModFluids;
import mielon.thesift.item.BlubItems;
import mielon.thesift.item.ModItems;
import mielon.thesift.particle.ModParticles;
import mielon.thesift.platform.ModRegistration;
import mielon.thesift.platform.NeoEvents;
import mielon.thesift.platform.NeoEvents.ServerLifecycleEvents;
import mielon.thesift.platform.NeoEvents.ServerLifecycleEvents.ServerStopped;
import mielon.thesift.platform.NeoEvents.ServerTickEvents;
import mielon.thesift.platform.NeoEvents.ServerTickEvents.EndTick;
import mielon.thesift.portal.PortalGrowth;
import mielon.thesift.portal.SonorousAutoplay;
import mielon.thesift.portal.SonorousConsoles;
import mielon.thesift.portal.SonorousIgnition;
import mielon.thesift.sound.ModSounds;
import mielon.thesift.world.RiftDirectory;
import mielon.thesift.world.RiftManager;
import mielon.thesift.world.SiftLocateCommand;
import mielon.thesift.world.SiftTeleportManager;
import mielon.thesift.world.SiftWeatherCommand;
import mielon.thesift.world.SiftiteRecoveryManager;
import mielon.thesift.worldgen.ModWorldgen;
import mielon.thesift.worldgen.SiftFeaturePlacementGuard;
import mielon.thesift.worldgen.SiftLandmarkTracker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(TheSiftMod.MOD_ID)
public final class TheSiftMod {
   public static final String MOD_ID = "the_sift";
   public static final Logger LOGGER = LoggerFactory.getLogger("the_sift");

   public TheSiftMod(IEventBus modBus) {
      NeoEvents.bootstrap();
      modBus.addListener(this::registerContent);
      modBus.addListener(this::commonSetup);
      modBus.addListener(TheSiftMod::registerEntityAttributes);
      modBus.addListener(TheSiftMod::registerSpawnPlacements);
      modBus.addListener(TheSiftMod::modifyDefaultComponents);
      modBus.addListener(TheSiftMod::addVanillaBlockEntityBlocks);
   }

   private void registerContent(RegisterEvent event) {
      if (event.getRegistryKey().equals(BuiltInRegistries.PARTICLE_TYPE.key())) {
         ModParticles.initialize();
      } else if (event.getRegistryKey().equals(BuiltInRegistries.SOUND_EVENT.key())) {
         ModSounds.initialize();
      } else if (event.getRegistryKey().equals(BuiltInRegistries.FLUID.key())) {
         ModFluids.initialize();
      } else if (event.getRegistryKey().equals(BuiltInRegistries.BLOCK.key())) {
         ModBlocks.bootstrap();
      } else if (event.getRegistryKey().equals(BuiltInRegistries.ENTITY_TYPE.key())) {
         ModEntities.initialize();
      } else if (event.getRegistryKey().equals(BuiltInRegistries.ITEM.key())) {
         ModItems.bootstrap();
         BlubItems.initialize();
      } else if (event.getRegistryKey().equals(BuiltInRegistries.FEATURE_TYPE.key())) {
         ModWorldgen.initialize();
      }

      ModRegistration.onRegister(event);
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      event.enqueueWork(this::onInitialize);
   }

   private void onInitialize() {
      ModBlocks.initialize();
      SiftPlantInteractions.register();
      SiftLocateCommand.register();
      SiftWeatherCommand.register();
      ModItems.initialize();
      ModAdvancements.register();
      SiftiteRecoveryManager.register();
      ServerTickEvents.START_SERVER_TICK.register(RiftDirectory::tick);
      SonorousIgnition.register();
      SingerSummoner.register();
      SonorousAutoplay.register();
      registerServerLifecycle();
      PortalGrowth.register();
      LOGGER.info("The Sift initialized.");
   }

   private static void registerEntityAttributes(EntityAttributeCreationEvent event) {
      event.put(ModEntities.SINGER, SingerEntity.createAttributes().build());
      event.put(ModEntities.ECHO_GOLEM, EchoGolemEntity.createAttributes().build());
      event.put(ModEntities.DARK_SNIFFER, DarkSnifferEntity.createAttributes().build());
      event.put(ModEntities.BLUB, BlubEntity.createAttributes().build());
      event.put(ModEntities.SIFTER, SifterEntity.createAttributes().build());
   }

   private static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
      event.register(
         ModEntities.ECHO_GOLEM,
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         EchoGolemEntity::checkEchoGolemSpawnRules,
         Operation.REPLACE
      );
      event.register(ModEntities.BLUB, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, BlubEntity::checkBlubSpawnRules, Operation.REPLACE);
      event.register(ModEntities.SIFTER, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, SifterEntity::checkSifterSpawnRules, Operation.REPLACE);
   }

   private static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
      ModBlocks.modifyDefaultComponents(event);
      ModItems.modifyDefaultComponents(event);
   }

   private static void addVanillaBlockEntityBlocks(BlockEntityTypeAddBlocksEvent event) {
      ModBlocks.addVanillaBlockEntityBlocks(event);
   }

   private static void registerServerLifecycle() {
      ServerTickEvents.END_SERVER_TICK.register((EndTick)server -> {
         SiftTeleportManager.tick(server);
         RiftManager.tick(server);
         SiftiteRecoveryManager.tick(server);
         IchorWaterlogging.tick(server);
      });
      ServerLifecycleEvents.SERVER_STOPPED.register((ServerStopped)server -> {
         SonorousConsoles.clearTransientBuffers();
         SiftTeleportManager.clearTransientState();
         RiftManager.clearTransientState();
         SiftiteRecoveryManager.clearTransientState();
         SiftFeaturePlacementGuard.clear();
         EchoGolemEntity.clearTransientState();
         IchorWaterlogging.clear();
      });
      ServerLifecycleEvents.SERVER_STOPPING.register(SiftLandmarkTracker::flush);
      ServerLifecycleEvents.SERVER_STOPPING.register(IchorWaterlogging::flush);
   }
}
