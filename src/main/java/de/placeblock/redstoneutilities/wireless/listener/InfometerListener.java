package de.placeblock.redstoneutilities.wireless.listener;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.wireless.infometer.InfometerListGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class InfometerListener implements Listener {

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item != null && Items.isInfometer(item)) {
            InfometerListGUI infometerListGUI = new InfometerListGUI(player, item);
            infometerListGUI.setup();
            infometerListGUI.register();
            infometerListGUI.show();
        }
    }

}
