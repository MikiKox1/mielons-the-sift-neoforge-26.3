package mielon.thesift.client.entity;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;
import com.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer.RenderData;
import com.geckolib.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.entity.SingerEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public final class SingerSoulBlockLayer extends BlockAndItemGeoLayer<SingerEntity, Void, EntityRenderState> {
   public SingerSoulBlockLayer(Context context, SingerRenderer renderer) {
      super(context, renderer);
   }

   protected List<RenderData> getRelevantBones(SingerEntity singer, Void relatedObject, EntityRenderState renderState, float partialTick) {
      return singer.isHoldingSoulBlock()
         ? List.of(RenderData.block("held_soul", RenderUtil.createRenderStateForBlock(ModBlocks.SOUL_BLOCK.defaultBlockState(), this.blockModelResolver)))
         : List.of();
   }

   public void addRenderData(SingerEntity singer, Void relatedObject, EntityRenderState renderState, float partialTick) {
      renderState.addGeckolibData(CONTENTS, this.getRelevantBones(singer, relatedObject, renderState, partialTick));
   }

   protected void submitBlockRender(
      PoseStack poses, GeoBone bone, BlockModelRenderState block, EntityRenderState renderState, SubmitNodeCollector tasks, int light
   ) {
      poses.pushPose();
      poses.scale(1.75F, 1.75F, 1.75F);
      super.submitBlockRender(poses, bone, block, renderState, tasks, 15728880);
      poses.popPose();
   }
}
