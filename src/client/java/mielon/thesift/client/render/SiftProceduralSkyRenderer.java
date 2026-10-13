package mielon.thesift.client.render;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderSystem.AutoStorageIndexBuffer;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;
import java.util.OptionalDouble;
import mielon.thesift.TheSiftMod;
import mielon.thesift.client.compat.SiftShaderCompat;
import mielon.thesift.world.SiftDayNightCycle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;

public final class SiftProceduralSkyRenderer {
   private static final int AZIMUTH_SEGMENTS = 192;
   private static final int ELEVATION_SEGMENTS = 112;
   private static final int VERTEX_COUNT = 86016;
   // Complementary Unbound's gbuffers_basic forwards glColor as a *flat*
   // varying. Finer tessellation near the zenith keeps those provoking-vertex
   // color steps from appearing as wide concentric rings and visible diagonal
   // triangles. Vanilla sky keeps the original inexpensive smooth mesh.
   private static final int SHADER_MID_START_ROW = 89;
   private static final int SHADER_TOP_START_ROW = 102;
   // Keep one azimuth resolution across adjacent elevation bands. Changing
   // it from row to row would produce slightly mismatched polygonal rims.
   private static final int SHADER_AZIMUTH_SEGMENTS = 256;
   private static final int SHADER_MID_SUBDIVISIONS = 2;
   private static final int SHADER_TOP_SUBDIVISIONS = 4;
   private static final int SHADER_VERTEX_COUNT = shaderQuadCount() * 4;
   private static final float RADIUS = 99.0F;
   private static final float TAU = (float) (Math.PI * 2);
   private static final float ART_BOTTOM_DEGREES = -14.0F;
   private static final float ART_TOP_DEGREES = 90.0F;
   private static final float DOME_BOTTOM_DEGREES = -90.0F;
   private static final int BASE_DAY = 0;
   private static final int BASE_NIGHT = 1;
   private static final int FAR_DAY = 2;
   private static final int FAR_NIGHT = 3;
   private static final int NEAR_DAY = 4;
   private static final int NEAR_NIGHT = 5;
   private static final GpuBuffer[] BUFFERS = new GpuBuffer[6];
   // Share one position/UV vertex buffer among six shaderpack-only RGBA
   // textures. The original shaderless POSITION_COLOR buffers stay untouched.
   private static GpuBuffer TEXTURED_DOME_BUFFER;
   private static int lastSkyRenderPath = -1;
   private static boolean photonSkyQueued;
   private static ClientLevel photonSkyLevel;
   private static AutoStorageIndexBuffer photonSkyQuadIndices;
   private static final Matrix4f PHOTON_SKY_POSE = new Matrix4f();
   private static final Matrix4f PHOTON_SKY_MODELVIEW = new Matrix4f();
   private static final Identifier[] SKY_TEXTURES = {
      texture("base_day"), texture("base_night"),
      texture("ribbons_far_day"), texture("ribbons_far_night"),
      texture("ribbons_near_day"), texture("ribbons_near_night")
   };
   private static final RenderPipeline TEXTURED_SKY_PIPELINE =
      RenderPipeline.builder(new Snippet[]{RenderPipelines.DEBUG_FILLED_SNIPPET})
         .withLocation(Identifier.fromNamespaceAndPath("the_sift", "pipeline/procedural_sky_textured_alpha2"))
         .withVertexShader("core/position_tex")
         .withFragmentShader("core/position_tex")
         .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
         .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
         .withCull(false)
         .build();
   // In Photon, an unmapped native sky pass *before* Iris final composition
   // (ALPHA3) was overwritten and invisible. ALPHA4 draws the same six baked
   // UV textures AFTER Iris final composition, exclusively where the main
   // depth buffer is still clear (reversed-Z depth == 0). This should preserve
   // nearby terrain and Photon volumetric clouds. The backend retains depth
   // from the world according to Iris FinalPassRenderer.
   private static final RenderPipeline PHOTON_POST_FINAL_SKY_PIPELINE =
      RenderPipeline.builder(new Snippet[]{RenderPipelines.DEBUG_FILLED_SNIPPET})
         .withLocation(Identifier.fromNamespaceAndPath("the_sift", "pipeline/photon_post_final_sky_alpha4"))
         .withVertexShader(Identifier.fromNamespaceAndPath("the_sift", "core/photon_sky_overlay"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("the_sift", "core/photon_sky_overlay"))
         .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
         .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
         .withDepthStencilState(new DepthStencilState(CompareOp.EQUAL, false))
         .withCull(false)
         .build();
   private static final float[][] DAY_PALETTE = new float[][]{
      {1.0F, 0.61F, 0.76F}, {0.3F, 0.94F, 0.97F}, {0.52F, 1.0F, 0.8F}, {0.91F, 0.73F, 1.0F}, {1.0F, 0.82F, 0.64F}
   };
   private static final float[][] NIGHT_PALETTE = new float[][]{
      {0.22F, 0.76F, 1.0F}, {0.13F, 0.96F, 1.0F}, {0.36F, 0.47F, 1.0F}, {0.72F, 0.35F, 1.0F}, {0.92F, 0.38F, 0.88F}
   };

   private SiftProceduralSkyRenderer() {
   }

   private static Identifier texture(String name) {
      return Identifier.fromNamespaceAndPath("the_sift", "textures/sky/alpha2/" + name + ".png");
   }

   public static void initialize() {
      // Iris TEXTURED uses gbuffers_textured / interpolated UVs, bypassing the
      // flat glColor in certain gbuffers_basic shaderpack programs. The actual
      // Photon/Complementary appearance still requires user in-world testing.
      SiftShaderCompat.assignPipeline(TEXTURED_SKY_PIPELINE, "TEXTURED");
      // No Iris shader override for our Photon post-final native pipeline.
   }

   public static void registerPipelines(RegisterRenderPipelinesEvent event) {
      event.registerPipeline(TEXTURED_SKY_PIPELINE);
      event.registerPipeline(PHOTON_POST_FINAL_SKY_PIPELINE);
   }

   // MC26.2 SkyRenderer owns its RenderTarget and creates an independent
   // Blaze3D RenderPass. MC26.3 passed a RenderPearl pass from the caller.
   // Preserve the Port 1.1.2 dual-density shader mesh and color corrections.
   public static void render(PoseStack poseStack, ClientLevel level, RenderTarget renderTarget, AutoStorageIndexBuffer quadIndices) {
      if (SiftShaderCompat.isShaderPackInUse() && SiftShaderCompat.isPhotonShaderPackSelected()) {
         // The Iris final pass overwrites direct native draws made during
         // SkyRenderer.renderSunMoonAndStars. Capture exact camera transforms
         // and defer this frame's sky until after final composition.
         photonSkyQueued = true;
         photonSkyLevel = level;
         photonSkyQuadIndices = quadIndices;
         PHOTON_SKY_POSE.set(poseStack.last().pose());
         PHOTON_SKY_MODELVIEW.set(RenderSystem.getModelViewStack());
         return;
      }
      renderActual(poseStack, level, renderTarget, quadIndices, false);
   }

   /** Called only by an optional Iris @Pseudo mixin at final-pass TAIL. */
   public static void renderAfterPhotonFinalPass() {
      if (!photonSkyQueued) {
         return;
      }
      photonSkyQueued = false;
      ClientLevel level = Minecraft.getInstance().level;
      if (level == null || level != photonSkyLevel
         || !SiftShaderCompat.isShaderPackInUse()
         || !SiftShaderCompat.isPhotonShaderPackSelected()) {
         photonSkyLevel = null;
         photonSkyQuadIndices = null;
         return;
      }

      PoseStack pose = new PoseStack();
      pose.last().pose().set(PHOTON_SKY_POSE);
      Matrix4fStack modelView = RenderSystem.getModelViewStack();
      modelView.pushMatrix();
      modelView.set(PHOTON_SKY_MODELVIEW);
      try {
         renderActual(
            pose, level, Minecraft.getInstance().gameRenderer.mainRenderTarget(),
            photonSkyQuadIndices, true
         );
      } finally {
         modelView.popMatrix();
         photonSkyLevel = null;
         photonSkyQuadIndices = null;
      }
   }

   private static void renderActual(
      PoseStack poseStack, ClientLevel level, RenderTarget renderTarget,
      AutoStorageIndexBuffer quadIndices, boolean photonPostFinal
   ) {
      boolean shadersActive = SiftShaderCompat.isShaderPackInUse();
      int renderPath = !shadersActive ? 0 : (photonPostFinal ? 4 : 1);
      if (renderPath != lastSkyRenderPath) {
         lastSkyRenderPath = renderPath;
         TheSiftMod.LOGGER.info(
            "The Sift ALPHA4 active sky render path: {}",
            renderPath == 0 ? "vanilla POSITION_COLOR"
               : (renderPath == 4 ? "Photon post-final depth-masked UV sky (experimental)" : "Iris TEXTURED (ALPHA2)")
         );
      }
      ensureBuffers(shadersActive);
      GpuBuffer[] buffers = BUFFERS;
      long ticks = level.getDefaultClockTime();
      float night = SiftDayNightCycle.nightBlend(ticks);
      float day = 1.0F - night;
      float weather = 1.0F - level.getRainLevel(1.0F) * 0.16F;
      float time = (float)ticks / 20.0F;
      // After Photon has composited its own moon, stars and cloud layers,
      // preserve some of that underlying information instead of painting a
      // fully opaque replacement sky. The ribbons retain their original alpha.
      float photonBaseBlend = photonPostFinal ? 0.72F : 1.0F;
      GpuBufferSlice[] transforms = new GpuBufferSlice[]{
         transform(poseStack, time * 1.0E-4F, 0.0F, weather, photonBaseBlend),
         transform(poseStack, time * 1.0E-4F, 0.0F, weather, night * photonBaseBlend),
         transform(poseStack, time * 0.00135F, (float)Math.sin(time * 0.004F) * 0.01F, weather, day * 0.96F),
         transform(poseStack, time * 0.00135F, (float)Math.sin(time * 0.004F) * 0.01F, weather, night * 0.96F),
         transform(poseStack, -time * 0.00205F, (float)Math.sin(time * 0.0065F + 1.8F) * 0.016F, weather, day * 0.78F),
         transform(poseStack, -time * 0.00205F, (float)Math.sin(time * 0.0065F + 1.8F) * 0.016F, weather, night * 0.82F)
      };
      int indexCount = PrimitiveTopology.QUADS.indexCount(shadersActive ? SHADER_VERTEX_COUNT : VERTEX_COUNT);
      GpuBuffer indexBuffer = quadIndices.getBuffer(indexCount);
      try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
         () -> "The Sift procedural sky",
         renderTarget.getColorTextureView(),
         Optional.empty(),
         renderTarget.getDepthTextureView(),
         OptionalDouble.empty()
      )) {
         pass.setPipeline(!shadersActive
            ? RenderPipelines.DEBUG_QUADS
            : (photonPostFinal ? PHOTON_POST_FINAL_SKY_PIPELINE : TEXTURED_SKY_PIPELINE));
         RenderSystem.bindDefaultUniforms(pass);
         pass.setIndexBuffer(indexBuffer, quadIndices.type());

         for (int layer = 0; layer < buffers.length; layer++) {
            float alpha = layer == 0 ? 1.0F : (layer == 1 ? night : (layer % 2 == 0 ? day : night));
            if (!(alpha <= 0.001F)) {
               pass.setUniform("DynamicTransforms", transforms[layer]);
               if (shadersActive) {
                  // SimpleTexture is owned and reloaded by TextureManager.
                  // No image uploads, VRAM allocations or gradient computation
                  // take place per frame in this code path.
                  AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(SKY_TEXTURES[layer]);
                  pass.bindTexture(
                     "Sampler0", texture.getTextureView(),
                     RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)
                  );
                  pass.setVertexBuffer(0, TEXTURED_DOME_BUFFER.slice());
               } else {
                  pass.setVertexBuffer(0, buffers[layer].slice());
               }
               pass.drawIndexed(indexCount, 1, 0, 0, 0);
            }
         }
      }
   }

   public static void close() {
      photonSkyQueued = false;
      photonSkyLevel = null;
      photonSkyQuadIndices = null;
      closeBuffers(BUFFERS);
      if (TEXTURED_DOME_BUFFER != null) {
         TEXTURED_DOME_BUFFER.close();
         TEXTURED_DOME_BUFFER = null;
      }
   }

   private static void closeBuffers(GpuBuffer[] buffers) {
      for (int i = 0; i < buffers.length; i++) {
         if (buffers[i] != null) {
            buffers[i].close();
            buffers[i] = null;
         }
      }
   }

   private static void ensureBuffers(boolean shaderMesh) {
      if (shaderMesh) {
         if (TEXTURED_DOME_BUFFER == null) {
            TEXTURED_DOME_BUFFER = buildTexturedDome();
         }
      } else if (BUFFERS[0] == null) {
         BUFFERS[0] = buildDome(false, 0, false);
         BUFFERS[1] = buildDome(true, 0, false);
         BUFFERS[2] = buildDome(false, 1, false);
         BUFFERS[3] = buildDome(true, 1, false);
         BUFFERS[4] = buildDome(false, 2, false);
         BUFFERS[5] = buildDome(true, 2, false);
      }
   }

   private static GpuBuffer buildTexturedDome() {
      // Preserve the ALPHA1 high-density shader tessellation as control
      // geometry. The six independent layers share a single UV mesh.
      int byteCount = SHADER_VERTEX_COUNT * DefaultVertexFormat.POSITION_TEX.getVertexSize();
      try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(byteCount)) {
         BufferBuilder builder = new BufferBuilder(bytes, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX);
         for (int y = 0; y < ELEVATION_SEGMENTS; y++) {
            int subdivisions = shaderElevationSubdivisions(y);
            for (int sub = 0; sub < subdivisions; sub++) {
               float v0 = (y + sub / (float)subdivisions) / ELEVATION_SEGMENTS;
               float v1 = (y + (sub + 1) / (float)subdivisions) / ELEVATION_SEGMENTS;
               for (int x = 0; x < SHADER_AZIMUTH_SEGMENTS; x++) {
                  float u0 = x / (float)SHADER_AZIMUTH_SEGMENTS;
                  float u1 = (x + 1) / (float)SHADER_AZIMUTH_SEGMENTS;
                  addTexturedVertex(builder, u0, v0);
                  addTexturedVertex(builder, u1, v0);
                  addTexturedVertex(builder, u1, v1);
                  addTexturedVertex(builder, u0, v1);
               }
            }
         }
         try (MeshData mesh = builder.buildOrThrow()) {
            return RenderSystem.getDevice().createBuffer(
               () -> "The Sift ALPHA2 shared textured sky", 32, mesh.vertexBuffer()
            );
         }
      }
   }

   private static void addTexturedVertex(BufferBuilder builder, float u, float v) {
      float azimuth = u * TAU;
      float elevation = (float)Math.toRadians(-90.0F + v * 180.0F);
      float horizontal = (float)Math.cos(elevation) * RADIUS;
      builder.addVertex(
         (float)Math.sin(azimuth) * horizontal,
         (float)Math.sin(elevation) * RADIUS,
         (float)Math.cos(azimuth) * horizontal
      ).setUv(u, v);
   }

   private static int shaderElevationSubdivisions(int row) {
      return row >= SHADER_TOP_START_ROW ? SHADER_TOP_SUBDIVISIONS : (row >= SHADER_MID_START_ROW ? SHADER_MID_SUBDIVISIONS : 1);
   }

   private static int shaderQuadCount() {
      int quads = 0;
      for (int row = 0; row < ELEVATION_SEGMENTS; row++) {
         quads += SHADER_AZIMUTH_SEGMENTS * shaderElevationSubdivisions(row);
      }

      return quads;
   }

   private static GpuBuffer buildDome(boolean night, int layer, boolean shaderMesh) {
      int vertexCount = shaderMesh ? SHADER_VERTEX_COUNT : VERTEX_COUNT;
      int byteCount = vertexCount * DefaultVertexFormat.POSITION_COLOR.getVertexSize();
      ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(byteCount);

      GpuBuffer var16;
      try {
         BufferBuilder builder = new BufferBuilder(bytes, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_COLOR);

         for (int y = 0; y < ELEVATION_SEGMENTS; y++) {
            int azimuth = shaderMesh ? SHADER_AZIMUTH_SEGMENTS : AZIMUTH_SEGMENTS;
            int subdivisions = shaderMesh ? shaderElevationSubdivisions(y) : 1;

            for (int sub = 0; sub < subdivisions; sub++) {
               float v0 = (y + sub / (float)subdivisions) / ELEVATION_SEGMENTS;
               float v1 = (y + (sub + 1) / (float)subdivisions) / ELEVATION_SEGMENTS;

               for (int x = 0; x < azimuth; x++) {
                  float u0 = x / (float)azimuth;
                  float u1 = (x + 1) / (float)azimuth;
                  addVertex(builder, u0, v0, night, layer, shaderMesh);
                  addVertex(builder, u1, v0, night, layer, shaderMesh);
                  addVertex(builder, u1, v1, night, layer, shaderMesh);
                  addVertex(builder, u0, v1, night, layer, shaderMesh);
               }
            }
         }

         MeshData mesh = builder.buildOrThrow();

         try {
            var16 = RenderSystem.getDevice().createBuffer(() -> "The Sift procedural sky layer", 32, mesh.vertexBuffer());
         } catch (Throwable var13) {
            if (mesh != null) {
               try {
                  mesh.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }
            }

            throw var13;
         }

         if (mesh != null) {
            mesh.close();
         }
      } catch (Throwable var14) {
         if (bytes != null) {
            try {
               bytes.close();
            } catch (Throwable var11) {
               var14.addSuppressed(var11);
            }
         }

         throw var14;
      }

      if (bytes != null) {
         bytes.close();
      }

      return var16;
   }

   private static void addVertex(BufferBuilder builder, float u, float v, boolean night, int layer, boolean shaderMesh) {
      float azimuth = u * (float) (Math.PI * 2);
      float elevationDegrees = -90.0F + v * 180.0F;
      float elevation = (float)Math.toRadians(elevationDegrees);
      float horizontal = (float)Math.cos(elevation) * 99.0F;
      float px = (float)Math.sin(azimuth) * horizontal;
      float py = (float)Math.sin(elevation) * 99.0F;
      float pz = (float)Math.cos(azimuth) * horizontal;
      float artV = clamp((elevationDegrees - -14.0F) / 104.0F);
      float lowerConvergence = smooth(clamp((-14.0F - elevationDegrees) / 68.0F));
      float ribbonFade = 1.0F - smooth(clamp((-14.0F - elevationDegrees) / 16.0F));
      // The original concentric zenith gradient looked like a perfect
      // bullseye once Iris BASIC rendered each primitive flat. Perturb only
      // the shader mesh's upper-sky artistic coordinate with smooth periodic
      // waves. This is zero at the horizon and at the pole (seam-safe).
      float artSampleV = shaderMesh ? warpZenithGradient(u, artV) : artV;
      float[] rgba = layer == 0
         ? baseColor(u, artSampleV, night, lowerConvergence)
         : ribbonColor(u, artSampleV, night, layer - 1, ribbonFade);
      builder.addVertex(px, py, pz).setColor(channel(rgba[0]), channel(rgba[1]), channel(rgba[2]), channel(rgba[3]));
   }

   private static float warpZenithGradient(float u, float v) {
      float envelope = smooth((v - 0.5F) / 0.15F) * (1.0F - smooth((v - 0.84F) / 0.13F));
      float wave = 0.045F * (float)Math.sin(TAU * (u + 0.11F))
         + 0.018F * (float)Math.sin(TAU * (3.0F * u - 0.27F));
      return clamp(v + envelope * wave);
   }

   private static float[] baseColor(float u, float v, boolean night, float lowerConvergence) {
      float horizon = 1.0F - smooth(clamp((v - 0.05F) / 0.82F));
      float wave = 0.5F + 0.5F * (float)Math.sin((float) (Math.PI * 2) * (u * 2.0F + 0.08F));
      float zenithBlend = smooth((v - 0.68F) / 0.27F);
      if (night) {
         float[] color = new float[]{
            lerp(horizon, 0.025F, 0.055F) + wave * 0.015F, lerp(horizon, 0.145F, 0.43F) + wave * 0.035F, lerp(horizon, 0.31F, 0.64F) + wave * 0.055F, 1.0F
         };
         float[] result = new float[]{lerp(zenithBlend, color[0], 0.035F), lerp(zenithBlend, color[1], 0.19F), lerp(zenithBlend, color[2], 0.43F), 1.0F};
         return convergeLowerHemisphere(result, lowerConvergence, 0.055F, 0.36F, 0.58F);
      } else {
         float r = lerp(horizon, 0.47F, 0.98F);
         float g = lerp(horizon, 0.82F, 0.76F);
         float b = lerp(horizon, 0.93F, 0.88F);
         float mint = 0.5F + 0.5F * (float)Math.sin((float) (Math.PI * 2) * (u * 3.0F - 0.21F));
         r -= mint * 0.07F * (1.0F - horizon * 0.35F);
         g += mint * 0.05F * (1.0F - horizon * 0.35F);
         float[] result = new float[]{lerp(zenithBlend, r, 0.43F), lerp(zenithBlend, g, 0.87F), lerp(zenithBlend, b, 0.94F), 1.0F};
         return convergeLowerHemisphere(result, lowerConvergence, 0.94F, 0.78F, 0.88F);
      }
   }

   private static float[] convergeLowerHemisphere(float[] color, float blend, float red, float green, float blue) {
      return new float[]{lerp(blend, color[0], red), lerp(blend, color[1], green), lerp(blend, color[2], blue), color[3]};
   }

   private static float[] ribbonColor(float u, float v, boolean night, int variant, float lowerFade) {
      float[][] palette = night ? NIGHT_PALETTE : DAY_PALETTE;
      float red = 0.0F;
      float green = 0.0F;
      float blue = 0.0F;
      float total = 0.0F;
      float combined = 0.0F;

      for (int i = 0; i < palette.length; i++) {
         float frequency = 1.0F + i % 3;
         float phase = i * 0.173F + variant * 0.271F;
         float center = 0.13F
            + i * 0.17F
            + (0.115F - variant * 0.018F) * (float)Math.sin((float) (Math.PI * 2) * (u * frequency + phase))
            + 0.038F * (float)Math.sin((float) (Math.PI * 2) * (u * (frequency + 2.0F) - phase));
         float width = (variant == 0 ? 0.135F : 0.085F) + i % 2 * 0.018F;
         float band = smooth(clamp(1.0F - Math.abs(v - center) / width));
         float pulse = (float)Math.sin((float) (Math.PI * 2) * (u * 2.0F + phase));
         band *= 0.78F + 0.22F * pulse * pulse;
         band *= 1.0F - smooth((v - 0.7F) / 0.22F);
         red += palette[i][0] * band;
         green += palette[i][1] * band;
         blue += palette[i][2] * band;
         total += band;
         combined = 1.0F - (1.0F - combined) * (1.0F - band * 0.58F);
      }

      if (total <= 1.0E-4F) {
         return new float[]{1.0F, 1.0F, 1.0F, 0.0F};
      }

      float glow = variant == 0 ? (night ? 0.48F : 0.42F) : (night ? 0.37F : 0.3F);
      return new float[]{red / total, green / total, blue / total, clamp(combined * glow * lowerFade)};
   }

   private static GpuBufferSlice transform(PoseStack poseStack, float yaw, float roll, float brightness, float alpha) {
      Matrix4fStack modelView = RenderSystem.getModelViewStack();
      modelView.pushMatrix();
      modelView.mul(poseStack.last().pose());
      modelView.rotateY(yaw);
      modelView.rotateZ(roll);
      GpuBufferSlice result = RenderSystem.getDynamicUniforms()
         .writeTransform(new Matrix4f(modelView), new Vector4f(brightness, brightness, brightness, alpha));
      modelView.popMatrix();
      return result;
   }

   private static int channel(float value) {
      return Math.round(clamp(value) * 255.0F);
   }

   private static float lerp(float delta, float start, float end) {
      return start + delta * (end - start);
   }

   private static float smooth(float value) {
      float clamped = clamp(value);
      return clamped * clamped * (3.0F - 2.0F * clamped);
   }

   private static float clamp(float value) {
      return Math.max(0.0F, Math.min(1.0F, value));
   }
}
