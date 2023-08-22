package de.placeblock.redstoneutilities.upgrades;

import de.placeblock.betterinventories.content.item.ClickData;
import de.placeblock.betterinventories.content.item.GUIButton;
import de.placeblock.betterinventories.gui.impl.CanvasGUI;
import de.placeblock.betterinventories.util.ItemBuilder;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class UpgradeGUI extends CanvasGUI {

    private final Upgradeable upgradeable;
    private final Player player;

    public UpgradeGUI(Upgradeable upgradeable, Player player) {
        super(RedstoneUtilities.getInstance(), Util.primary("Upgrades"), InventoryType.HOPPER);
        this.upgradeable = upgradeable;
        this.player = player;

        this.setup();
        this.update();
    }

    public void setup() {
        List<Upgrade> allowedUpgrades = this.upgradeable.getAllowedUpgrades();
        List<Integer> slots = this.getSlots(allowedUpgrades.size());
        for (int i = 0; i < allowedUpgrades.size(); i++) {
            int slot = slots.get(i);
            Upgrade upgrade = allowedUpgrades.get(i);
            this.getCanvas().setSectionAt(slot, new SingleUpgradeGUIButton(this, this.player, this.upgradeable, upgrade));
        }
    }

    private List<Integer> getSlots(int amount) {
        List<Integer> slots = new ArrayList<>();
        int delta = amount - 1;
        int start = 2 - delta;
        for (int i = start; i <= 2 + delta; i = i + 2) {
            slots.add(i);
        }
        return slots;
    }

    private static class SingleUpgradeGUIButton extends GUIButton {
        private final UpgradeGUI upgradeGUI;
        private final Upgrade upgrade;

        public SingleUpgradeGUIButton(UpgradeGUI gui, Player player, Upgradeable upgradeable, Upgrade upgrade) {
            super(gui, getItem(player, upgradeable, upgrade));
            this.upgradeGUI = gui;
            this.upgrade = upgrade;
        }

        private static ItemStack getItem(Player player, Upgradeable upgradeable, Upgrade upgrade) {
            int level = upgradeable.getUpgradeLevel(upgrade, 0);
            ItemBuilder itemBuilder = new ItemBuilder(Util.primary(upgrade.getName() + " (" + level + ")"), upgrade.getMaterial());
            if (upgradeable.hasReachedMaxLevel(upgrade)) {
                itemBuilder.lore(List.of(
                        Component.text("Maximales Level erreicht!")
                                .color(RedstoneUtilities.INFERIOR_COLOR)
                                .decoration(TextDecoration.ITALIC, false)
                ));
            } else {
                ItemStack nextLevelItem = upgradeable.getNextLevelItem(upgrade);
                if (player.getInventory().containsAtLeast(nextLevelItem, 1)) {
                    itemBuilder.lore(List.of(
                            Component.text("Klicken zum verbessern!")
                                    .color(RedstoneUtilities.INFERIOR_COLOR)
                                    .decoration(TextDecoration.ITALIC, false)
                    ));
                } else {
                    itemBuilder.lore(List.of(
                            Component.text("Dir fehlen die Ressourcen!")
                                    .color(RedstoneUtilities.INFERIOR_COLOR)
                                    .decoration(TextDecoration.ITALIC, false)
                    ));
                }
            }
            return itemBuilder.build();
        }

        @Override
        public void onClick(ClickData data) {
            Upgradeable upgradeable = this.upgradeGUI.upgradeable;
            if (upgradeable.hasReachedMaxLevel(this.upgrade)) return;

            ItemStack nextLevelItem = upgradeable.getNextLevelItem(upgrade);
            Player player = this.upgradeGUI.player;
            PlayerInventory inventory = player.getInventory();
            if (!inventory.containsAtLeast(nextLevelItem, 1)) return;
            inventory.removeItem(nextLevelItem);
            upgradeable.upgrade(this.upgrade);
            this.setItemStack(getItem(player, upgradeable, this.upgrade));
            this.getGui().update();
        }
    }
}
