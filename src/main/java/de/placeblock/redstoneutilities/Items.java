package de.placeblock.redstoneutilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Items {
    public static final TextComponent INFOMETER_TITLE = Component.text("Redstone Infometer")
            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .color(RedstoneUtilities.PRIMARY_COLOR);

    public static final Material RECEIVER_MATERIAL = Material.CALIBRATED_SCULK_SENSOR;
    public static final Material SENDER_MATERIAL = Material.SCULK_SHRIEKER;
    public static final Material INFOMETER_MATERIAL = Material.RECOVERY_COMPASS;
    public static final Material AUTOCRAFTER_MATERIAL = Material.CRAFTING_TABLE;
    public static final Material CHUNKLOADER_MATERIAL = Material.RESPAWN_ANCHOR;
    public static final Material TELEPORTER_MATERIAL = Material.END_PORTAL_FRAME;
    public static final Material NETWORK_CONTROLLER_MATERIAL = Material.END_PORTAL_FRAME;

    public static final ItemStack RECEIVER_ITEM;
    public static final ItemStack SENDER_ITEM;
    public static final ItemStack CONNECTOR_ITEM;
    public static final ItemStack INFOMETER_ITEM;
    public static final ItemStack AUTOCRAFTER_ITEM;
    public static final ItemStack FILTER_ITEM;
    public static final ItemStack CHUNKLOADER_ITEM;
    public static final ItemStack TELEPORTER_ITEM;
    public static final ItemStack NETWORK_CONTROLLER_ITEM;
    private static final Material FILTER_MATERIAL = Material.HOPPER;

    static {
        SENDER_ITEM = getItem(SENDER_MATERIAL, "Redstone Sender", List.of());
        RECEIVER_ITEM = getItem(RECEIVER_MATERIAL, "Redstone Empfänger", List.of());
        CONNECTOR_ITEM = getItem(CHUNKLOADER_MATERIAL, "Redstone Verbinder", List.of(
                Component.text("Rechtsklick -> Verbinden")
                        .color(RedstoneUtilities.INFERIOR_COLOR)
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
                Component.text("Shift + Rechtsklick -> Trennen")
                        .color(RedstoneUtilities.INFERIOR_COLOR)
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
        INFOMETER_ITEM = getItem(INFOMETER_MATERIAL, INFOMETER_TITLE, List.of());
        AUTOCRAFTER_ITEM = getItem(AUTOCRAFTER_MATERIAL, "AutoCrafter", List.of(
                Component.text("Kann nur auf")
                        .color(RedstoneUtilities.INFERIOR_COLOR)
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
                Component.text("Spendern plaziert werden")
                        .color(RedstoneUtilities.INFERIOR_COLOR)
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
        FILTER_ITEM = getItem(FILTER_MATERIAL, "Filter", List.of());
        CHUNKLOADER_ITEM = getItem(CHUNKLOADER_MATERIAL, "ChunkLoader", List.of());
        TELEPORTER_ITEM = getItem(TELEPORTER_MATERIAL, "Teleporter", List.of());
        NETWORK_CONTROLLER_ITEM = getItem(NETWORK_CONTROLLER_MATERIAL, "Network Controller", List.of());
    }

    public static ItemStack getItem(Material material, String title, List<TextComponent> lore) {
        return getItem(material, Component.text(title)
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .color(RedstoneUtilities.PRIMARY_COLOR), lore);
    }

    public static ItemStack getItem(Material material, TextComponent title, List<TextComponent> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addEnchant(Enchantment.PROTECTION_FALL, 1, false);
        itemMeta.lore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.displayName(title);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static boolean isInfometer(ItemStack item) {
        return item.getType() == Items.INFOMETER_MATERIAL
                && Items.INFOMETER_TITLE.equals(item.getItemMeta().displayName());
    }
}
