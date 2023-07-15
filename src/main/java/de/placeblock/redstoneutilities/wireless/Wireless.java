package de.placeblock.redstoneutilities.wireless;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.wireless.listener.InfometerListener;
import de.placeblock.redstoneutilities.wireless.listener.RedstoneListener;
import de.placeblock.redstoneutilities.wireless.recipes.ConnectorRecipe;
import de.placeblock.redstoneutilities.wireless.recipes.InfometerRecipe;
import de.placeblock.redstoneutilities.wireless.recipes.ReceiverRecipe;
import de.placeblock.redstoneutilities.wireless.recipes.SenderRecipe;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;

public class Wireless {

    @Getter
    private ConnectorHandler connectorHandler;

    public void setup(RedstoneUtilities plugin) {
        this.connectorHandler = new ConnectorHandler();
        this.connectorHandler.start(plugin);
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(this.connectorHandler, plugin);

        InfometerListener infometerListener = new InfometerListener();
        pluginManager.registerEvents(infometerListener, plugin);
        RedstoneListener redstoneListener = new RedstoneListener(plugin);
        pluginManager.registerEvents(redstoneListener, plugin);

        new ReceiverRecipe().register();
        new SenderRecipe().register();
        new ConnectorRecipe().register();
        new InfometerRecipe().register();

    }

}
