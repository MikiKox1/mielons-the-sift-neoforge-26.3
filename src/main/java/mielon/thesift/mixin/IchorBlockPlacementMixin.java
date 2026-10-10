package mielon.thesift.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mielon.thesift.fluid.IchorWaterlogging;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public abstract class IchorBlockPlacementMixin {
   @WrapOperation(
      method = "place",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z"
      )
   )
   private boolean theSift$placeInIchor(
      BlockItem instance,
      BlockPlaceContext context,
      BlockState state,
      Operation<Boolean> original
   ) {
      FluidState existing = context.getLevel().getFluidState(context.getClickedPos());
      // A flowing Ichor volume is not a bucketable source. Placing a
      // waterloggable block into that flow must never mint a new source.
      BlockState placementState = IchorWaterlogging.isIchor(existing.getType())
            && existing.isSource()
            && IchorWaterlogging.canContain(state)
         ? IchorWaterlogging.fillState(state)
         : state;
      return original.call(instance, context, placementState);
   }
}
