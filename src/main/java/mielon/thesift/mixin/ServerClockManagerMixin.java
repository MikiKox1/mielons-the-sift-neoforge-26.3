package mielon.thesift.mixin;

import mielon.thesift.world.SiftDayNightCycle;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerClockManager.class)
public abstract class ServerClockManagerMixin {
   @Unique
   private boolean theSift$internalClockWrite;
   @Unique
   private ServerClockManagerMixin.Transition theSift$transition;
   @Unique
   private Holder<WorldClock> theSift$markerClock;
   @Unique
   private long theSift$beforeMarker;

   @Inject(method = "setTotalTicks", at = @At("HEAD"), cancellable = true)
   private void theSift$smoothSetTime(Holder<WorldClock> clock, long targetTicks, CallbackInfo ci) {
      if (!this.theSift$internalClockWrite && theSift$isSiftClock(clock)) {
         if (!clock.equals(this.theSift$markerClock)) {
            ServerClockManager self = (ServerClockManager)(Object)this;
            long currentTicks = self.getTotalTicks(clock);
            if (this.theSift$beginTransition(clock, currentTicks, targetTicks)) {
               ci.cancel();
            }
         }
      }
   }

   @Inject(method = "moveToTimeMarker", at = @At("HEAD"))
   private void theSift$rememberTimeBeforeMarker(Holder<WorldClock> clock, ResourceKey<ClockTimeMarker> marker, CallbackInfoReturnable<Boolean> cir) {
      if (!this.theSift$internalClockWrite && theSift$isSiftClock(clock)) {
         this.theSift$markerClock = clock;
         this.theSift$beforeMarker = ((ServerClockManager)(Object)this).getTotalTicks(clock);
      } else {
         this.theSift$markerClock = null;
      }
   }

   @Inject(method = "moveToTimeMarker", at = @At("RETURN"))
   private void theSift$smoothTimeMarker(Holder<WorldClock> clock, ResourceKey<ClockTimeMarker> marker, CallbackInfoReturnable<Boolean> cir) {
      if (cir.getReturnValueZ() && clock.equals(this.theSift$markerClock)) {
         ServerClockManager self = (ServerClockManager)(Object)this;
         long targetTicks = self.getTotalTicks(clock);
         this.theSift$beginTransition(clock, this.theSift$beforeMarker, targetTicks);
         this.theSift$markerClock = null;
      } else {
         this.theSift$markerClock = null;
      }
   }

   @Inject(method = "tick", at = @At("TAIL"))
   private void theSift$advanceCommandTransition(CallbackInfo ci) {
      ServerClockManagerMixin.Transition transition = this.theSift$transition;
      if (transition != null) {
         int elapsed = transition.elapsed() + 1;
         long displayedTicks = elapsed >= 60 ? transition.targetTicks() : transition.startTicks() + elapsed;
         this.theSift$transition = elapsed >= 60
            ? null
            : new ServerClockManagerMixin.Transition(transition.clock(), transition.startTicks(), transition.targetTicks(), elapsed);
         this.theSift$internalClockWrite = true;

         try {
            ((ServerClockManager)(Object)this).setTotalTicks(transition.clock(), displayedTicks);
         } finally {
            this.theSift$internalClockWrite = false;
         }
      }
   }

   @Unique
   private boolean theSift$beginTransition(Holder<WorldClock> clock, long currentTicks, long targetTicks) {
      float currentNight = SiftDayNightCycle.nightBlend(currentTicks);
      float targetNight = SiftDayNightCycle.nightBlend(targetTicks);
      boolean currentIsNight = currentNight >= 0.5F;
      boolean targetIsNight = targetNight >= 0.5F;
      if (currentIsNight == targetIsNight) {
         this.theSift$transition = null;
         return false;
      }

      long targetPeriod = targetTicks - Math.floorMod(targetTicks, 24120L);
      long startTicks;
      if (targetIsNight) {
         startTicks = targetPeriod + 12000L;
      } else {
         startTicks = targetPeriod >= 24120L ? targetPeriod - 60L : 24060L;
      }

      this.theSift$transition = new ServerClockManagerMixin.Transition(clock, startTicks, targetTicks, 0);
      this.theSift$internalClockWrite = true;

      try {
         ((ServerClockManager)(Object)this).setTotalTicks(clock, startTicks);
      } finally {
         this.theSift$internalClockWrite = false;
      }

      return true;
   }

   @Unique
   private static boolean theSift$isSiftClock(Holder<WorldClock> clock) {
      return clock.unwrapKey().map(key -> key.identifier().equals(SiftDayNightCycle.CLOCK_ID)).orElse(false);
   }

   @Unique
   private record Transition(Holder<WorldClock> clock, long startTicks, long targetTicks, int elapsed) {
   }
}
