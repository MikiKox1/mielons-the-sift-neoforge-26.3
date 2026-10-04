package mielon.thesift.platform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Defers registry insertion while preserving the release-accurate 26.3 objects.
 *
 * <p>The Fabric release constructs most registry values in static initializers.
 * Replacing every public field with a DeferredHolder would force invasive changes
 * throughout the codebase, so this bridge keeps object construction unchanged and
 * only defers insertion until NeoForge fires the matching RegisterEvent.</p>
 */
public final class ModRegistration {
   private static final List<Entry> ENTRIES = new ArrayList<>();
   private static final Set<Identifier> COMPLETED_REGISTRIES = new HashSet<>();

   private ModRegistration() {
   }

   public static <V, T extends V> T register(Registry<V> registry, ResourceKey<V> key, T value) {
      return register(registry, key.identifier(), value);
   }

   public static <V, T extends V> T register(Registry<V> registry, Identifier id, T value) {
      Identifier registryId = registry.key().identifier();
      if (COMPLETED_REGISTRIES.contains(registryId)) {
         throw new IllegalStateException("Attempted to queue " + id + " after registry event " + registryId + " completed");
      }

      ENTRIES.add(new Entry(registryId, event -> event.register(registry.key(), id, () -> value)));
      return value;
   }

   public static void onRegister(RegisterEvent event) {
      Identifier registryId = event.getRegistryKey().identifier();
      for (Entry entry : ENTRIES) {
         if (entry.registryId().equals(registryId)) {
            entry.registrar().accept(event);
         }
      }
      COMPLETED_REGISTRIES.add(registryId);
   }

   private record Entry(Identifier registryId, Consumer<RegisterEvent> registrar) {
   }
}
