package de.placeblock.redstoneutilities.impl.itemnetwork.storage;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import java.util.*;

@Getter
public class StorageChest implements Listener {

    private final StorageController storageController;
    private final Location location;
    private final Inventory inventory;

    public StorageChest(StorageController storageController, Location location, Inventory inventory) {
        this.storageController = storageController;
        this.location = location;
        this.inventory = inventory;

        RedstoneUtilities plugin = RedstoneUtilities.getInstance();
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(this, plugin);
    }

    public List<ItemStack> getContents() {
        return Arrays.stream(this.inventory.getContents()).toList();
    }

    public int removeItem(ItemStack itemStack) {
        HashMap<Integer, ItemStack> leftItems = this.inventory.removeItem(itemStack);
        if (leftItems.size() == 0) return 0;
        return leftItems.get(0).getAmount();
    }

    public int addItem(ItemStack itemStack) {
        HashMap<Integer, ItemStack> notFit = this.inventory.addItem(itemStack);
        if (notFit.size() == 0) return 0;
        return notFit.get(0).getAmount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StorageChest that = (StorageChest) o;
        return Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!event.getBlock().getLocation().equals(this.location)) return;
        this.storageController.removeChest(this);
        this.disable();
    }

    @EventHandler
    public void on(BlockExplodeEvent event) {
        if (event.getExplodedBlockState() == null
            || !event.getExplodedBlockState().getLocation().equals(this.location)) return;
        this.storageController.removeChest(this);
        this.disable();
    }

    @EventHandler
    public void on(InventoryMoveItemEvent event) {
        if (!event.getDestination().equals(this.inventory) &&
            !event.getSource().equals(this.inventory)) return;
        Bukkit.getScheduler().runTaskLater(RedstoneUtilities.getInstance(), this.storageController::updateGUIs, 1L);
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (!event.getInventory().equals(this.inventory)) return;
        this.storageController.updateGUIs();
    }

    @EventHandler
    public void on(InventoryDragEvent event) {
        if (!event.getInventory().equals(this.inventory)) return;
        this.storageController.updateGUIs();
    }

    public void disable() {
        HandlerList.unregisterAll(this);
    }
}
