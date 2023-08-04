package de.placeblock.redstoneutilities.impl.itemnetwork;

import de.placeblock.redstoneutilities.BlockEntityManager;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.impl.itemnetwork.networkcontroller.NetworkControllerBlockEntityType;
import de.placeblock.redstoneutilities.impl.itemnetwork.networkcontroller.NetworkControllerRecipe;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

public class ItemNetworkManager implements BlockEntityManager {
    public static final String ITEM_NETWORK_NAME = "item_network";
    private ItemNetworkListener itemNetworkListener;

    @Override
    public void setup(RedstoneUtilities plugin) {
        plugin.getBlockEntityTypeRegistry().register(new NetworkControllerBlockEntityType(plugin));

        PluginManager pluginManager = plugin.getServer().getPluginManager();
        this.itemNetworkListener = new ItemNetworkListener();
        pluginManager.registerEvents(this.itemNetworkListener, plugin);

        new NetworkControllerRecipe().register();

    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this.itemNetworkListener);
    }
}
