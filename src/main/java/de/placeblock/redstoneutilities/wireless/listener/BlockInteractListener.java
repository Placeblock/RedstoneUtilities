package de.placeblock.redstoneutilities.wireless.listener;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.Messages;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.wireless.ConnectorHandler;
import de.placeblock.redstoneutilities.wireless.InteractionPDCUtil;
import de.placeblock.redstoneutilities.wireless.Wireless;
import de.placeblock.redstoneutilities.wireless.infometer.InfometerListGUI;
import de.placeblock.redstoneutilities.wireless.infometer.ItemPDCUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlockInteractListener implements Listener {

    @EventHandler
    public void on(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Interaction interaction)) return;

        this.handleConnection(player, interaction);
        this.handleInfometer(player, interaction);
        this.handleType(player, interaction);
    }

    private void handleType(Player player, Interaction interaction) {
        ItemStack item = player.getInventory().getItemInMainHand();
        Wireless wireless = RedstoneUtilities.getInstance().getWireless();
        if (wireless.isInfometer(item) || item.isSimilar(Items.CONNECTOR_ITEM)) return;
        InteractionPDCUtil.setType(interaction, item.getType());
        wireless.setTypeEntities(interaction, item.getType());
    }

    private void handleInfometer(Player player, Interaction interaction) {
        ItemStack item = player.getInventory().getItemInMainHand();
        Wireless wireless = RedstoneUtilities.getInstance().getWireless();
        if (!wireless.isInfometer(item)) return;
        if (player.isSneaking()) {
            this.handleInfometerGUI(player, interaction, item);
        } else {
            this.handleInfometerInfo(player, interaction);
        }
    }

    private void handleInfometerGUI(Player player, Interaction interaction, ItemStack item) {
        if (InteractionPDCUtil.isReceiver(interaction)) {
            List<Location> registeredReceivers = ItemPDCUtil.getReceivers(item);
            for (Location registeredReceiver : registeredReceivers) {
                if (registeredReceiver.equals(interaction.getLocation().toBlockLocation())) {
                    ItemPDCUtil.removeReceiver(item, registeredReceiver);
                    player.sendMessage(Messages.REMOVED_FROM_INFOMETER);
                    return;
                }
            }
            System.out.println(interaction.getLocation().toBlockLocation());
            ItemPDCUtil.addReceiver(item, interaction.getLocation());
            player.sendMessage(Messages.ADDED_TO_INFOMETER);
        } else {
            player.sendMessage(Messages.ONLY_RECEIVER_INFOMETER);
        }
    }

    private void handleInfometerInfo(Player player, Interaction interaction) {
        if (InteractionPDCUtil.isReceiver(interaction)) {
            List<Location> senders = InteractionPDCUtil.getSenders(interaction);
            String name = InteractionPDCUtil.getName(interaction);
            player.sendMessage(Messages.getInfometerReceiver(senders, name));
        } else if (InteractionPDCUtil.isSender(interaction)) {
            List<Location> receivers = InteractionPDCUtil.getReceivers(interaction);
            player.sendMessage(Messages.getInfometerSender(receivers));
        }
    }

    private void handleConnection(Player player, Interaction interaction) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.isSimilar(Items.CONNECTOR_ITEM)) return;

        Wireless wireless = RedstoneUtilities.getInstance().getWireless();
        ConnectorHandler connectorHandler = wireless.getConnectorHandler();
        if (InteractionPDCUtil.isSender(interaction)) {
            connectorHandler.addPlayer(player, interaction.getLocation(), player.isSneaking());
            player.sendMessage(Messages.CONNECT_CANCEL);

        } else if (InteractionPDCUtil.isReceiver(interaction) && connectorHandler.hasPlayer(player)) {
            ConnectorHandler.ConnectorInfo connectorInfo = connectorHandler.getPlayer(player);
            if (connectorInfo.isDestroying()) {
                Location senderLocation = connectorInfo.getLocation();
                Interaction senderInteraction = Util.getInteraction(senderLocation);
                if (senderInteraction == null) return;
                InteractionPDCUtil.removeReceiver(senderInteraction, interaction.getLocation());
                InteractionPDCUtil.removeSender(interaction, senderLocation.getBlock().getLocation());
                player.sendMessage(Messages.DISCONNECTED);
            } else {
                int cost = connectorHandler.getCost(player);
                if (connectorHandler.removeCost(player, cost)) {
                    connectorHandler.removePlayer(player);
                    Location senderLocation = connectorInfo.getLocation();
                    wireless.addReceiverToSender(senderLocation, interaction.getLocation());
                    wireless.addSenderToReceiver(senderLocation, interaction.getLocation());
                    player.sendMessage(Messages.CONNECTED);
                }
            }

        }
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        Wireless wireless = RedstoneUtilities.getInstance().getWireless();
        ItemStack item = event.getItem();
        if (item != null && wireless.isInfometer(item)) {
            InfometerListGUI infometerListGUI = new InfometerListGUI(player, item);
            infometerListGUI.setup();
            infometerListGUI.register();
            infometerListGUI.show();
        }
    }

}
