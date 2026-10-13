package mielon.thesift.fluid;

import mielon.thesift.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

/**
 * Keeps the original 1.1.2 Ichor regeneration behavior independent from fluid
 * entityInside hooks, so Ichor-filled cauldrons and waterlogged blocks count.
 */
public final class IchorRegeneration {
   private static final int DURATION = 101;
   private static final int REFRESH_THRESHOLD = 51;
   private MobEffectInstance contactEffect;
   private boolean wasTouching;

   public void tick(LivingEntity entity) {
      if (entity.level().isClientSide()) {
         return;
      }

      MobEffectInstance current = entity.getEffect(MobEffects.REGENERATION);
      boolean touching = isTouching(entity);
      if (!isContactEffect(current)) {
         this.contactEffect = null;
      }

      if (!touching) {
         if (this.wasTouching && current != null && current == this.contactEffect) {
            refresh(entity);
         }
         this.wasTouching = false;
         this.contactEffect = null;
      } else {
         this.wasTouching = true;
         if (current == null) {
            refresh(entity);
            this.contactEffect = entity.getEffect(MobEffects.REGENERATION);
         } else if (isContactEffect(current)) {
            this.contactEffect = current;
            if (current.getDuration() <= REFRESH_THRESHOLD) {
               refresh(entity);
            }
         }
      }
   }

   private static boolean isContactEffect(MobEffectInstance effect) {
      return effect != null
         && effect.getAmplifier() == 0
         && effect.isAmbient()
         && !effect.isVisible()
         && effect.showIcon()
         && effect.getDuration() > 0
         && effect.getDuration() <= DURATION;
   }

   private static void refresh(LivingEntity entity) {
      entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, DURATION, 0, true, false, true));
   }

   private static boolean isTouching(LivingEntity entity) {
      Level level = entity.level();
      AABB bounds = entity.getBoundingBox().deflate(0.001);
      for (BlockPos pos : BlockPos.betweenClosed(
         BlockPos.containing(bounds.minX, bounds.minY, bounds.minZ),
         BlockPos.containing(bounds.maxX, bounds.maxY, bounds.maxZ)
      )) {
         if (!level.hasChunkAt(pos)) {
            continue;
         }

         BlockState state = level.getBlockState(pos);
         FluidState fluid = state.getFluidState();
         if (fluid.getType() instanceof IchorFluid && bounds.minY < pos.getY() + fluid.getHeight(level, pos)) {
            return true;
         }

         if (state.is(ModBlocks.ICHOR_CAULDRON)) {
            double height = (6.0 + 3.0 * state.getValue(LayeredCauldronBlock.LEVEL)) / 16.0;
            if (bounds.intersects(
               pos.getX() + 0.125,
               pos.getY() + 0.25,
               pos.getZ() + 0.125,
               pos.getX() + 0.875,
               pos.getY() + height,
               pos.getZ() + 0.875
            )) {
               return true;
            }
         }
      }
      return false;
   }
}
