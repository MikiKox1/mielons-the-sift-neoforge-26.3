package mielon.thesift.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public final class SculkflowerBlock extends CactusFlowerBlock {
   public SculkflowerBlock(Properties properties) {
      super(properties);
   }

   protected boolean mayPlaceOn(BlockState floor, BlockGetter level, BlockPos floorPos) {
      return floor.is(Blocks.FARMLAND)
         || floor.is(Blocks.SCULK)
         || floor.is(ModBlocks.HEALTHY_SCULK)
         || floor.is(ModBlocks.DRY_HEALTHY_SCULK)
         || super.mayPlaceOn(floor, level, floorPos);
   }
}
