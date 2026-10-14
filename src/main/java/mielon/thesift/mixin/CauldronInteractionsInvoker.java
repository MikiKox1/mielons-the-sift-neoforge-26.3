package mielon.thesift.mixin;

import net.minecraft.core.cauldron.CauldronInteraction.Dispatcher;
import net.minecraft.core.cauldron.CauldronInteractions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CauldronInteractions.class)
public interface CauldronInteractionsInvoker {
   @Invoker("addDefaultInteractions")
   static void theSift$addDefaultInteractions(Dispatcher dispatcher) {
      throw new AssertionError("Missing CauldronInteractions invoker transformation");
   }
}
