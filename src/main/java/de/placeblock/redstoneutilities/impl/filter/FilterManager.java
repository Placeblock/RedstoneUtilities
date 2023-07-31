package de.placeblock.redstoneutilities.impl.filter;

import de.placeblock.redstoneutilities.BlockEntityManager;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

public class FilterManager implements BlockEntityManager {
    private FilterListener filterListener;

    public void setup(RedstoneUtilities plugin) {
        new FilterRecipe().register();

        PluginManager pluginManager = plugin.getServer().getPluginManager();
        this.filterListener = new FilterListener();
        pluginManager.registerEvents(this.filterListener, plugin);

        plugin.getBlockEntityTypeRegistry().register(new FilterBlockEntityType(plugin));
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this.filterListener);
    }


}
