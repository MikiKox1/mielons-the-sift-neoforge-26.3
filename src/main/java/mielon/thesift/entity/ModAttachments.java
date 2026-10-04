package mielon.thesift.entity;

import mielon.thesift.platform.ModRegistration;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {
   public static final AttachmentType<Boolean> SNIFFER_INFECTED = ModRegistration.register(
      NeoForgeRegistries.ATTACHMENT_TYPES,
      Identifier.fromNamespaceAndPath("the_sift", "sniffer_infected"),
      AttachmentType.builder(() -> false).sync(ByteBufCodecs.BOOL).build()
   );
   public static final AttachmentType<Boolean> WARDEN_PETTING = ModRegistration.register(
      NeoForgeRegistries.ATTACHMENT_TYPES,
      Identifier.fromNamespaceAndPath("the_sift", "warden_petting"),
      AttachmentType.builder(() -> false).sync(ByteBufCodecs.BOOL).build()
   );

   private ModAttachments() {
   }

   public static void initialize() {
   }
}
