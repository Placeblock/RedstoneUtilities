package de.placeblock.redstoneutilities.autocrafting;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.autocrafting.listener.ItemListener;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;

@Getter
public class AutoCrafting {

    private RecipeChangeManager recipeChangeManager;

    public void setup(RedstoneUtilities plugin) {
        new AutoCrafterRecipe().register();
        this.recipeChangeManager = new RecipeChangeManager();
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(this.recipeChangeManager, plugin);
        ItemListener itemListener = new ItemListener();
        pluginManager.registerEvents(itemListener, plugin);
    }

}
