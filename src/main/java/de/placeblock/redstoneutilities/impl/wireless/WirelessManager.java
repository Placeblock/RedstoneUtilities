package de.placeblock.redstoneutilities.impl.wireless;

import de.placeblock.redstoneutilities.BlockEntityManager;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.impl.wireless.listener.InfometerListener;
import de.placeblock.redstoneutilities.impl.wireless.listener.RedstoneListener;
import de.placeblock.redstoneutilities.impl.wireless.receiver.ReceiverBlockEntityType;
import de.placeblock.redstoneutilities.impl.wireless.recipes.InfometerRecipe;
import de.placeblock.redstoneutilities.impl.wireless.recipes.ReceiverRecipe;
import de.placeblock.redstoneutilities.impl.wireless.recipes.SenderRecipe;
import de.placeblock.redstoneutilities.impl.wireless.sender.SenderBlockEntityType;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

public class WirelessManager implements BlockEntityManager {
    public static final String SENDER_NAME = "wireless_sender";
    public static final String RECEIVER_NAME = "wireless_receiver";
    public static final String WIRELESS_NAME = "wireless";

    private InfometerListener infometerListener;
    private RedstoneListener redstoneListener;

    public void setup(RedstoneUtilities plugin) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        this.infometerListener = new InfometerListener();
        pluginManager.registerEvents(this.infometerListener, plugin);
        this.redstoneListener = new RedstoneListener(plugin);
        pluginManager.registerEvents(this.redstoneListener, plugin);

        new ReceiverRecipe().register();
        new SenderRecipe().register();
        new InfometerRecipe().register();

        plugin.getBlockEntityTypeRegistry().register(new ReceiverBlockEntityType(plugin));
        plugin.getBlockEntityTypeRegistry().register(new SenderBlockEntityType(plugin));
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this.infometerListener);
        HandlerList.unregisterAll(this.redstoneListener);
    }

}
