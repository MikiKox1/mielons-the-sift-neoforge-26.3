package mielon.thesift.item;

import mielon.thesift.platform.ModRegistration;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.block.SonorousDeepslateBlock;
import mielon.thesift.entity.ModEntities;
import mielon.thesift.fluid.ModFluids;
import mielon.thesift.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

public final class ModItems {
   public static final ResourceKey<Item> SINGER_SPAWN_EGG_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("the_sift", "singer_spawn_egg")
   );
   public static final Item SINGER_SPAWN_EGG = (Item)ModRegistration.register(
      BuiltInRegistries.ITEM, SINGER_SPAWN_EGG_KEY, new SpawnEggItem(new Properties().setId(SINGER_SPAWN_EGG_KEY).spawnEgg(ModEntities.SINGER))
   );
   public static final ResourceKey<Item> ECHO_GOLEM_SPAWN_EGG_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("the_sift", "echo_golem_spawn_egg")
   );
   public static final Item ECHO_GOLEM_SPAWN_EGG = (Item)ModRegistration.register(
      BuiltInRegistries.ITEM, ECHO_GOLEM_SPAWN_EGG_KEY, new SpawnEggItem(new Properties().setId(ECHO_GOLEM_SPAWN_EGG_KEY).spawnEgg(ModEntities.ECHO_GOLEM))
   );
   public static final ResourceKey<Item> DARK_SNIFFER_SPAWN_EGG_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("the_sift", "dark_sniffer_spawn_egg")
   );
   public static final Item DARK_SNIFFER_SPAWN_EGG = (Item)ModRegistration.register(
      BuiltInRegistries.ITEM,
      DARK_SNIFFER_SPAWN_EGG_KEY,
      new SpawnEggItem(new Properties().setId(DARK_SNIFFER_SPAWN_EGG_KEY).spawnEgg(ModEntities.DARK_SNIFFER))
   );
   public static final ResourceKey<Item> SIFTER_SPAWN_EGG_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("the_sift", "sifter_spawn_egg")
   );
   public static final Item SIFTER_SPAWN_EGG = (Item)ModRegistration.register(
      BuiltInRegistries.ITEM, SIFTER_SPAWN_EGG_KEY, new SpawnEggItem(new Properties().setId(SIFTER_SPAWN_EGG_KEY).spawnEgg(ModEntities.SIFTER))
   );
   public static final ResourceKey<Item> OVERGROWN_WILLOW_BOAT_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("the_sift", "overgrown_willow_boat")
   );
   public static final Item OVERGROWN_WILLOW_BOAT = (Item)ModRegistration.register(
      BuiltInRegistries.ITEM,
      OVERGROWN_WILLOW_BOAT_KEY,
      new BoatItem(ModEntities.OVERGROWN_WILLOW_BOAT, new Properties().stacksTo(1).setId(OVERGROWN_WILLOW_BOAT_KEY))
   );
   public static final ResourceKey<Item> OVERGROWN_WILLOW_CHEST_BOAT_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("the_sift", "overgrown_willow_chest_boat")
   );
   public static final Item OVERGROWN_WILLOW_CHEST_BOAT = (Item)ModRegistration.register(
      BuiltInRegistries.ITEM,
      OVERGROWN_WILLOW_CHEST_BOAT_KEY,
      new BoatItem(ModEntities.OVERGROWN_WILLOW_CHEST_BOAT, new Properties().stacksTo(1).setId(OVERGROWN_WILLOW_CHEST_BOAT_KEY))
   );
   public static final TagKey<Item> SIFTITE_REPAIR_MATERIALS = TagKey.create(
      Registries.ITEM, Identifier.fromNamespaceAndPath("the_sift", "siftite_repair_materials")
   );
   public static final TagKey<Item> SIFTITE_ITEMS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("the_sift", "siftite_items"));
   public static final ToolMaterial SIFTITE_TOOL_MATERIAL = new ToolMaterial(
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      ToolMaterial.NETHERITE.durability() + 470,
      ToolMaterial.NETHERITE.speed() * 1.2F,
      ToolMaterial.NETHERITE.attackDamageBonus() + 1.0F,
      ToolMaterial.NETHERITE.enchantmentValue(),
      SIFTITE_REPAIR_MATERIALS
   );
   public static final ResourceKey<EquipmentAsset> SIFTITE_EQUIPMENT_ASSET = ResourceKey.create(
      EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath("the_sift", "siftite")
   );
   public static final ArmorMaterial SIFTITE_ARMOR_MATERIAL = new ArmorMaterial(
      ArmorMaterials.NETHERITE.durability() + 4,
      new EnumMap<>(
         Map.of(
            ArmorType.BOOTS, 3,
            ArmorType.LEGGINGS, 6,
            ArmorType.CHESTPLATE, 8,
            ArmorType.HELMET, 3,
            ArmorType.BODY, 19
         )
      ),
      ArmorMaterials.NETHERITE.enchantmentValue(),
      BuiltInRegistries.SOUND_EVENT.wrapAsHolder(ModSounds.SIFTITE_ARMOR_EQUIP),
      3.0F,
      0.15F,
      SIFTITE_REPAIR_MATERIALS,
      SIFTITE_EQUIPMENT_ASSET
   );
   public static final Item ICHOR_SHARD = registerItem("ichor_shard", new Properties());
   public static final Item CHAROITE = registerItem("charoite", new Properties());
   public static final Item SIFTITE_NUGGET = registerItem("siftite_nugget", new Properties());
   public static final Item SIFTITE_INGOT = registerItem("siftite_ingot", new Properties());
   public static final Item ICHOR_SNOWBALL = registerItem("ichor_snowball", SnowballItem::new, new Properties().stacksTo(16));
   public static final Item ICHOR_BOTTLE = registerItem(
      "ichor_bottle",
      IchorBottleItem::new,
      new Properties()
         .stacksTo(1)
         .usingConvertsTo(Items.GLASS_BOTTLE)
         .component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK)
         .component(
            DataComponents.POTION_CONTENTS,
            new PotionContents(Optional.empty(), Optional.of(5909123), List.of(new MobEffectInstance(MobEffects.REGENERATION, 60)), Optional.empty())
         )
   );
   public static final Item RAW_SIFTER_MEAT = registerItem("raw_sifter_meat", new Properties().food(Foods.PORKCHOP));
   public static final Item COOKED_SIFTER_MEAT = registerItem("cooked_sifter_meat", new Properties().food(Foods.COOKED_PORKCHOP));
   public static final Item SIFT_RIFT = registerItem("sift_rift", SiftRiftItem::new, new Properties().stacksTo(1).rarity(Rarity.EPIC));
   public static final ResourceKey<JukeboxSong> RIFT_JUKEBOX_SONG_KEY = ResourceKey.create(
      Registries.JUKEBOX_SONG, Identifier.fromNamespaceAndPath("the_sift", "rift")
   );
   public static final Item MUSIC_DISC_RIFT = registerItem(
      "music_disc_rift",
      new Properties()
         .stacksTo(1)
         .rarity(Rarity.RARE)
         .delayedComponent(
            DataComponents.JUKEBOX_PLAYABLE,
            provider -> provider.get(RIFT_JUKEBOX_SONG_KEY).map(JukeboxPlayable::new).orElse(null)
         )
   );
   public static final ResourceKey<JukeboxSong> WELLSPRING_JUKEBOX_SONG_KEY = ResourceKey.create(
      Registries.JUKEBOX_SONG, Identifier.fromNamespaceAndPath("the_sift", "wellspring")
   );
   public static final Item MUSIC_DISC_WELLSPRING = registerItem(
      "music_disc_wellspring", new Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(WELLSPRING_JUKEBOX_SONG_KEY)
   );
   public static final ResourceKey<Item> SCULKFLOWER_SEEDS_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("the_sift", "sculkflower_seeds")
   );
   public static final Item SCULKFLOWER_SEEDS = (Item)ModRegistration.register(
      BuiltInRegistries.ITEM, SCULKFLOWER_SEEDS_KEY, new BlockItem(ModBlocks.SCULKFLOWER_CROP, new Properties().setId(SCULKFLOWER_SEEDS_KEY))
   );
   public static final Item SIFTITE_SWORD = registerItem("siftite_sword", new Properties().sword(SIFTITE_TOOL_MATERIAL, 3.0F, -2.4F).fireResistant());
   public static final Item SIFTITE_SHOVEL = registerItem("siftite_shovel", new Properties().shovel(SIFTITE_TOOL_MATERIAL, 1.5F, -3.0F).fireResistant());
   public static final Item SIFTITE_PICKAXE = registerItem("siftite_pickaxe", new Properties().pickaxe(SIFTITE_TOOL_MATERIAL, 1.0F, -2.8F).fireResistant());
   public static final Item SIFTITE_AXE = registerItem("siftite_axe", new Properties().axe(SIFTITE_TOOL_MATERIAL, 5.0F, -3.0F).fireResistant());
   public static final Item SIFTITE_HOE = registerItem("siftite_hoe", new Properties().hoe(SIFTITE_TOOL_MATERIAL, -4.0F, 0.0F).fireResistant());
   public static final Item SIFTITE_SPEAR = registerItem(
      "siftite_spear", new Properties().spear(SIFTITE_TOOL_MATERIAL, 1.25F, 1.2F, 0.4F, 2.5F, 9.0F, 5.5F, 5.1F, 8.75F, 4.6F).fireResistant()
   );
   public static final Item SIFTITE_HELMET = registerItem(
      "siftite_helmet",
      new Properties()
         .humanoidArmor(SIFTITE_ARMOR_MATERIAL, ArmorType.HELMET)
         .durability(ArmorType.HELMET.getDurability(SIFTITE_ARMOR_MATERIAL.durability()))
         .fireResistant()
   );
   public static final Item SIFTITE_CHESTPLATE = registerItem(
      "siftite_chestplate",
      new Properties()
         .humanoidArmor(SIFTITE_ARMOR_MATERIAL, ArmorType.CHESTPLATE)
         .durability(ArmorType.CHESTPLATE.getDurability(SIFTITE_ARMOR_MATERIAL.durability()))
         .fireResistant()
   );
   public static final Item SIFTITE_LEGGINGS = registerItem(
      "siftite_leggings",
      new Properties()
         .humanoidArmor(SIFTITE_ARMOR_MATERIAL, ArmorType.LEGGINGS)
         .durability(ArmorType.LEGGINGS.getDurability(SIFTITE_ARMOR_MATERIAL.durability()))
         .fireResistant()
   );
   public static final Item SIFTITE_BOOTS = registerItem(
      "siftite_boots",
      new Properties()
         .humanoidArmor(SIFTITE_ARMOR_MATERIAL, ArmorType.BOOTS)
         .durability(ArmorType.BOOTS.getDurability(SIFTITE_ARMOR_MATERIAL.durability()))
         .fireResistant()
   );
   public static final Item SIFTITE_UPGRADE_SMITHING_TEMPLATE = registerItem(
      "siftite_upgrade_smithing_template",
      properties -> new SmithingTemplateItem(
         Component.translatable("item.the_sift.smithing_template.siftite_upgrade.applies_to").withStyle(ChatFormatting.BLUE),
         Component.translatable("item.the_sift.smithing_template.siftite_upgrade.ingredients").withStyle(ChatFormatting.BLUE),
         Component.translatable("item.the_sift.smithing_template.siftite_upgrade.base_slot_description"),
         Component.translatable("item.the_sift.smithing_template.siftite_upgrade.additions_slot_description"),
         List.of(
            Identifier.withDefaultNamespace("container/slot/helmet"),
            Identifier.withDefaultNamespace("container/slot/chestplate"),
            Identifier.withDefaultNamespace("container/slot/leggings"),
            Identifier.withDefaultNamespace("container/slot/boots"),
            Identifier.withDefaultNamespace("container/slot/sword"),
            Identifier.withDefaultNamespace("container/slot/pickaxe"),
            Identifier.withDefaultNamespace("container/slot/axe"),
            Identifier.withDefaultNamespace("container/slot/shovel"),
            Identifier.withDefaultNamespace("container/slot/hoe"),
            Identifier.withDefaultNamespace("container/slot/spear")
         ),
         List.of(Identifier.withDefaultNamespace("container/slot/ingot")),
         properties
      ),
      new Properties().rarity(Rarity.UNCOMMON)
   );
   public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(
      BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath("the_sift", "the_sift")
   );
   public static final CreativeModeTab CREATIVE_TAB = CreativeModeTab.builder()
      .title(Component.translatable("itemGroup.the_sift"))
      .icon(() -> new ItemStack(ModBlocks.DRY_HEALTHY_SCULK_GROWTH_ITEM))
      .displayItems(
         (params, output) -> {
            ItemStack horn = new ItemStack(ModBlocks.SONOROUS_DEEPSLATE_ITEM);
            horn.set(
               DataComponents.BLOCK_STATE,
               new BlockItemStateProperties(Map.of(SonorousDeepslateBlock.MODE.getName(), SonorousDeepslateBlock.Mode.HORN.getSerializedName()))
            );
            output.accept(horn);
            ItemStack note = new ItemStack(ModBlocks.SONOROUS_DEEPSLATE_ITEM);
            note.set(
               DataComponents.BLOCK_STATE,
               new BlockItemStateProperties(Map.of(SonorousDeepslateBlock.MODE.getName(), SonorousDeepslateBlock.Mode.NOTE.getSerializedName()))
            );
            output.accept(note);
            output.accept(ModBlocks.REINFORCED_SIFTSLATE_ITEM);
            output.accept(ModBlocks.SIFTSLATE_ITEM);
            output.accept(ModBlocks.SIFTSLATE_COAL_ORE_ITEM);
            output.accept(ModBlocks.SIFTSLATE_DIAMOND_ORE_ITEM);
            output.accept(ModBlocks.SIFTSLATE_EMERALD_ORE_ITEM);
            output.accept(ModBlocks.SIFTSLATE_CHAROITE_ORE_ITEM);
            output.accept(ModBlocks.SIFTSLATE_SIFTITE_ORE_ITEM);
            output.accept(ModBlocks.COBBLED_SIFTSLATE_ITEM);
            output.accept(ModBlocks.COBBLED_SIFTSLATE_STAIRS_ITEM);
            output.accept(ModBlocks.COBBLED_SIFTSLATE_SLAB_ITEM);
            output.accept(ModBlocks.COBBLED_SIFTSLATE_WALL_ITEM);
            output.accept(ModBlocks.SIFTSLATE_BRICKS_ITEM);
            output.accept(ModBlocks.SIFTSLATE_BRICK_STAIRS_ITEM);
            output.accept(ModBlocks.SIFTSLATE_BRICK_SLAB_ITEM);
            output.accept(ModBlocks.SIFTSLATE_BRICK_WALL_ITEM);
            output.accept(ModBlocks.CHISELED_SIFTSLATE_ITEM);
            output.accept(ModBlocks.SIFTSLATE_GROWTH_ITEM);
            output.accept(ModBlocks.SIFT_RUBBLE_ITEM);
            output.accept(ModBlocks.HEALTHY_SCULK_ITEM);
            output.accept(ModBlocks.DRY_HEALTHY_SCULK_ITEM);
            output.accept(ModBlocks.DRY_HEALTHY_SCULK_GROWTH_ITEM);
            output.accept(ModBlocks.ICHOR_SNOW_BLOCK_ITEM);
            output.accept(ModBlocks.ICHOR_SNOW_ITEM);
            output.accept(ICHOR_SNOWBALL);
            output.accept(ModBlocks.OVERGROWN_WILLOW_LOG_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_WOOD_ITEM);
            output.accept(ModBlocks.STRIPPED_OVERGROWN_WILLOW_LOG_ITEM);
            output.accept(ModBlocks.STRIPPED_OVERGROWN_WILLOW_WOOD_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_PLANKS_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_STAIRS_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_SLAB_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_FENCE_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_FENCE_GATE_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_DOOR_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_TRAPDOOR_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_PRESSURE_PLATE_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_BUTTON_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_SHELF_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_SIGN_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_HANGING_SIGN_ITEM);
            output.accept(OVERGROWN_WILLOW_BOAT);
            output.accept(OVERGROWN_WILLOW_CHEST_BOAT);
            output.accept(ModBlocks.SOUL_BLOCK_ITEM);
            output.accept(ModBlocks.OVERGROWN_FRONDS_ITEM);
            output.accept(ModBlocks.OVERGROWN_CHARD_ITEM);
            output.accept(ModBlocks.OVERGROWN_STALKS_ITEM);
            output.accept(ModBlocks.OVERGROWN_HANGING_ROOTS_ITEM);
            output.accept(ModBlocks.SIFTSLATE_STALKS_ITEM);
            output.accept(ModBlocks.SIFTSLATE_HANGING_ROOTS_ITEM);
            output.accept(ModBlocks.HEALTHY_SCULK_SPROUTS_ITEM);
            output.accept(ModBlocks.DRY_HEALTHY_SCULK_SPROUTS_ITEM);
            output.accept(ModBlocks.OVERGROWN_LOTUS_ITEM);
            output.accept(ModBlocks.SUNBURST_PLANT_ITEM);
            output.accept(ModBlocks.WHISPERBLOOM_ITEM);
            output.accept(SCULKFLOWER_SEEDS);
            output.accept(ModBlocks.SCULKFLOWER_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_FOLIAGE_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_SAPLING_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_VINES_ITEM);
            output.accept(RAW_SIFTER_MEAT);
            output.accept(COOKED_SIFTER_MEAT);
            output.accept(ModFluids.ICHOR_BUCKET);
            output.accept(ICHOR_BOTTLE);
            output.accept(ModBlocks.ICHOR_GLASS_ITEM);
            output.accept(ModBlocks.ICHOR_GLASS_PANE_ITEM);
            output.accept(ICHOR_SHARD);
            output.accept(CHAROITE);
            output.accept(SIFTITE_INGOT);
            output.accept(SIFTITE_NUGGET);
            output.accept(SIFTITE_SWORD);
            output.accept(SIFTITE_PICKAXE);
            output.accept(SIFTITE_AXE);
            output.accept(SIFTITE_SHOVEL);
            output.accept(SIFTITE_HOE);
            output.accept(SIFTITE_SPEAR);
            output.accept(SIFTITE_HELMET);
            output.accept(SIFTITE_CHESTPLATE);
            output.accept(SIFTITE_LEGGINGS);
            output.accept(SIFTITE_BOOTS);
            output.accept(SIFTITE_UPGRADE_SMITHING_TEMPLATE);
            output.accept(MUSIC_DISC_RIFT);
            output.accept(MUSIC_DISC_WELLSPRING);
            output.accept(ModBlocks.SIFT_PORTAL_ITEM);
            output.accept(SIFT_RIFT);
            output.accept(SINGER_SPAWN_EGG);
            output.accept(ECHO_GOLEM_SPAWN_EGG);
            output.accept(DARK_SNIFFER_SPAWN_EGG);
            output.accept(BlubItems.BLUB_SPAWN_EGG);
            output.accept(SIFTER_SPAWN_EGG);
         }
      )
      .build();

   static {
      ModRegistration.register(BuiltInRegistries.CREATIVE_MODE_TAB, CREATIVE_TAB_KEY, CREATIVE_TAB);
   }

   private ModItems() {
   }

   public static void bootstrap() {
   }

   public static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
      // NeoForge MC26.2 composter data maps carry these registrations, as
      // recorded in data/neoforge/data_maps/item/compostables.json.
   }

   public static void initialize() {
      IchorBottleItem.registerInteractions();
   }

   private static Item registerItem(String name, Properties properties) {
      ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("the_sift", name));
      return (Item)ModRegistration.register(BuiltInRegistries.ITEM, key, new Item(properties.setId(key)));
   }

   private static <T extends Item> T registerItem(String name, Function<Properties, T> factory, Properties properties) {
      ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("the_sift", name));
      return (T)ModRegistration.register(BuiltInRegistries.ITEM, key, factory.apply(properties.setId(key)));
   }
}
