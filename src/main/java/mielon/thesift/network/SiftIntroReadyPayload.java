package mielon.thesift.network;

import mielon.thesift.world.SiftTeleportManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record SiftIntroReadyPayload() implements CustomPacketPayload {
   public static final Type<SiftIntroReadyPayload> TYPE = new Type(Identifier.fromNamespaceAndPath("the_sift", "intro_ready"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SiftIntroReadyPayload> CODEC = StreamCodec.unit(new SiftIntroReadyPayload());

   public Type<SiftIntroReadyPayload> type() {
      return TYPE;
   }

   public static void register() {
      PayloadTypeRegistry.serverboundPlay().register(TYPE, CODEC);
      ServerPlayNetworking.registerGlobalReceiver(
         TYPE, (payload, context) -> context.server().execute(() -> SiftTeleportManager.playFirstIntro(context.player()))
      );
   }
}
