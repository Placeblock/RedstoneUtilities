package de.placeblock.redstoneutilities.impl.wireless.receiver;

import de.placeblock.redstoneutilities.*;
import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.connector.Connectable;
import de.placeblock.redstoneutilities.connector.ConnectorHandler;
import de.placeblock.redstoneutilities.impl.wireless.*;
import de.placeblock.redstoneutilities.impl.wireless.infometer.InfometerPDCUtil;
import de.placeblock.redstoneutilities.impl.wireless.sender.SenderBlockEntity;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ReceiverBlockEntity extends WirelessBlockEntity<ReceiverBlockEntity, ReceiverBlockEntityType> implements Connectable<ReceiverBlockEntity, SenderBlockEntity>, Powerable {
    private String wirelessName = "Unknown";
    private List<SenderBlockEntity> senders = new ArrayList<>();

    public ReceiverBlockEntity(ReceiverBlockEntityType type, UUID uuid, Location location) {
        super(type, uuid, location);
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
        List<ReceiverBlockEntity> registeredReceivers = InfometerPDCUtil.getReceivers(item);
        for (ReceiverBlockEntity registeredReceiver : registeredReceivers) {
            if (registeredReceiver.equals(this)) {
                InfometerPDCUtil.removeReceiver(item, registeredReceiver.getUuid());
                player.sendMessage(Messages.REMOVED_FROM_INFOMETER);
                return;
            }
        }
        InfometerPDCUtil.addReceiver(item, this);
        player.sendMessage(Messages.ADDED_TO_INFOMETER);
    }

    private void handleInfometerInfo(Player player) {
        player.sendMessage(Messages.getInfometerReceiver(this.senders, this.wirelessName));
    }

    public void summonParticles() {
        Location location = this.getCenterLocation();
        World world = location.getWorld();
        world.spawnParticle(Particle.SCULK_CHARGE, location, 1, 0, 0, 0, 0, 0.0F);
    }

    public void setPower(int power) {
        RedstoneWire redstoneWire = Util.getRedstone(this.getInteraction());
        if (redstoneWire == null) return;
        redstoneWire.setPower(power);
        this.getBlockLocation().getBlock().setBlockData(redstoneWire);
    }

    @Override
    public void summon(Location location) {
        super.summon(location);
        World world = location.getWorld();

        Location displayLocation = location.clone().add(0.25, 0, 0.25);

        world.spawn(displayLocation, BlockDisplay.class, bd -> {
            bd.setBlock(Material.CALIBRATED_SCULK_SENSOR.createBlockData());
            bd.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(0.5F, 0.5F, 0.5F), new AxisAngle4f()));
            this.entityStructure.add(bd.getUniqueId());
        });
    }

    @Override
    public void remove(Player player, boolean drop) {
        for (SenderBlockEntity sender : this.senders) {
            sender.getReceivers().remove(this);
            ConnectorHandler connectorHandler = RedstoneUtilities.getInstance().getConnectorHandler();
            int cost = connectorHandler.getCost(sender.getBlockLocation(), this.getBlockLocation());
            connectorHandler.giveCost(player, cost, Material.REDSTONE);
        }
        this.setPower(0);
        super.remove(player, drop);
    }

    @Override
    public void load() {
        super.load();
        Interaction interaction = this.getInteraction();
        List<SenderBlockEntity> senders = WirelessPDCUtil.getSenders(interaction);
        this.wirelessName = WirelessPDCUtil.getName(interaction);
        this.senders = senders;
    }

    @Override
    public void store() {
        super.store();
        Interaction interaction = this.getInteraction();
        WirelessPDCUtil.setName(interaction, this.wirelessName);
        List<UUID> senderUUIDs = this.senders.stream().map(BlockEntity::getUuid).toList();
        WirelessPDCUtil.setSenders(interaction, senderUUIDs);
    }

    @Override
    public Class<SenderBlockEntity> getTargetType() {
        return SenderBlockEntity.class;
    }

    @Override
    public void onConnect(Player player, Connectable<SenderBlockEntity, ReceiverBlockEntity> connected) {
        SenderBlockEntity sender = (SenderBlockEntity) connected;
        this.senders.add(sender);
        sender.getReceivers().add(this);
        this.onChange();
    }

    @Override
    public void onDisconnect(Player player, Connectable<SenderBlockEntity, ReceiverBlockEntity> disconnected) {
        SenderBlockEntity sender = (SenderBlockEntity) disconnected;
        this.senders.remove(sender);
        sender.getReceivers().remove(this);
        this.onChange();
    }

    @Override
    public boolean canConnect(Player player, Connectable<SenderBlockEntity, ReceiverBlockEntity> connectable) {
        return !this.senders.contains(connectable);
    }

    @Override
    public Material getCostType() {
        return Material.REDSTONE;
    }
}
