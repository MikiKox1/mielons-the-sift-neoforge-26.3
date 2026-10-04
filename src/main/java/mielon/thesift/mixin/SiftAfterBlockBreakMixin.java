package mielon.thesift.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mielon.thesift.platform.NeoEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public abstract class SiftAfterBlockBreakMixin {
   @Shadow
   protected ServerLevel level;

   @Shadow
   @Final
   protected ServerPlayer player;

   @Unique
   private BlockEntity theSift$breakingBlockEntity;

   @WrapOperation(
      method = "destroyBlock",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ServerPlayerGameMode;removeBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;ZLnet/minecraft/world/item/ItemStack;)Z"
      )
   )
   private boolean theSift$captureBlockEntityForBreak(
      ServerPlayerGameMode instance,
      BlockPos pos,
      BlockState adjustedState,
      boolean canHarvest,
      ItemStack toolStack,
      Operation<Boolean> original,
      @Local(name = "blockEntity") BlockEntity blockEntity
   ) {
      this.theSift$breakingBlockEntity = blockEntity;
      try {
         return original.call(instance, pos, adjustedState, canHarvest, toolStack);
      } finally {
         this.theSift$breakingBlockEntity = null;
      }
   }

   @WrapOperation(
      method = "removeBlock",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/Block;destroy(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
      )
   )
   private void theSift$afterSuccessfulRemoval(
      Block block,
      LevelAccessor level,
      BlockPos pos,
      BlockState adjustedState,
      Operation<Void> original
   ) {
      // Fabric's PlayerBlockBreakEvents.AFTER is injected at this exact INVOKE.
      // The block has already been removed successfully; dispatching before the
      // original Block.destroy call therefore preserves the 26.3 Fabric timing.
      NeoEvents.afterBlockBreak(this.level, this.player, pos, adjustedState, this.theSift$breakingBlockEntity);
      original.call(block, level, pos, adjustedState);
   }
}
