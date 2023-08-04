package de.placeblock.redstoneutilities.impl.itemnetwork.networkcontroller;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.Recipe;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class NetworkControllerRecipe implements Recipe {
    @Override
    public void register() {
        NamespacedKey key = new NamespacedKey(RedstoneUtilities.getInstance(), "network_controller");

        ShapedRecipe recipe = new ShapedRecipe(key, Items.NETWORK_CONTROLLER_ITEM);
        recipe.shape("ERE", "RCR", "ERE");
        recipe.setIngredient('R', Material.REDSTONE);
        recipe.setIngredient('E', Material.ENDER_PEARL);
        recipe.setIngredient('C', Material.TRAPPED_CHEST);

        Bukkit.addRecipe(recipe);
    }
}
