package mielon.thesift.advancement;

import java.util.Map;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.entity.BlubEntity;
import mielon.thesift.entity.DarkSnifferEntity;
import mielon.thesift.entity.RiftEntity;
import mielon.thesift.item.ModItems;
import mielon.thesift.platform.NeoEvents.PlayerBlockBreakEvents;
import mielon.thesift.platform.NeoEvents.PlayerBlockBreakEvents.After;
import mielon.thesift.platform.NeoEvents.ServerLivingEntityEvents;
import mielon.thesift.platform.NeoEvents.ServerLivingEntityEvents.AfterDeath;
import mielon.thesift.platform.NeoEvents.ServerTickEvents;
import mielon.thesift.platform.NeoEvents.ServerTickEvents.EndTick;
import mielon.thesift.platform.NeoEvents.UseBlockCallback;
import mielon.thesift.world.RiftDirectory;
import mielon.thesift.world.TheSiftDimension;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class ModAdvancements {
   public static final String SONG_OF_THE_PAST = "story/song_of_the_past";
   public static final String FALLEN_CIVILIZATION = "story/fallen_civilization";
   public static final String CERTIFIED_SIFTER = "the_sift/certified_sifter";
   // The original NeoForge 1.21.1 The Sift 1.1.2 awards each visited Sift
   // biome using a server tick hook and impossible criteria. Preserve that
   // logic instead of depending on unvalidated JSON location predicates.
   private static final Map<Identifier, String> CERTIFIED_SIFTER_BIOMES = Map.ofEntries(
      Map.entry(Identifier.fromNamespaceAndPath("the_sift", "sift_wastes"), "sift_wastes"),
      Map.entry(Identifier.fromNamespaceAndPath("the_sift", "sift_deep_dark"), "sift_deep_dark"),
      Map.entry(Identifier.fromNamespaceAndPath("the_sift", "ichor_snowy_peaks"), "ichor_snowy_peaks"),
      Map.entry(Identifier.fromNamespaceAndPath("the_sift", "siftslate_slopes"), "siftslate_slopes"),
      Map.entry(Identifier.fromNamespaceAndPath("the_sift", "siftslate_peaks"), "siftslate_peaks"),
      Map.entry(Identifier.fromNamespaceAndPath("the_sift", "overgrown_slopes"), "overgrown_slopes"),
      Map.entry(Identifier.fromNamespaceAndPath("the_sift", "overgrown_peaks"), "overgrown_peaks"),
      Map.entry(Identifier.fromNamespaceAndPath("the_sift", "overgrown_forest_slopes"), "overgrown_forest_slopes"),
      Map.entry(Identifier.fromNamespaceAndPath("the_sift", "overgrown_forest"), "overgrown_forest"),
      Map.entry(Identifier.fromNamespaceAndPath("the_sift", "overgrown_clearing"), "overgrown_clearing")
   );
   public static final String SPICEWOOD = "the_sift/spicewood";
   public static final String LIKE_FATHER_AND_SON = "the_sift/like_father_and_son";
   public static final String PITCH_PERFECT = "the_sift/pitch_perfect";
   public static final String FRIEND_OF_THE_SIFT = "the_sift/friend_of_the_sift";
   public static final String BLUB = "the_sift/blub";
   public static final String FOOD_CHAIN = "the_sift/food_chain";
   public static final String FALSE_ALARM = "the_sift/false_alarm";
   public static final String PLANTING_THE_DARK = "the_sift/planting_the_dark";
   public static final String KINDA_PURPLEISH = "the_sift/kinda_purpleish";
   public static final String SIFTED_NUGGET = "the_sift/sifted_nugget";
   public static final String BETTER_ENCHANTER = "the_sift/better_enchanter";
   public static final String RIFTER = "the_sift/rifter";
   public static final String RIFT_RESONANCE = "the_sift/rift_resonance";
   public static final String KEEP_INVENTORY = "the_sift/keep_inventory";
   private static boolean registered;

   private ModAdvancements() {
   }

   public static void register() {
      if (!registered) {
         registered = true;
         ServerTickEvents.END_SERVER_TICK.register((EndTick)ModAdvancements::tickLocationAdvancements);
         UseBlockCallback.EVENT
            .register(
               (UseBlockCallback)(player, level, hand, hit) -> {
                  if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                     ItemStack held = player.getItemInHand(hand);
                     BlockPos clickedPos = hit.getBlockPos();
                     BlockState clicked = level.getBlockState(clickedPos);
                     if (held.is(ModItems.SCULKFLOWER_SEEDS)
                        && hit.getDirection() == Direction.UP
                        && clicked.is(Blocks.SCULK)
                        && level.getBlockState(clickedPos.above()).canBeReplaced()) {
                        award(serverPlayer, "the_sift/planting_the_dark");
                     }

                     if (level.dimension().equals(TheSiftDimension.LEVEL_KEY)
                        && (held.is(Items.FLINT_AND_STEEL) || held.is(Items.FIRE_CHARGE))
                        && isWood(clicked)) {
                        BlockPos firePos = clickedPos.relative(hit.getDirection());
                        if (level.getBlockState(firePos).canBeReplaced() && BaseFireBlock.getState(level, firePos).canSurvive(level, firePos)) {
                           award(serverPlayer, "the_sift/spicewood");
                        }
                     }

                     if (held.is(ModItems.MUSIC_DISC_RIFT)
                        && clicked.is(Blocks.JUKEBOX)
                        && level.getBlockEntity(clickedPos) instanceof JukeboxBlockEntity jukebox
                        && jukebox.getTheItem().isEmpty()
                        && isNearOpenRift(serverLevel, Vec3.atCenterOf(clickedPos), 16.0)) {
                        award(serverPlayer, "the_sift/rift_resonance");
                     }

                     return InteractionResult.PASS;
                  } else {
                     return InteractionResult.PASS;
                  }
               }
            );
         PlayerBlockBreakEvents.AFTER.register((After)(level, player, pos, state, blockEntity) -> {
            if (player instanceof ServerPlayer serverPlayer) {
               if (state.is(ModBlocks.SIFTSLATE_CHAROITE_ORE)) {
                  award(serverPlayer, "the_sift/kinda_purpleish");
               } else if (state.is(ModBlocks.SIFTSLATE_SIFTITE_ORE)) {
                  award(serverPlayer, "the_sift/sifted_nugget");
               }
            }
         });
         ServerLivingEntityEvents.AFTER_DEATH
            .register(
               (AfterDeath)(entity, source) -> {
                  if (entity instanceof DarkSnifferEntity
                     && source.getEntity() instanceof BlubEntity blub
                     && blub.isTame()
                     && blub.getOwner() instanceof ServerPlayer owner) {
                     award(owner, "the_sift/food_chain");
                  }
               }
            );
      }
   }

   private static void tickLocationAdvancements(MinecraftServer server) {
      // Match original upstream's every-16-ticks check, not every frame.
      if ((server.getTickCount() & 15) != 0) {
         return;
      }
      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         updateLocationAdvancements(player);
      }
   }

   private static void updateLocationAdvancements(ServerPlayer player) {
      if (!(player.level() instanceof ServerLevel level) || player.isSpectator()) {
         return;
      }
      level.registryAccess().lookupOrThrow(Registries.STRUCTURE)
         .get(BuiltinStructures.ANCIENT_CITY)
         .ifPresent(ancientCity -> {
            if (level.structureManager().getStructureWithPieceAt(player.blockPosition(), ancientCity.value()).isValid()) {
               awardCriterion(player, FALLEN_CIVILIZATION, "ancient_city");
            }
         });
      if (level.dimension().equals(TheSiftDimension.LEVEL_KEY)) {
         level.getBiome(player.blockPosition()).unwrapKey().ifPresent(biomeKey -> {
            String criterion = CERTIFIED_SIFTER_BIOMES.get(biomeKey.identifier());
            if (criterion != null) {
               awardCriterion(player, CERTIFIED_SIFTER, criterion);
            }
         });
      }
   }

   private static void awardCriterion(ServerPlayer player, String path, String criterion) {
      AdvancementHolder advancement = player.level().getServer().getAdvancements().get(Identifier.fromNamespaceAndPath("the_sift", path));
      if (advancement != null) {
         player.getAdvancements().award(advancement, criterion);
      }
   }

   public static void award(ServerPlayer player, String path) {
      AdvancementHolder advancement = player.level().getServer().getAdvancements().get(Identifier.fromNamespaceAndPath("the_sift", path));
      if (advancement != null) {
         player.getAdvancements().award(advancement, "event");
      }
   }

   public static void awardNearby(ServerLevel level, Vec3 center, double radius, String path) {
      AABB area = new AABB(center, center).inflate(radius);

      for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, area, playerx -> playerx.isAlive() && !playerx.isSpectator())) {
         award(player, path);
      }
   }

   private static boolean isNearOpenRift(ServerLevel level, Vec3 center, double radius) {
      double maxDistance = radius * radius;

      for (RiftEntity rift : RiftDirectory.loaded()) {
         if (!rift.isRemoved() && !rift.isClosing() && rift.level() == level && rift.position().distanceToSqr(center) <= maxDistance) {
            return true;
         }
      }

      return false;
   }

   private static boolean isWood(BlockState state) {
      return state.is(BlockTags.LOGS)
         || state.is(BlockTags.PLANKS)
         || state.is(BlockTags.WOODEN_STAIRS)
         || state.is(BlockTags.WOODEN_SLABS)
         || state.is(BlockTags.WOODEN_FENCES)
         || state.is(BlockTags.WOODEN_DOORS)
         || state.is(BlockTags.WOODEN_TRAPDOORS)
         || state.is(BlockTags.WOODEN_BUTTONS)
         || state.is(BlockTags.WOODEN_PRESSURE_PLATES)
         || state.is(BlockTags.WOODEN_SHELVES)
         || state.is(ModBlocks.OVERGROWN_WILLOW_WOOD)
         || state.is(ModBlocks.STRIPPED_OVERGROWN_WILLOW_WOOD);
   }
}
