package mielon.thesift.client.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

public final class RiftRenderTypes {
   private static final RenderPipeline SURFACE_PIPELINE = createPipeline(false);
   private static final RenderPipeline GLOW_PIPELINE = createPipeline(true);
   public static final RenderType SIFT = createType(true, false);
   public static final RenderType OVERWORLD = createType(false, false);
   public static final RenderType GLOW = createType(true, true);

   private RiftRenderTypes() {
   }

   public static void registerPipelines(RegisterRenderPipelinesEvent event) {
      event.registerPipeline(SURFACE_PIPELINE);
      event.registerPipeline(GLOW_PIPELINE);
   }

   private static RenderPipeline createPipeline(boolean glow) {
      Builder builder = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
         .withLocation(id(glow ? "pipeline/rift_glow" : "pipeline/rift_surface"))
         .withVertexShader(id("core/rift"))
         .withFragmentShader(id("core/rift"))
         .withSampler("Sampler0")
         .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
         .withDepthStencilState(new DepthStencilState(DepthStencilState.DEFAULT.depthTest(), !glow))
         .withCull(true);
      if (glow) {
         builder.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT));
      }

      return builder.build();
   }

   private static RenderType createType(boolean sift, boolean glow) {
      Identifier texture = id("textures/entity/rift/" + (sift ? "sift" : "overworld") + ".png");
      return RenderType.create(
         "sift_rift_" + sift + "_" + glow, RenderSetup.builder(glow ? GLOW_PIPELINE : SURFACE_PIPELINE).withTexture("Sampler0", texture).createRenderSetup()
      );
   }

   private static Identifier id(String path) {
      return Identifier.fromNamespaceAndPath("the_sift", path);
   }
}
