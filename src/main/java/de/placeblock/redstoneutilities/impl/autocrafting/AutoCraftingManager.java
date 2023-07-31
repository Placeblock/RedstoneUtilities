package de.placeblock.redstoneutilities.impl.autocrafting;

import de.placeblock.redstoneutilities.BlockEntityManager;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.impl.autocrafting.listener.ItemListener;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

@Getter
public class AutoCraftingManager implements BlockEntityManager {
    public static final String AUTO_CRAFTING_NAME = "auto_crafter";

    private RecipeChangeManager recipeChangeManager;
    private ItemListener itemListener;

    public void setup(RedstoneUtilities plugin) {
        new AutoCrafterRecipe().register();

        this.recipeChangeManager = new RecipeChangeManager();
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(this.recipeChangeManager, plugin);
        this.itemListener = new ItemListener();
        pluginManager.registerEvents(this.itemListener, plugin);

        plugin.getBlockEntityTypeRegistry().register(new AutoCrafterBlockEntityType(plugin));
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this.recipeChangeManager);
        HandlerList.unregisterAll(this.itemListener);
    }

}
