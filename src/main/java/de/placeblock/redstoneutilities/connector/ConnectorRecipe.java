package de.placeblock.redstoneutilities.connector;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.Recipe;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class ConnectorRecipe implements Recipe {
    @Override
    public void register() {
        NamespacedKey key = new NamespacedKey(RedstoneUtilities.getInstance(), "redstone_connector");

        ShapedRecipe recipe = new ShapedRecipe(key, Items.CONNECTOR_ITEM);
        recipe.shape("SRS", "RER", "SRS");
        recipe.setIngredient('R', Material.REDSTONE);
        recipe.setIngredient('S', Material.SCULK);
        recipe.setIngredient('E', Material.ENDER_PEARL);

        Bukkit.addRecipe(recipe);
    }
}
