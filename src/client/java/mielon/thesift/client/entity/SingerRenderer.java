package mielon.thesift.client.entity;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import mielon.thesift.client.light.SingerHeadLightBridge;
import mielon.thesift.entity.SingerEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class SingerRenderer extends GeoEntityRenderer<SingerEntity, EntityRenderState> {
   private static final DataTicket<SingerEntity> SINGER_ENTITY = DataTicket.create("the_sift_singer_entity", SingerEntity.class);

   public SingerRenderer(Context context) {
      super(context, new SingerModel());
      this.withRenderLayer(new SingerSoulBlockLayer(context, this));
      this.withRenderLayer(new AutoGlowingGeoLayer(this));
      this.withRenderLayer(new SiftGlowingOutlineLayer(this));
      this.shadowRadius = 0.0F;
   }

   public void addRenderData(SingerEntity animatable, Void relatedObject, EntityRenderState renderState, float partialTick) {
      super.addRenderData(animatable, relatedObject, renderState, partialTick);
      renderState.addGeckolibData(SINGER_ENTITY, animatable);
   }

   @Override
   public boolean shouldRender(SingerEntity singer, Frustum frustum, double cameraX, double cameraY, double cameraZ, float partialTick) {
      boolean visible = super.shouldRender(singer, frustum, cameraX, cameraY, cameraZ, partialTick);
      if (!visible) {
         // Preserve offscreen lighting without moving the visible light source
         // back and forth between the body approximation and its animated head
         // locator on every rendered frame. The model locator is 34 pixels high.
         SingerHeadLightBridge.update(singer, singer.getPosition(partialTick).add(0.0, 34.0 / 16.0, 0.0));
      }
      return visible;
   }

   public void preRenderPass(RenderPassInfo<EntityRenderState> renderPassInfo, SubmitNodeCollector renderTasks) {
      SingerEntity singer = (SingerEntity)renderPassInfo.getGeckolibData(SINGER_ENTITY);
      if (singer != null) {
         renderPassInfo.addLocatorPositionListener("head_light", (worldPos, modelPos, localPos) -> {
            if (worldPos != null) {
               SingerHeadLightBridge.update(singer, worldPos);
            }
         });
      }
   }
}
