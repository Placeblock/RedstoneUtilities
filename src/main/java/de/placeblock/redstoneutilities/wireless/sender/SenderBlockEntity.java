package de.placeblock.redstoneutilities.wireless.sender;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.Messages;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.wireless.ConnectorHandler;
import de.placeblock.redstoneutilities.wireless.Wireless;
import de.placeblock.redstoneutilities.wireless.WirelessBlockEntity;
import de.placeblock.redstoneutilities.wireless.WirelessPDCUtil;
import de.placeblock.redstoneutilities.wireless.receiver.ReceiverBlockEntity;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

@Getter
public class SenderBlockEntity extends WirelessBlockEntity<SenderBlockEntity, SenderBlockEntityType> {
    private final List<ReceiverBlockEntity> receivers;
    public SenderBlockEntity(SenderBlockEntityType type,
                             Interaction interaction,
                             List<Entity> entityStructure,
                             Material wirelessType,
                             List<ItemDisplay> typeEntities,
                             List<ReceiverBlockEntity> receivers) {
        super(type, interaction, entityStructure, wirelessType, typeEntities);
        this.receivers = receivers;
    }

    @Override
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.isSimilar(Items.CONNECTOR_ITEM)) return;

        Wireless wireless = RedstoneUtilities.getInstance().getWireless();
        ConnectorHandler connectorHandler = wireless.getConnectorHandler();
        if (!connectorHandler.hasPlayer(player)) return;

        connectorHandler.addPlayer(player, this.interaction.getLocation(), player.isSneaking());
        player.sendMessage(Messages.CONNECT_CANCEL);
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
            WirelessPDCUtil.removeSender(receiver.getInteraction(), this.getBlockLocation());
            Wireless wireless = RedstoneUtilities.getInstance().getWireless();
            ConnectorHandler connectorHandler = wireless.getConnectorHandler();
            connectorHandler.giveCost(player, interaction.getLocation(), receiver.getBlockLocation());
        }
    }

    @Override
    public void store() {
        super.store();
    }

    @Override
    protected void handleInfometerInteraction(Player player) {
        player.sendMessage(Messages.getInfometerSender(this.receivers));
    }

    @Override
    protected void handleConnectorInteraction(Player player) {

    }
}
