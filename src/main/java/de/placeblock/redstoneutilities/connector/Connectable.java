package de.placeblock.redstoneutilities.connector;

import de.placeblock.redstoneutilities.Messages;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface Connectable<B extends Connectable<B, T>, T extends Connectable<T, B>> {

    Location getCenterLocation();

    Class<T> getTargetType();

    void onConnect(Player player, Connectable<T, B> connected);

    void onDisconnect(Player player, Connectable<T, B> disconnected);

    boolean canConnect(Player player, Connectable<T, B> connectable);

    Material getCostType();

    default void handleInteraction(Player player) {
        ConnectorHandler connectorHandler = RedstoneUtilities.getInstance().getConnectorHandler();
        if (connectorHandler.hasPlayer(player)) {
            ConnectorHandler.ConnectorInfo connectorInfo = connectorHandler.getPlayer(player);
            Connectable<?, ?> connectable = connectorInfo.getConnectable();
            if (connectable.equals(this)) {
                player.sendMessage(Messages.CANNOT_CONNECT_SELF);
                return;
            }
            if (this.getClass() == connectable.getTargetType()
                && connectable.getClass() == this.getTargetType()) {
                Connectable<T, B> saveConnectable = (Connectable<T, B>) connectable;
                if (connectorInfo.isDestroying()) {
                    saveConnectable.onDisconnect(player, this);
                    player.sendMessage(Messages.DISCONNECTED);
                    if (saveConnectable.getCostType() != null) {
                        int cost = connectorHandler.getCost(player);
                        connectorHandler.giveCost(player, cost, saveConnectable.getCostType());
                    }
                } else if (this.canConnect(player, saveConnectable)){
                    if (saveConnectable.getCostType() != null) {
                        int cost = connectorHandler.getCost(player);
                        if (!connectorHandler.removeCost(player, cost, saveConnectable.getCostType())) {
                            return;
                        }
                    }
                    player.sendMessage(Messages.CONNECTED);
                    saveConnectable.onConnect(player, this);
                }
                connectorHandler.removePlayer(player);
            }
        } else {
            connectorHandler.addPlayer(player, this, this.getCostType(), player.isSneaking());
            player.sendMessage(Messages.CONNECT_CANCEL);
        }
    }

}
