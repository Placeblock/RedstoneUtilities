package de.placeblock.redstoneutilities.impl.wireless.recipes;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.Recipe;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class SenderRecipe implements Recipe {
    public void register() {
        NamespacedKey key = new NamespacedKey(RedstoneUtilities.getInstance(), "redstone_sender");

        ShapedRecipe recipe = new ShapedRecipe(key, Items.SENDER_ITEM);
        recipe.shape("RTR", "RSR", "RRR");
        recipe.setIngredient('R', Material.REDSTONE_BLOCK);
        recipe.setIngredient('S', Items.SENDER_MATERIAL);
        recipe.setIngredient('T', Material.REDSTONE);

        Bukkit.addRecipe(recipe);
    }
}
