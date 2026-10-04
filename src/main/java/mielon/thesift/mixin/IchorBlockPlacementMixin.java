package mielon.thesift.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mielon.thesift.fluid.IchorWaterlogging;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
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
      BlockState placementState = IchorWaterlogging.isIchor(context.getLevel().getFluidState(context.getClickedPos()).getType())
            && IchorWaterlogging.canContain(state)
         ? IchorWaterlogging.fillState(state)
         : state;
      return original.call(instance, context, placementState);
   }
}
