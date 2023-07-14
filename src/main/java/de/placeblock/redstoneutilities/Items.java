package de.placeblock.redstoneutilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Items {

    public static final Material RECEIVER_MATERIAL = Material.CALIBRATED_SCULK_SENSOR;
    public static final Material SENDER_MATERIAL = Material.SCULK_SHRIEKER;
    public static final Material INFOMETER_MATERIAL = Material.RECOVERY_COMPASS;
    public static final Material AUTOCRAFTER_MATERIAL = Material.CRAFTING_TABLE;

    public static final ItemStack RECEIVER_ITEM;
    public static final ItemStack SENDER_ITEM;
    public static final ItemStack CONNECTOR_ITEM;
    public static final ItemStack INFOMETER_ITEM;
    public static final ItemStack AUTOCRAFTER_ITEM;

    public static final @NotNull TextComponent REDSTONE_INFOMETER_DISPLAYNAME = Component.text("Redstone Infometer")
            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .color(RedstoneUtilities.PRIMARY_COLOR);

    static {
        ItemStack receiverItem = new ItemStack(RECEIVER_MATERIAL);
        ItemMeta receiverMeta = receiverItem.getItemMeta();
        receiverMeta.addEnchant(Enchantment.PROTECTION_FALL, 1, false);
        receiverMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        receiverMeta.displayName(Component.text("Redstone Empfänger")
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .color(RedstoneUtilities.PRIMARY_COLOR));
        receiverItem.setItemMeta(receiverMeta);
        RECEIVER_ITEM = receiverItem;

        ItemStack senderItem = new ItemStack(SENDER_MATERIAL);
        ItemMeta senderMeta = senderItem.getItemMeta();
        senderMeta.addEnchant(Enchantment.PROTECTION_FALL, 1, false);
        senderMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        senderMeta.displayName(Component.text("Redstone Sender")
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .color(RedstoneUtilities.PRIMARY_COLOR));
        senderItem.setItemMeta(senderMeta);
        SENDER_ITEM = senderItem;

        ItemStack connectorItem = new ItemStack(Material.REDSTONE_TORCH);
        ItemMeta connectorMeta = connectorItem.getItemMeta();
        connectorMeta.addEnchant(Enchantment.PROTECTION_FALL, 1, false);
        connectorMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        connectorMeta.displayName(Component.text("Redstone Verbinder")
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .color(RedstoneUtilities.PRIMARY_COLOR));
        connectorItem.setItemMeta(connectorMeta);
        connectorItem.lore(List.of(
                Component.text("Rechtsklick -> Verbinden")
                        .color(RedstoneUtilities.INFERIOR_COLOR)
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
                Component.text("Shift + Rechtsklick -> Trennen")
                        .color(RedstoneUtilities.INFERIOR_COLOR)
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
        ));
        CONNECTOR_ITEM = connectorItem;

        ItemStack infometerItem = new ItemStack(INFOMETER_MATERIAL);
        ItemMeta infometer_meta = infometerItem.getItemMeta();
        infometer_meta.addEnchant(Enchantment.PROTECTION_FALL, 1, false);
        infometer_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        infometer_meta.displayName(REDSTONE_INFOMETER_DISPLAYNAME);
        infometerItem.setItemMeta(infometer_meta);
        INFOMETER_ITEM = infometerItem;

        ItemStack autocrafterItem = new ItemStack(AUTOCRAFTER_MATERIAL);
        ItemMeta autocrafterMeta = autocrafterItem.getItemMeta();
        autocrafterMeta.addEnchant(Enchantment.PROTECTION_FALL, 1, false);
        autocrafterMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        autocrafterMeta.displayName(Component.text("Autocrafter")
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .color(RedstoneUtilities.PRIMARY_COLOR));
        autocrafterItem.setItemMeta(autocrafterMeta);
        autocrafterItem.lore(List.of(
                Component.text("Kann nur auf")
                        .color(RedstoneUtilities.INFERIOR_COLOR)
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
                Component.text("Shift + Rechtsklick -> Trennen")
                        .color(RedstoneUtilities.INFERIOR_COLOR)
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
        ));
        AUTOCRAFTER_ITEM = autocrafterItem;
    }

    public static boolean isInfometer(ItemStack item) {
        return item.getType() == Items.INFOMETER_MATERIAL
                && Items.REDSTONE_INFOMETER_DISPLAYNAME.equals(item.getItemMeta().displayName());
    }
}
