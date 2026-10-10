package mielon.thesift.world;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import mielon.thesift.TheSiftMod;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.network.RiftLoadingPayload;
import mielon.thesift.sound.ModSounds;
import mielon.thesift.worldgen.SiftAllaySpawner;
import mielon.thesift.worldgen.SiftDeferredSnifferSpawner;
import mielon.thesift.worldgen.SiftEchoGolemSpawner;
import mielon.thesift.worldgen.SiftLandmarkTracker;
import mielon.thesift.worldgen.SiftSifterSpawner;
import mielon.thesift.worldgen.SiftSoulCanyonParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class SiftTeleportManager {
   private static final Set<UUID> WAITING_FOR_EXIT = new HashSet<>();
   private static final Set<UUID> PORTAL_GUARD_CHECKED = new HashSet<>();
   private static final Set<UUID> INITIALIZED_PLAYERS = new HashSet<>();
   private static final Set<UUID> PORTAL_CONTACTS = new HashSet<>();
   private static int cleanupCooldown;

   private SiftTeleportManager() {
   }

   public static void tick(MinecraftServer server) {
      SiftLandmarkTracker.tick(server);
      SiftSoulCanyonParticles.tick(server);
      ServerLevel sift = server.getLevel(TheSiftDimension.LEVEL_KEY);
      if (sift != null && (Boolean)sift.getGameRules().get(GameRules.SPAWN_MOBS)) {
         SiftDeferredSnifferSpawner.tick(server);
         SiftEchoGolemSpawner.tick(server);
         SiftAllaySpawner.tick(server);
         SiftSifterSpawner.tick(server);
      }

      ServerLevel overworld = server.getLevel(Level.OVERWORLD);
      if (overworld != null && sift != null) {
         for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            processPlayer(server, player);
         }

         processPortalContacts(server, overworld);
         releaseNonPlayerGuards(server, overworld);
         if (--cleanupCooldown <= 0) {
            cleanupCooldown = 600;
            WAITING_FOR_EXIT.removeIf(id -> overworld.getEntityInAnyDimension(id) == null);
            PORTAL_GUARD_CHECKED.removeIf(id -> overworld.getEntityInAnyDimension(id) == null);
            INITIALIZED_PLAYERS.removeIf(id -> server.getPlayerList().getPlayer(id) == null);
         }
      }
   }

   public static void playFirstIntro(ServerPlayer player) {
      MinecraftServer server = player.level().getServer();
      if (server != null && player.level().dimension().equals(TheSiftDimension.LEVEL_KEY) && SiftWorldStorage.claimFirstSiftEntry(server, player.getUUID())) {
         player.connection
            .send(
               new ClientboundSoundEntityPacket(
                  BuiltInRegistries.SOUND_EVENT.wrapAsHolder(ModSounds.SIFT_INTRO), SoundSource.AMBIENT, player, 0.4F, 1.0F, player.getRandom().nextLong()
               )
            );
      }
   }

   public static void clearTransientState() {
      WAITING_FOR_EXIT.clear();
      PORTAL_GUARD_CHECKED.clear();
      INITIALIZED_PLAYERS.clear();
      PORTAL_CONTACTS.clear();
      cleanupCooldown = 0;
      SiftDeferredSnifferSpawner.clear();
      SiftLandmarkTracker.clearTransientState();
      SiftSoulCanyonParticles.clear();
      SiftEchoGolemSpawner.clear();
      SiftAllaySpawner.clear();
      SiftSifterSpawner.clear();
   }

   public static void suppressUntilExit(Entity entity) {
      entity.getSelfAndPassengers().forEach(passenger -> {
         WAITING_FOR_EXIT.add(passenger.getUUID());
         PORTAL_GUARD_CHECKED.add(passenger.getUUID());
         SiftWorldStorage.markPortalExitRequired(passenger);
      });
      PortalTransitGuard.block(entity);
   }

   public static boolean isPortalExitBlocked(MinecraftServer server, Entity entity) {
      boolean blocked = false;
      for (Entity passenger : entity.getSelfAndPassengers().toList()) {
         blocked |= isPortalExitBlocked(server, passenger.getUUID(), passenger);
      }

      return blocked;
   }

   private static boolean isPortalExitBlocked(MinecraftServer server, UUID id, Entity entity) {
      if (PORTAL_GUARD_CHECKED.add(id) && SiftWorldStorage.requiresPortalExit(server, id)) {
         WAITING_FOR_EXIT.add(id);
      }

      if (!WAITING_FOR_EXIT.contains(id)) {
         return false;
      }

      if (!isTouchingPortal(entity) && !RiftManager.isTouchingRift(entity)) {
         WAITING_FOR_EXIT.remove(id);
         SiftWorldStorage.clearPortalExitRequired(server, id);
         return false;
      }

      return true;
   }

   private static void processPlayer(MinecraftServer server, ServerPlayer player) {
      if (player.isAlive()) {
         boolean touchingPortal = isTouchingPortal(player);
         UUID id = player.getUUID();
         if (player.isSpectator()) {
            if (touchingPortal) {
               WAITING_FOR_EXIT.add(id);
            }
         } else if (INITIALIZED_PLAYERS.add(id)) {
            if (touchingPortal) {
               suppressUntilExit(player);
            }
         } else {
            processTouchState(server, player, touchingPortal);
         }
      }
   }

   public static void onPortalContact(Entity entity) {
      if (!(entity instanceof ServerPlayer) && !entity.isRemoved() && entity.isAlive() && entity.level() instanceof ServerLevel) {
         PORTAL_CONTACTS.add(entity.getUUID());
      }
   }

   private static void processPortalContacts(MinecraftServer server, ServerLevel overworld) {
      if (!PORTAL_CONTACTS.isEmpty()) {
         Set<UUID> contacts = new HashSet<>(PORTAL_CONTACTS);
         PORTAL_CONTACTS.clear();

         for (UUID id : contacts) {
            Entity entity = overworld.getEntityInAnyDimension(id);
            if (entity != null && !(entity instanceof ServerPlayer) && !entity.isRemoved() && entity.isAlive()) {
               processTouchState(server, entity, true);
            }
         }
      }
   }

   private static void releaseNonPlayerGuards(MinecraftServer server, ServerLevel overworld) {
      Iterator<UUID> iterator = WAITING_FOR_EXIT.iterator();

      while (iterator.hasNext()) {
         UUID id = iterator.next();
         Entity entity = overworld.getEntityInAnyDimension(id);
         if (entity != null
            && !(entity instanceof ServerPlayer)
            && !PortalTransitGuard.isBlocked(entity)
            && !isTouchingPortal(entity)
            && !RiftManager.isTouchingRift(entity)) {
            iterator.remove();
            SiftWorldStorage.clearPortalExitRequired(server, id);
         }
      }
   }

   private static void processTouchState(MinecraftServer server, Entity entity, boolean touchingPortal) {
      if (!PortalTransitGuard.isBlocked(entity)) {
         if (isPortalExitBlocked(server, entity)) {
            return;
         }

         if (touchingPortal) {
            SiftTeleportManager.ResourceKeyMatch dimension = classifyDimension(entity.level());
            if (dimension == SiftTeleportManager.ResourceKeyMatch.OVERWORLD || dimension == SiftTeleportManager.ResourceKeyMatch.THE_SIFT) {
               ServerPlayer player = entity instanceof ServerPlayer serverPlayer ? serverPlayer : null;
               if (player != null) {
                  RiftLoadingPayload.send(player, true, false);
               }

               boolean transferring = false;

               try {
                  transferring = dimension == SiftTeleportManager.ResourceKeyMatch.OVERWORLD
                     ? teleportIntoSift(server, entity)
                     : teleportBackToOverworld(server, entity);
               } finally {
                  if (!transferring && player != null) {
                     RiftLoadingPayload.send(player, false, false);
                  }
               }
            }
         }
      }
   }

   private static boolean teleportIntoSift(MinecraftServer server, Entity entity) {
      ServerLevel sift = server.getLevel(TheSiftDimension.LEVEL_KEY);
      if (sift == null) {
         return false;
      }

      Optional<BlockPos> anchorOptional = SiftPortalStructure.ensurePortal(server, sift);
      if (anchorOptional.isEmpty()) {
         return false;
      }

      SiftWorldStorage.saveReturnPoint(entity);
      BlockPos anchor = anchorOptional.get();
      Vec3 destination = new Vec3(anchor.getX() + 0.5, anchor.getY(), anchor.getZ() + 0.5);
      Entity teleported = teleport(entity, sift, destination, entity.getYRot(), entity.getXRot());
      if (teleported != null) {
         suppressUntilExit(teleported);
      }

      return teleported != null;
   }

   private static boolean teleportBackToOverworld(MinecraftServer server, Entity entity) {
      ServerLevel overworld = server.getLevel(Level.OVERWORLD);
      if (overworld == null) {
         return false;
      }

      // Prefer the active one-shot return ticket. If Rift entry cleared it,
      // use the last real main-portal entrance, not a nearby temporary Rift.
      UUID entityId = entity.getUUID();
      Optional<SiftWorldStorage.ReturnPoint> oneShot = SiftWorldStorage.getReturnPoint(server, entityId);
      Optional<SiftWorldStorage.ReturnPoint> durable = oneShot.isPresent()
         ? Optional.empty()
         : SiftWorldStorage.getMainPortalEntrance(server, entityId);
      Optional<SiftWorldStorage.ReturnPoint> stored = oneShot.isPresent() ? oneShot : durable;
      String routeSource = oneShot.isPresent() ? "one_shot" : durable.isPresent() ? "durable" : "no_data";
      if (stored.isPresent()) {
         SiftWorldStorage.ReturnPoint point = stored.get();
         BlockPos entrance = point.portalBlock();
         overworld.getChunk(entrance);
         boolean portalStillOpen = overworld.getBlockState(entrance).is(ModBlocks.SIFT_PORTAL);
         TheSiftMod.LOGGER.info(
            "Sift MAIN exit entity={} source={} targetPortal={} active={}", entityId, routeSource, entrance, portalStillOpen
         );
         Vec3 destination;
         if (portalStillOpen) {
            destination = new Vec3(point.x(), point.y(), point.z());
         } else {
            // The main portal may have been deliberately closed while the traveler
            // was in The Sift. Its remembered Ancient City position is still the
            // authoritative route target; only adjust the exact landing block for
            // collision/safety instead of silently converting a MAIN exit to a Rift.
            BlockPos safe = entity.adjustSpawnLocation(overworld, entrance);
            destination = new Vec3(safe.getX() + 0.5, safe.getY(), safe.getZ() + 0.5);
         }

         float yaw = point.yaw();
         float pitch = point.pitch();
         Entity teleported = teleport(entity, overworld, destination, yaw, pitch);
         TheSiftMod.LOGGER.info(
            "Sift MAIN exit result entity={} source={} success={} destination={}", entityId, routeSource, teleported != null, destination
         );
         if (teleported != null) {
            if (portalStillOpen) {
               suppressUntilExit(teleported);
            } else {
               PortalTransitGuard.block(teleported);
            }

            SiftWorldStorage.clearReturnPoint(server, entity.getUUID());
         }

         return teleported != null;
      } else {
         // A MAIN portal must never change route families merely because an old
         // world has already lost its historical return ticket. Once those
         // coordinates are gone they cannot be reconstructed exactly; return to
         // a safe Overworld spawn instead of manufacturing an unrelated Rift.
         BlockPos anchor = RiftManager.overworldAnchor(server, entity);
         overworld.getChunk(anchor);
         BlockPos safe = entity.adjustSpawnLocation(overworld, anchor);
         TheSiftMod.LOGGER.warn(
            "Sift MAIN exit entity={} source=no_data; using direct Overworld spawn fallback at {}", entityId, safe
         );
         Entity teleported = teleport(
            entity, overworld, new Vec3(safe.getX() + 0.5, safe.getY(), safe.getZ() + 0.5), entity.getYRot(), entity.getXRot()
         );
         TheSiftMod.LOGGER.info(
            "Sift MAIN exit result entity={} source=no_data success={} destination={}",
            entityId,
            teleported != null,
            new Vec3(safe.getX() + 0.5, safe.getY(), safe.getZ() + 0.5)
         );
         if (teleported != null) {
            PortalTransitGuard.block(teleported);
         }

         return teleported != null;
      }
   }

   private static Entity teleport(Entity entity, ServerLevel destinationLevel, Vec3 destination, float yaw, float pitch) {
      return entity.teleport(
         new TeleportTransition(
            destinationLevel, destination, Vec3.ZERO, yaw, pitch, TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET)
         )
      );
   }

   public static boolean isTouchingPortal(Entity entity) {
      AABB box = entity.getBoundingBox().deflate(1.0E-7);
      Level level = entity.level();
      int minX = Mth.floor(box.minX);
      int minY = Mth.floor(box.minY);
      int minZ = Mth.floor(box.minZ);
      int maxX = Mth.floor(box.maxX);
      int maxY = Mth.floor(box.maxY);
      int maxZ = Mth.floor(box.maxZ);
      MutableBlockPos pos = new MutableBlockPos();

      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               pos.set(x, y, z);
               if (level.getBlockState(pos).is(ModBlocks.SIFT_PORTAL)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private static SiftTeleportManager.ResourceKeyMatch classifyDimension(Level level) {
      if (level.dimension().equals(Level.OVERWORLD)) {
         return SiftTeleportManager.ResourceKeyMatch.OVERWORLD;
      } else {
         return level.dimension().equals(TheSiftDimension.LEVEL_KEY)
            ? SiftTeleportManager.ResourceKeyMatch.THE_SIFT
            : SiftTeleportManager.ResourceKeyMatch.OTHER;
      }
   }

   private enum ResourceKeyMatch {
      OVERWORLD,
      THE_SIFT,
      OTHER;
   }
}
