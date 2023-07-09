package de.placeblock.redstoneutilities.wireless.listener;

import de.placeblock.redstoneutilities.Messages;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.wireless.InteractionPDCUtil;
import de.placeblock.redstoneutilities.wireless.Wireless;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(event.getDamager() instanceof Player player)
            || !(entity instanceof Interaction interaction)
            || (!InteractionPDCUtil.isReceiver(interaction) && !InteractionPDCUtil.isSender(interaction))) return;

        Wireless wireless = RedstoneUtilities.getInstance().getWireless();

        if (InteractionPDCUtil.isReceiver(interaction)) {
            List<Location> senders = InteractionPDCUtil.getSenders(interaction);
            for (Location sender : senders) {
                Interaction senderInteraction = Util.getInteraction(sender);
                if (senderInteraction == null || !InteractionPDCUtil.isSender(senderInteraction)) continue;
                InteractionPDCUtil.removeReceiver(senderInteraction, entity.getLocation());
                this.giveRedstone(player, sender, interaction.getLocation());
            }
            RedstoneWire redstoneWire = Util.getRedstone(interaction);
            if (redstoneWire != null) {
                redstoneWire.setPower(0);
                interaction.getLocation().getBlock().setBlockData(redstoneWire);
            }
        } else if (InteractionPDCUtil.isSender(interaction)) {
            List<Location> receivers = InteractionPDCUtil.getReceivers(interaction);
            for (Location receiver : receivers) {
                Interaction receiverInteraction = Util.getInteraction(receiver);
                if (receiverInteraction == null || !InteractionPDCUtil.isReceiver(receiverInteraction)) continue;
                InteractionPDCUtil.removeSender(receiverInteraction, entity.getLocation().getBlock().getLocation());
                this.giveRedstone(player, interaction.getLocation(), receiver);
            }
        }

        wireless.removeEntity(interaction);
    }

    private void giveRedstone(Player player, Location sender, Location receiver) {
        int cost = RedstoneUtilities.getInstance().getWireless().getConnectorHandler().getCost(sender, receiver);
        player.getInventory().addItem(new ItemStack(Material.REDSTONE, cost));
        player.sendMessage(Messages.REDSTONE_RECEIVED);
    }

}
