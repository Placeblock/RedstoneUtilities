package de.placeblock.redstoneutilities.impl.autocrafting;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;
import java.util.Map;

public class RecipeChangeManager implements Listener {

    Map<Player, AutoCrafterBlockEntity> players = new HashMap<>();

    public void register(Player player, AutoCrafterBlockEntity blockEntity) {
        this.players.put(player, blockEntity);
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)
            || event.getInventory().getType() != InventoryType.WORKBENCH
            || !this.players.containsKey(player)) return;
        this.players.remove(player);
    }

    @EventHandler
    public void on(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)
            || !this.players.containsKey(player)) return;
        Recipe recipe = event.getRecipe();
        AutoCrafterBlockEntity blockEntity = this.players.get(player);
        blockEntity.setRecipe(recipe);
    }
}
