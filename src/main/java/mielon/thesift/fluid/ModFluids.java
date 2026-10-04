package mielon.thesift.fluid;

import mielon.thesift.platform.ModRegistration;
import mielon.thesift.sound.ModSounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModFluids {
   public static final FluidType ICHOR_TYPE = ModRegistration.register(
      NeoForgeRegistries.FLUID_TYPES,
      id("ichor"),
      new FluidType(
         FluidType.Properties.create()
            .descriptionId("block.the_sift.ichor")
            .lightLevel(12)
            .canSwim(true)
            .canDrown(true)
            .fallDistanceModifier(0.0F)
            .canExtinguish(true)
            .supportsBoating(true)
            .canHydrate(true)
            .isWaterLike(true)
            .sound(SoundActions.BUCKET_FILL, ModSounds.ICHOR_BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, ModSounds.ICHOR_BUCKET_EMPTY)
      ) {}
   );
   public static final ResourceKey<Fluid> ICHOR_KEY = ResourceKey.create(BuiltInRegistries.FLUID.key(), id("ichor"));
   public static final ResourceKey<Fluid> FLOWING_ICHOR_KEY = ResourceKey.create(BuiltInRegistries.FLUID.key(), id("flowing_ichor"));
   public static final FlowingFluid ICHOR = (FlowingFluid)ModRegistration.register(BuiltInRegistries.FLUID, ICHOR_KEY, new IchorFluid.Source());
   public static final FlowingFluid FLOWING_ICHOR = (FlowingFluid)ModRegistration.register(BuiltInRegistries.FLUID, FLOWING_ICHOR_KEY, new IchorFluid.Flowing());
   public static final ResourceKey<Block> ICHOR_BLOCK_KEY = ResourceKey.create(BuiltInRegistries.BLOCK.key(), id("ichor"));
   public static final LiquidBlock ICHOR_BLOCK = (LiquidBlock)ModRegistration.register(
      BuiltInRegistries.BLOCK,
      ICHOR_BLOCK_KEY,
      new LiquidBlock(FLOWING_ICHOR, Properties.ofFullCopy(Blocks.WATER).setId(ICHOR_BLOCK_KEY).lightLevel(state -> 12).noLootTable())
   );
   public static final ResourceKey<Item> ICHOR_BUCKET_KEY = ResourceKey.create(BuiltInRegistries.ITEM.key(), id("ichor_bucket"));
   public static final Item ICHOR_BUCKET = (Item)ModRegistration.register(
      BuiltInRegistries.ITEM,
      ICHOR_BUCKET_KEY,
      new IchorBucketItem(ICHOR, new net.minecraft.world.item.Item.Properties().setId(ICHOR_BUCKET_KEY).stacksTo(1).craftRemainder(Items.BUCKET))
   );

   private ModFluids() {
   }

   public static void initialize() {
      IchorState.source = ICHOR.getSource(false);
   }

   private static Identifier id(String path) {
      return Identifier.fromNamespaceAndPath("the_sift", path);
   }
}
