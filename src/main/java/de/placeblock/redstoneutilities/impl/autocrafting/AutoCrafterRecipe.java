package de.placeblock.redstoneutilities.impl.autocrafting;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.Recipe;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class AutoCrafterRecipe implements Recipe {
    @Override
    public void register() {
        NamespacedKey key = new NamespacedKey(RedstoneUtilities.getInstance(), "auto_crafter");

        ShapedRecipe recipe = new ShapedRecipe(key, Items.AUTOCRAFTER_ITEM);
        recipe.shape("IRI", "RCR", "IRI");
        recipe.setIngredient('C', Material.CRAFTING_TABLE);
        recipe.setIngredient('R', Material.REDSTONE);
        recipe.setIngredient('I', Material.IRON_INGOT);

        Bukkit.addRecipe(recipe);
    }
}
