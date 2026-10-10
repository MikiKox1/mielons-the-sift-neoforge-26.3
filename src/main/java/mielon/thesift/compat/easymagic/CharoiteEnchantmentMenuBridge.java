package mielon.thesift.compat.easymagic;

/**
 * Internal bridge implemented by the vanilla enchantment-menu mixin.
 *
 * <p>Easy Magic replaces the access-transformed vanilla enchanting container with
 * its own three-slot container, so its compatibility mixin toggles the same Charoite
 * cost state without linking against Easy Magic classes.</p>
 */
public interface CharoiteEnchantmentMenuBridge {
   void theSift$setCharoiteCostsActive(boolean active);
}
