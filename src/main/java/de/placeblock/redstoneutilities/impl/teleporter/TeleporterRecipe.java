package de.placeblock.redstoneutilities.impl.teleporter;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.Recipe;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class TeleporterRecipe implements Recipe {
    @Override
    public void register() {
        NamespacedKey key = new NamespacedKey(RedstoneUtilities.getInstance(), "teleporter");

        ShapedRecipe recipe = new ShapedRecipe(key, Items.TELEPORTER_ITEM);
        recipe.shape("IRI", "RER", "IRI");
        recipe.setIngredient('E', Material.ENDER_PEARL);
        recipe.setIngredient('R', Material.REDSTONE);
        recipe.setIngredient('I', Material.IRON_BLOCK);

        Bukkit.addRecipe(recipe);
    }
}
