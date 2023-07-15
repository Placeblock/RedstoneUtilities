package de.placeblock.redstoneutilities.wireless.receiver;

import de.placeblock.redstoneutilities.Messages;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.blockentity.BlockEntityRegistry;
import de.placeblock.redstoneutilities.wireless.*;
import de.placeblock.redstoneutilities.wireless.infometer.InfometerPDCUtil;
import de.placeblock.redstoneutilities.wireless.sender.SenderBlockEntity;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReceiverBlockEntity extends WirelessBlockEntity<ReceiverBlockEntity, ReceiverBlockEntityType> {
    private String wirelessName;
    private List<SenderBlockEntity> senders;
    public ReceiverBlockEntity(ReceiverBlockEntityType type,
                               Interaction interaction) {
        super(type, interaction);
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

        Location senderLocation = connectorInfo.getLocation();
        BlockEntityRegistry blockEntityRegistry = RedstoneUtilities.getInstance().getBlockEntityRegistry();
        SenderBlockEntity sender = blockEntityRegistry.get(senderLocation, SenderBlockEntity.class);

        if (connectorInfo.isDestroying()) {
            Interaction senderInteraction = Util.getInteraction(senderLocation);
            if (senderInteraction == null) return;
            this.senders.remove(sender);
            sender.getReceivers().remove(this);
            connectorHandler.removePlayer(player);
            player.sendMessage(Messages.DISCONNECTED);
        } else {
            int cost = connectorHandler.getCost(player);
            if (connectorHandler.removeCost(player, cost)) {
                this.senders.add(sender);
                sender.getReceivers().add(this);
                connectorHandler.removePlayer(player);
                player.sendMessage(Messages.CONNECTED);
            }
        }
    }

    public void summonParticles() {
        Location location = this.getCenterLocation();
        World world = location.getWorld();
        world.spawnParticle(Particle.SCULK_CHARGE, location, 1, 0, 0, 0, 0, 0.0F);
    }

    public void setPower(int power) {
        RedstoneWire redstoneWire = Util.getRedstone(this.interaction);
        if (redstoneWire == null) return;
        redstoneWire.setPower(power);
        this.getBlockLocation().getBlock().setBlockData(redstoneWire);
    }

    @Override
    public void remove(Player player, boolean drop) {
        super.remove(player, drop);
        for (SenderBlockEntity sender : this.senders) {
            sender.getReceivers().remove(this);
            Wireless wireless = RedstoneUtilities.getInstance().getWireless();
            ConnectorHandler connectorHandler = wireless.getConnectorHandler();
            connectorHandler.giveCost(player, sender.getBlockLocation(), this.getBlockLocation());
        }
        this.setPower(0);
    }

    @Override
    public void load() {
        super.load();
        BlockEntityRegistry blockEntityRegistry = RedstoneUtilities.getInstance().getBlockEntityRegistry();
        List<Location> senderLocs = WirelessPDCUtil.getSenders(interaction);
        List<SenderBlockEntity> senders = new ArrayList<>();
        for (Location senderLoc : senderLocs) {
            SenderBlockEntity senderBlockEntity = blockEntityRegistry.get(senderLoc, SenderBlockEntity.class);
            if (senderBlockEntity == null) continue;
            senders.add(senderBlockEntity);
        }
        this.wirelessName = WirelessPDCUtil.getName(interaction);
        this.senders = senders;
    }

    @Override
    public void store() {
        super.store();
        WirelessPDCUtil.setName(this.interaction, this.wirelessName);
        List<Location> senderLocs = this.senders.stream().map(BlockEntity::getBlockLocation).toList();
        WirelessPDCUtil.setSenders(this.interaction, senderLocs);
    }
}
