package de.placeblock.redstoneutilities.autocrafting;

import de.placeblock.redstoneutilities.gui.GUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class AutoCrafterGUI extends GUI {
    public AutoCrafterGUI(Player player) {
        super(player);
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,  9*3, Component.text("AutoCrafter"));
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void setup() {

    }
}
