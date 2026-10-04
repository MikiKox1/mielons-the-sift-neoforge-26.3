package mielon.thesift.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public record SingerAnimationTimings(int appearTicks, int singTicks, int disappearTicks) {
   private static final String RESOURCE = "/assets/the_sift/geckolib/animations/entity/singer.animation.json";
   private static final int FALLBACK_TICKS = 20;

   public static SingerAnimationTimings load() {
      try (InputStream stream = SingerAnimationTimings.class.getResourceAsStream("/assets/the_sift/geckolib/animations/entity/singer.animation.json")) {
         if (stream == null) {
            return new SingerAnimationTimings(20, 20, 20);
         }

         JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
         JsonObject animations = root.getAsJsonObject("animations");
         return new SingerAnimationTimings(ticksFor(animations, "appear"), ticksFor(animations, "sing"), ticksFor(animations, "disappear"));
      } catch (Exception ignored) {
         return new SingerAnimationTimings(20, 20, 20);
      }
   }

   private static int ticksFor(JsonObject animations, String name) {
      JsonElement element = animations == null ? null : animations.get(name);
      if (element != null && element.isJsonObject()) {
         JsonObject animation = element.getAsJsonObject();
         double length = 1.0;
         JsonElement lengthElement = animation.get("animation_length");
         if (lengthElement != null && lengthElement.isJsonPrimitive()) {
            length = Math.max(0.05, lengthElement.getAsDouble());
         }

         return Math.max(1, (int)Math.ceil(length * 20.0));
      } else {
         return 20;
      }
   }
}
