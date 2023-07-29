package de.placeblock.redstoneutilities.wireless;

import de.placeblock.redstoneutilities.BlockEntityManager;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.wireless.listener.InfometerListener;
import de.placeblock.redstoneutilities.wireless.listener.RedstoneListener;
import de.placeblock.redstoneutilities.wireless.receiver.ReceiverBlockEntityType;
import de.placeblock.redstoneutilities.wireless.recipes.ConnectorRecipe;
import de.placeblock.redstoneutilities.wireless.recipes.InfometerRecipe;
import de.placeblock.redstoneutilities.wireless.recipes.ReceiverRecipe;
import de.placeblock.redstoneutilities.wireless.recipes.SenderRecipe;
import de.placeblock.redstoneutilities.wireless.sender.SenderBlockEntityType;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

public class WirelessManager implements BlockEntityManager {

    @Getter
    private ConnectorHandler connectorHandler;
    private InfometerListener infometerListener;
    private RedstoneListener redstoneListener;

    public void setup(RedstoneUtilities plugin) {
        this.connectorHandler = new ConnectorHandler();
        this.connectorHandler.start(plugin);
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(this.connectorHandler, plugin);

        this.infometerListener = new InfometerListener();
        pluginManager.registerEvents(this.infometerListener, plugin);
        this.redstoneListener = new RedstoneListener(plugin);
        pluginManager.registerEvents(this.redstoneListener, plugin);

        new ReceiverRecipe().register();
        new SenderRecipe().register();
        new ConnectorRecipe().register();
        new InfometerRecipe().register();

        plugin.getBlockEntityTypeRegistry().register(new ReceiverBlockEntityType(plugin));
        plugin.getBlockEntityTypeRegistry().register(new SenderBlockEntityType(plugin));
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this.connectorHandler);
        HandlerList.unregisterAll(this.infometerListener);
        HandlerList.unregisterAll(this.redstoneListener);
    }

}
