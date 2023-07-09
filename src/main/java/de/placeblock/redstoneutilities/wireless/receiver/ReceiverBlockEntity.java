package de.placeblock.redstoneutilities.wireless.receiver;

import de.placeblock.redstoneutilities.Messages;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import de.placeblock.redstoneutilities.wireless.*;
import de.placeblock.redstoneutilities.wireless.infometer.InfometerPDCUtil;
import de.placeblock.redstoneutilities.wireless.sender.SenderBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ReceiverBlockEntity extends WirelessBlockEntity {
    private final String wirelessName;
    private final List<SenderBlockEntity> senders;
    public ReceiverBlockEntity(BlockEntityType<?> type,
                               Interaction interaction,
                               List<Entity> entityStructure,
                               Material wirelessType,
                               List<ItemDisplay> typeEntities,
                               String wirelessName,
                               List<SenderBlockEntity> senders) {
        super(type, interaction, entityStructure, wirelessType, typeEntities);
        this.wirelessName = wirelessName;
        this.senders = senders;
    }


    protected void handleInfometerInteraction(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (player.isSneaking()) {
            this.handleInfometerGUI(player, item);
        } else {
            this.handleInfometerInfo(player);
        }
    }

    private void handleInfometerGUI(Player player, ItemStack item) {
        List<Location> registeredReceivers = InfometerPDCUtil.getReceivers(item);
        for (Location registeredReceiver : registeredReceivers) {
            if (registeredReceiver.equals(this.getBlockLocation())) {
                InfometerPDCUtil.removeReceiver(item, registeredReceiver);
                player.sendMessage(Messages.REMOVED_FROM_INFOMETER);
                return;
            }
        }
        InfometerPDCUtil.addReceiver(item, this.getBlockLocation());
        player.sendMessage(Messages.ADDED_TO_INFOMETER);
    }

    private void handleInfometerInfo(Player player) {
        player.sendMessage(Messages.getInfometerReceiver(this.senders, this.wirelessName));
    }

    protected void handleConnectorInteraction(Player player) {
        Wireless wireless = RedstoneUtilities.getInstance().getWireless();
        ConnectorHandler connectorHandler = wireless.getConnectorHandler();
        if (!connectorHandler.hasPlayer(player)) return;

        ConnectorHandler.ConnectorInfo connectorInfo = connectorHandler.getPlayer(player);
        if (connectorInfo.isDestroying()) {
            Location senderLocation = connectorInfo.getLocation();
            Interaction senderInteraction = Util.getInteraction(senderLocation);
            if (senderInteraction == null) return;
            WirelessPDCUtil.removeReceiver(senderInteraction, interaction.getLocation());
            WirelessPDCUtil.removeSender(interaction, senderLocation.getBlock().getLocation());
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

    public void setPower(int power) {
        RedstoneWire redstoneWire = Util.getRedstone(this.interaction);
        if (redstoneWire == null) return;
        redstoneWire.setPower(power);
        this.getBlockLocation().getBlock().setBlockData(redstoneWire);
    }

    @Override
    public void store() {
        super.store();
    }
}
