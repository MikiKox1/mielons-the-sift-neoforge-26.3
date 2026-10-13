package mielon.thesift.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem.AutoStorageIndexBuffer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import mielon.thesift.client.render.SiftProceduralSkyRenderer;
import mielon.thesift.world.TheSiftDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.world.level.MoonPhase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public abstract class SiftSkyRendererMixin {
   @Shadow
   @Final
   private AutoStorageIndexBuffer quadIndices;

   /**
    * Minecraft 26.3 / RenderPearl prohibits texture uploads (and other GPU
    * resource creation) while a RenderPass is open. SkyRenderer.render HEAD
    * is before its own createRenderPass(), whereas renderSunMoonAndStars
    * receives an already-active pass. Prepare every Sift sky texture, vertex
    * mesh, index buffer and sampler here on the render thread, including
    * after resource reloads or shaderpack switches.
    */
   @Inject(method = "render", at = @At("HEAD"))
   private void theSift$prepareSkyBeforeRenderPass(
      GpuBufferSlice skyFog, SkyRenderState state, CallbackInfo ci
   ) {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null && level.dimension().equals(TheSiftDimension.LEVEL_KEY)) {
         SiftProceduralSkyRenderer.prepareSkyResourcesBeforePass(level, this.quadIndices);
      }
   }

   @Inject(method = "renderSunMoonAndStars", at = @At("HEAD"), cancellable = true)
   private void theSift$renderProceduralSky(
      RenderPass renderPass,
      PoseStack poseStack,
      float sunAngle,
      float moonAngle,
      float starAngle,
      MoonPhase moonPhase,
      float rainBrightness,
      float starBrightness,
      CallbackInfo ci
   ) {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null && level.dimension().equals(TheSiftDimension.LEVEL_KEY)) {
         SiftProceduralSkyRenderer.render(poseStack, level, renderPass, this.quadIndices);
         ci.cancel();
      }
   }

   @Inject(method = "close", at = @At("TAIL"))
   private void theSift$closeProceduralSky(CallbackInfo ci) {
      SiftProceduralSkyRenderer.close();
   }
}
