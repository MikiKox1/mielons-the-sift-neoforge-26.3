package mielon.thesift.mixin;

import mielon.thesift.fluid.IchorWaterlogging;
import mielon.thesift.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleWaterloggedBlock.class)
public interface IchorSimpleWaterloggedMixin {
   @Inject(method = "canPlaceLiquid", at = @At("HEAD"), cancellable = true)
   private void theSift$canFill(LivingEntity user, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
      if (IchorWaterlogging.isIchor(fluid)) {
         // Match vanilla waterlogging: only a source fluid may enter a
         // waterloggable block. Accepting FLOWING_ICHOR here lets natural flow
         // become a bucketable source as soon as it reaches the block.
         cir.setReturnValue(IchorWaterlogging.isIchorSource(fluid) && IchorWaterlogging.canFill(state));
      }
   }

   @Inject(method = "placeLiquid", at = @At("HEAD"), cancellable = true)
   private void theSift$fill(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluid, CallbackInfoReturnable<Boolean> cir) {
      if (IchorWaterlogging.isIchor(fluid.getType())) {
         cir.setReturnValue(fluid.isSource() && IchorWaterlogging.fill(level, pos, state));
      }
   }

   @Inject(method = "pickupBlock", at = @At("HEAD"), cancellable = true)
   private void theSift$drain(LivingEntity user, LevelAccessor level, BlockPos pos, BlockState state, CallbackInfoReturnable<ItemStack> cir) {
      if (IchorWaterlogging.isIchorlogged(state)) {
         cir.setReturnValue(IchorWaterlogging.drain(level, pos, state) ? new ItemStack(ModFluids.ICHOR_BUCKET) : ItemStack.EMPTY);
      }
   }
}
