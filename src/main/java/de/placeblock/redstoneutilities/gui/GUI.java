package de.placeblock.redstoneutilities.gui;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public abstract class GUI implements Listener {
    protected final Inventory inv;

    protected final Player player;


    public GUI(Player player) {
        this.player = player;
        this.inv = createInventory();
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    public void register() {
        RedstoneUtilities plugin = RedstoneUtilities.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        if (event.getInventory().equals(this.inv)) {
            this.unregister();
        }
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        this.onClick(event);
    }

    protected abstract Inventory createInventory();
    protected abstract void onClick(InventoryClickEvent event);

    public abstract void setup();

    public void show() {
        this.player.openInventory(this.inv);
    }

}
