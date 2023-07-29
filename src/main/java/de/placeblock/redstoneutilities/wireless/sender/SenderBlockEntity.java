package de.placeblock.redstoneutilities.wireless.sender;

import de.placeblock.redstoneutilities.Messages;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.wireless.*;
import de.placeblock.redstoneutilities.wireless.receiver.ReceiverBlockEntity;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SenderBlockEntity extends WirelessBlockEntity<SenderBlockEntity, SenderBlockEntityType> {
    private List<ReceiverBlockEntity> receivers = new ArrayList<>();
    public SenderBlockEntity(SenderBlockEntityType type, UUID uuid) {
        super(type, uuid);
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
    public void summon(Location location) {
        super.summon(location);
        World world = location.getWorld();

        Location displayLocation = location.clone().add(0.25, 0, 0.25);

        world.spawn(displayLocation, BlockDisplay.class, bd -> {
            bd.setBlock(Material.SCULK_SHRIEKER.createBlockData());
            bd.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(0.5F, 0.5F, 0.5F), new AxisAngle4f()));
            this.entityStructure.add(bd.getUniqueId());
        });
    }

    @Override
    public void remove(Player player, boolean drop) {
        for (ReceiverBlockEntity receiver : this.receivers) {
            receiver.getSenders().remove(this);
            WirelessManager wirelessManager = RedstoneUtilities.getInstance().getWirelessManager();
            ConnectorHandler connectorHandler = wirelessManager.getConnectorHandler();
            connectorHandler.giveCost(player, this.getInteraction().getLocation(), receiver.getBlockLocation());
        }
        super.remove(player, drop);
    }

    @Override
    protected void handleInfometerInteraction(Player player) {
        player.sendMessage(Messages.getInfometerSender(this.receivers));
    }

    @Override
    protected void handleConnectorInteraction(Player player) {
        WirelessManager wirelessManager = RedstoneUtilities.getInstance().getWirelessManager();
        ConnectorHandler connectorHandler = wirelessManager.getConnectorHandler();
        if (connectorHandler.hasPlayer(player)) return;

        connectorHandler.addPlayer(player, this, player.isSneaking());
        player.sendMessage(Messages.CONNECT_CANCEL);
    }

    @Override
    public void load() {
        super.load();
        this.receivers = WirelessPDCUtil.getReceivers(this.getInteraction());
    }

    @Override
    public void store() {
        super.store();
        List<UUID> receiverUUIDs = this.receivers.stream().map(BlockEntity::getUuid).toList();
        WirelessPDCUtil.setReceivers(this.getInteraction(), receiverUUIDs);
    }
}
