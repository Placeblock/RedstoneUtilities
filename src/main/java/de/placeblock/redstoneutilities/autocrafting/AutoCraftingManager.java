package de.placeblock.redstoneutilities.autocrafting;

import de.placeblock.redstoneutilities.BlockEntityManager;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.autocrafting.listener.ItemListener;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

@Getter
public class AutoCraftingManager implements BlockEntityManager {

    private RecipeChangeManager recipeChangeManager;
    private ItemListener itemListener;

    public void setup(RedstoneUtilities plugin) {
        new AutoCrafterRecipe().register();
        this.recipeChangeManager = new RecipeChangeManager();
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(this.recipeChangeManager, plugin);
        this.itemListener = new ItemListener();
        pluginManager.registerEvents(this.itemListener, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this.recipeChangeManager);
        HandlerList.unregisterAll(this.itemListener);
    }

}
