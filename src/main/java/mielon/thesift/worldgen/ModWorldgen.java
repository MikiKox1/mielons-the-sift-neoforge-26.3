package mielon.thesift.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;

public final class ModWorldgen {
   public static final MapCodec<SiftMonolithFeature> LUSH_MONOLITH = register("lush_monolith", SiftMonolithFeature.LUSH_CODEC);
   public static final MapCodec<SiftMonolithFeature> BARE_MONOLITH = register("bare_monolith", SiftMonolithFeature.BARE_CODEC);
   public static final MapCodec<SiftArchFeature> CLIFF_ARCH = register("cliff_arch", SiftArchFeature.CODEC);
   public static final MapCodec<SiftDrySpikeFeature> DRY_SCULK_SPIKES = register("dry_sculk_spikes", SiftDrySpikeFeature.CODEC);
   public static final MapCodec<OvergrownWillowTreeFeature> OVERGROWN_WILLOW_TREE = register("overgrown_willow_tree", OvergrownWillowTreeFeature.CODEC);
   public static final MapCodec<CoveredGrowthCleanupFeature> COVERED_GROWTH_CLEANUP = register("covered_growth_cleanup", CoveredGrowthCleanupFeature.CODEC);
   public static final MapCodec<AbandonedMainPortalFeature> ABANDONED_MAIN_PORTAL = register("abandoned_main_portal", AbandonedMainPortalFeature.CODEC);
   public static final MapCodec<SnifferCaveFeature> SNIFFER_CAVE_OVERGROWN = register("sniffer_cave_overgrown", SnifferCaveFeature.OVERGROWN_CODEC);
   public static final MapCodec<SnifferCaveFeature> SNIFFER_CAVE_WASTES = register("sniffer_cave_wastes", SnifferCaveFeature.WASTES_CODEC);
   public static final MapCodec<SnifferPlantPatchFeature> SNIFFER_PLANT_PATCH = register("sniffer_plant_patch", SnifferPlantPatchFeature.CODEC);
   public static final MapCodec<SiftFlowerPatchFeature> SIFT_FLOWER_PATCH = register("sift_flower_patch", SiftFlowerPatchFeature.CODEC);
   public static final MapCodec<SiftslatePlantPatchFeature> SIFTSLATE_PLANT_PATCH = register("siftslate_plant_patch", SiftslatePlantPatchFeature.CODEC);
   public static final MapCodec<SoulCanyonFeature> SOUL_CANYON = register("soul_canyon", SoulCanyonFeature.CODEC);
   public static final MapCodec<IchorLakeFeature> ICHOR_LAKE = register("ichor_lake", IchorLakeFeature.CODEC);
   public static final MapCodec<SiftLavaFloorFeature> LAVA_FLOOR = register("lava_floor", SiftLavaFloorFeature.CODEC);
   public static final MapCodec<SiftSurfaceSculkRegionFeature> SURFACE_SCULK_REGION = register("surface_sculk_region", SiftSurfaceSculkRegionFeature.CODEC);
   public static final MapCodec<SiftIchorSnowFeature> ICHOR_SNOW = register("ichor_snow", SiftIchorSnowFeature.CODEC);
   public static final MapCodec<SiftIchorCaveSpringFeature> ICHOR_CAVE_SPRING = register("ichor_cave_spring", SiftIchorCaveSpringFeature.CODEC);

   private ModWorldgen() {
   }

   private static <F extends Feature> MapCodec<F> register(String name, MapCodec<F> codec) {
      return (MapCodec<F>)Registry.register(BuiltInRegistries.FEATURE_TYPE, Identifier.fromNamespaceAndPath("the_sift", name), codec);
   }

   public static void initialize() {
   }
}
