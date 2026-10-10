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
      // LevelExtractor asks the renderer whether an entity should be rendered
      // before it creates GeckoLib's render state. Keep a body-relative
      // fallback source alive here so occlusion/frustum rejection cannot leave
      // the Singer dark or its light at an old position. A visible render pass
      // replaces this approximation with the exact animated head locator.
      SingerHeadLightBridge.update(singer, singer.getPosition(partialTick).add(0.0, singer.getBbHeight() * 0.85, 0.0));
      return super.shouldRender(singer, frustum, cameraX, cameraY, cameraZ, partialTick);
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
