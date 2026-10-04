package mielon.thesift.mixin;

import mielon.thesift.platform.NeoEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class SiftAfterPlayerDeathMixin {
   @Inject(method = "die", at = @At("TAIL"))
   private void theSift$afterPlayerDeath(DamageSource source, CallbackInfo ci) {
      // Fabric Entity Events has a separate ServerPlayer.die TAIL hook because
      // ServerPlayer overrides LivingEntity.die rather than delegating to it.
      NeoEvents.afterDeath((ServerPlayer)(Object)this, source);
   }
}
