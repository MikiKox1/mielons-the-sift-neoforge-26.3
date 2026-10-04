package mielon.thesift.mixin;

import mielon.thesift.platform.NeoEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class SiftAfterDeathMixin {
   @Inject(
      method = "die",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V"
      )
   )
   private void theSift$afterDeath(DamageSource source, CallbackInfo ci) {
      // Fabric's ServerLivingEntityEvents.AFTER_DEATH is injected at the same
      // broadcastEntityEvent INVOKE, before the invocation itself.
      NeoEvents.afterDeath((LivingEntity)(Object)this, source);
   }
}
