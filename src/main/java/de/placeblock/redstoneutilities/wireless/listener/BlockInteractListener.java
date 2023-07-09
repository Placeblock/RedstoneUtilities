package de.placeblock.redstoneutilities.wireless.listener;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.wireless.WirelessPDCUtil;
import de.placeblock.redstoneutilities.wireless.Wireless;
import de.placeblock.redstoneutilities.wireless.infometer.InfometerListGUI;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BlockInteractListener implements Listener {

    private void handleType(Player player, Interaction interaction) {
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
