package de.placeblock.redstoneutilities.impl.filter;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.Recipe;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class FilterRecipe implements Recipe {
    @Override
    public void register() {
        NamespacedKey key = new NamespacedKey(RedstoneUtilities.getInstance(), "item_filter");

        ShapedRecipe recipe = new ShapedRecipe(key, Items.FILTER_ITEM);
        recipe.shape("IRI", "RHR", "IRI");
        recipe.setIngredient('R', Material.REDSTONE);
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('H', Material.HOPPER);

        Bukkit.addRecipe(recipe);
    }
}
