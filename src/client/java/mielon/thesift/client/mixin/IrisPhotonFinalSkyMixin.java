package mielon.thesift.client.mixin;

import mielon.thesift.client.render.SiftProceduralSkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Optional Photon-only post-final sky compositing hook. Iris composites its
 * own colortexes into Minecraft.mainRenderTarget at finalizeLevelRendering's
 * end. The ordinary sky callback runs too early; its native draw gets
 * overwritten by that final pass. This hook intentionally runs AFTER it.
 *
 * @Pseudo + require=0 protect standalone instances without Iris.
 * Do not reference Iris binary classes in this mixin's method signature.
 */
@Pseudo
@Mixin(targets = "net.irisshaders.iris.pipeline.IrisRenderingPipeline", remap = false)
public abstract class IrisPhotonFinalSkyMixin {
   @Inject(method = "finalizeLevelRendering()V", at = @At("TAIL"), require = 0, remap = false)
   private void theSift$afterPhotonFinalPass(CallbackInfo ci) {
      SiftProceduralSkyRenderer.renderAfterPhotonFinalPass();
   }
}
