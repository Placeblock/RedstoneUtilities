package de.placeblock.redstoneutilities.wireless.sender;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.Messages;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import de.placeblock.redstoneutilities.wireless.ConnectorHandler;
import de.placeblock.redstoneutilities.wireless.Wireless;
import de.placeblock.redstoneutilities.wireless.WirelessBlockEntity;
import de.placeblock.redstoneutilities.wireless.receiver.ReceiverBlockEntity;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SenderBlockEntity extends WirelessBlockEntity {
    private final List<ReceiverBlockEntity> receivers;
    public SenderBlockEntity(BlockEntityType<?> type,
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
