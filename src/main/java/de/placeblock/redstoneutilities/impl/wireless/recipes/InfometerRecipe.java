package de.placeblock.redstoneutilities.impl.wireless.recipes;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.Recipe;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class InfometerRecipe implements Recipe {
    @Override
    public void register() {
        NamespacedKey key = new NamespacedKey(RedstoneUtilities.getInstance(), "redstone_infometer");

        ShapedRecipe recipe = new ShapedRecipe(key, Items.INFOMETER_ITEM);
        recipe.shape(" S ", "SRS", " S ");
        recipe.setIngredient('S', Material.SCULK);
        recipe.setIngredient('R', Material.RECOVERY_COMPASS);

        Bukkit.addRecipe(recipe);
    }
}
