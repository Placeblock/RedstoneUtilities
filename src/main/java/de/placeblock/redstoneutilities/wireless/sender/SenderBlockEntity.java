package de.placeblock.redstoneutilities.wireless.sender;

import de.placeblock.redstoneutilities.Messages;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.blockentity.BlockEntityRegistry;
import de.placeblock.redstoneutilities.wireless.*;
import de.placeblock.redstoneutilities.wireless.receiver.ReceiverBlockEntity;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SenderBlockEntity extends WirelessBlockEntity<SenderBlockEntity, SenderBlockEntityType> {
    private List<ReceiverBlockEntity> receivers;
    public SenderBlockEntity(SenderBlockEntityType type,
                             Interaction interaction) {
        super(type, interaction);
    }

    public void summonParticles() {
        Location location = this.getCenterLocation();
        World world = location.getWorld();
        final int[] i = {10};
        new BukkitRunnable() {
            @Override
            public void run() {
                world.spawnParticle(Particle.SHRIEK, location, 1, 0 ,0, 0, 1, 1);
                --i[0];
                if (i[0] == 0) {
                    this.cancel();
                }
            }
        }.runTaskTimer(RedstoneUtilities.getInstance(), 0, 5);
    }

    @Override
    public void remove(Player player, boolean drop) {
        super.remove(player, drop);
        for (ReceiverBlockEntity receiver : this.receivers) {
            receiver.getSenders().remove(this);
            System.out.println("REMOVED SENDER FROM RECEIVER");
            Wireless wireless = RedstoneUtilities.getInstance().getWireless();
            ConnectorHandler connectorHandler = wireless.getConnectorHandler();
            connectorHandler.giveCost(player, interaction.getLocation(), receiver.getBlockLocation());
        }
    }

    @Override
    protected void handleInfometerInteraction(Player player) {
        player.sendMessage(Messages.getInfometerSender(this.receivers));
    }

    @Override
    protected void handleConnectorInteraction(Player player) {
        Wireless wireless = RedstoneUtilities.getInstance().getWireless();
        ConnectorHandler connectorHandler = wireless.getConnectorHandler();
        if (connectorHandler.hasPlayer(player)) return;

        connectorHandler.addPlayer(player, this.interaction.getLocation(), player.isSneaking());
        player.sendMessage(Messages.CONNECT_CANCEL);
    }

    @Override
    public void load() {
        super.load();
        BlockEntityRegistry blockEntityRegistry = RedstoneUtilities.getInstance().getBlockEntityRegistry();
        List<Location> receiverLocs = WirelessPDCUtil.getReceivers(interaction);
        List<ReceiverBlockEntity> receivers = new ArrayList<>();
        for (Location receiverLoc : receiverLocs) {
            ReceiverBlockEntity receiverBlockEntity = blockEntityRegistry.get(receiverLoc, ReceiverBlockEntity.class);
            receivers.add(receiverBlockEntity);
        }
        this.receivers = receivers;
    }

    @Override
    public void store() {
        super.store();
        List<Location> receiverLocs = this.receivers.stream().map(BlockEntity::getBlockLocation).toList();
        WirelessPDCUtil.setReceivers(this.interaction, receiverLocs);
    }
}
