package de.placeblock.redstoneutilities.upgrades;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.gui.GUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class UpgradeGUI extends GUI {
    public static final ItemStack UPGRADE_ITEM = Util.getItem(Material.NETHERITE_INGOT, Component.text("Upgrades"));

    private final Upgradeable upgradeable;

    public UpgradeGUI(Player player, Upgradeable upgradeable) {
        super(player);
        this.upgradeable = upgradeable;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 9);
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        Material material = item.getType();
        Upgrade upgrade = Upgrade.getUpgrade(material);
        if (upgrade == null || this.upgradeable.hasReachedMaxLevel(upgrade)) return;

        ItemStack nextLevelItem = this.upgradeable.getNextLevelItem(upgrade);
        PlayerInventory inventory = player.getInventory();
        if (!inventory.containsAtLeast(nextLevelItem, 1)) return;
        inventory.removeItem(nextLevelItem);
        this.upgradeable.upgrade(upgrade);
        this.player.closeInventory();
    }

    @Override
    public void setup() {
        List<Upgrade> allowedUpgrades = this.upgradeable.getAllowedUpgrades();
        List<Integer> slots = this.getSlots(allowedUpgrades.size());
        for (int i = 0; i < allowedUpgrades.size(); i++) {
            int slot = slots.get(i);
            Upgrade upgrade = allowedUpgrades.get(i);
            this.inv.setItem(slot, this.getItem(upgrade));
        }
    }

    private ItemStack getItem(Upgrade upgrade) {
        ItemStack item = new ItemStack(upgrade.getMaterial());
        ItemMeta itemMeta = item.getItemMeta();
        int level = this.upgradeable.getUpgradeLevel(upgrade, 0);
        TextComponent displayName = Component.text(upgrade.getName() + " (" + level + ")")
            .color(RedstoneUtilities.PRIMARY_COLOR)
            .decoration(TextDecoration.ITALIC, false);
        itemMeta.displayName(displayName);
        if (this.upgradeable.hasReachedMaxLevel(upgrade)) {
            itemMeta.lore(List.of(
                Component.text("Maximales Level erreicht!")
                    .color(RedstoneUtilities.INFERIOR_COLOR)
                    .decoration(TextDecoration.ITALIC, false)
            ));
        } else {
            ItemStack nextLevelItem = this.upgradeable.getNextLevelItem(upgrade);
            if (this.player.getInventory().containsAtLeast(nextLevelItem, 1)) {
                itemMeta.lore(List.of(
                    Component.text("Klicken zum verbessern!")
                        .color(RedstoneUtilities.INFERIOR_COLOR)
                        .decoration(TextDecoration.ITALIC, false)
                ));
            } else {
                itemMeta.lore(List.of(
                    Component.text("Dir fehlen die Ressourcen!")
                        .color(RedstoneUtilities.INFERIOR_COLOR)
                        .decoration(TextDecoration.ITALIC, false)
                ));
            }
        }
        item.setItemMeta(itemMeta);
        return item;
    }

    private List<Integer> getSlots(int amount) {
        List<Integer> slots = new ArrayList<>();
        int delta = amount - 1;
        int start = 4 - delta;
        for (int i = start; i <= 4 + delta; i = i + 2) {
            slots.add(i);
        }
        return slots;
    }
}
