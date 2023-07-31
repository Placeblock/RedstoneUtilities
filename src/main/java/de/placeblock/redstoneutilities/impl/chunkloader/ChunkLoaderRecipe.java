package de.placeblock.redstoneutilities.impl.chunkloader;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.Recipe;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class ChunkLoaderRecipe implements Recipe {
    @Override
    public void register() {
        NamespacedKey key = new NamespacedKey(RedstoneUtilities.getInstance(), "chunkloader");

        ShapedRecipe recipe = new ShapedRecipe(key, Items.CHUNKLOADER_ITEM);
        recipe.shape("BEB", "ERE", "BEB");
        recipe.setIngredient('R', Material.RESPAWN_ANCHOR);
        recipe.setIngredient('E', Material.END_CRYSTAL);
        recipe.setIngredient('B', Material.REDSTONE_BLOCK);

        Bukkit.addRecipe(recipe);
    }
}
