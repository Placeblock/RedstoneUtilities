package de.placeblock.redstoneutilities.impl.autocrafting;

import de.placeblock.redstoneutilities.Recipeable;
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

    Map<Player, Recipeable> players = new HashMap<>();

    public void register(Player player, Recipeable recipeable) {
        this.players.put(player, recipeable);
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
        Recipeable recipeable = this.players.get(player);
        recipeable.setRecipe(recipe);
    }
}
