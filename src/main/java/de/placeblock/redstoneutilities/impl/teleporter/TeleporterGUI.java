package de.placeblock.redstoneutilities.impl.teleporter;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.gui.GUI;
import de.placeblock.redstoneutilities.upgrades.UpgradeGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TeleporterGUI extends GUI {
    public static final ItemStack CHANGE_NAME_ITEM;

    static {
        CHANGE_NAME_ITEM = Util.getItem(Material.PAPER, Component.text("Umbenennen"));
    }

    private final TeleporterBlockEntity blockEntity;
    public TeleporterGUI(Player player, TeleporterBlockEntity blockEntity) {
        super(player);
        this.blockEntity = blockEntity;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,  InventoryType.HOPPER, Component.text("AutoCrafter"));
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getSlot() == 1) {
            this.player.closeInventory();
            RedstoneUtilities instance = RedstoneUtilities.getInstance();
            instance.getTextInputHandler().addPlayer(player, (name) -> {
                this.blockEntity.setTeleporterName(name);
                for (TeleporterBlockEntity teleporter : this.blockEntity.getSelfTargetTeleporters()) {
                    teleporter.removeTargetTeleporterEntity();
                    teleporter.summonTargetTeleporterEntity();
                }
            });
        } else if (event.getSlot() == 3) {
            UpgradeGUI upgradeGUI = new UpgradeGUI(this.player, this.blockEntity);
            upgradeGUI.setup();
            upgradeGUI.register();
            upgradeGUI.show();
        }
    }

    @Override
    public void setup() {
        this.inv.setItem(1, CHANGE_NAME_ITEM);
        this.inv.setItem(3, UpgradeGUI.UPGRADE_ITEM);
    }
}
