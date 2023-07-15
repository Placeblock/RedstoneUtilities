package de.placeblock.redstoneutilities.wireless.infometer;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.pdc.PDCLocationUtil;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class InfometerPDCUtil {
    public static final NamespacedKey RECEIVERS_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "receivers");

    public static void addReceiver(ItemStack item, Location location) {
        ItemMeta itemMeta = item.getItemMeta();
        PDCLocationUtil.addLocation(itemMeta, location, RECEIVERS_KEY);
        item.setItemMeta(itemMeta);
    }

    public static void removeReceiver(ItemStack item, Location location) {
        ItemMeta itemMeta = item.getItemMeta();
        PDCLocationUtil.removeLocation(itemMeta, location, RECEIVERS_KEY);
        item.setItemMeta(itemMeta);
    }

    public static List<Location> getReceivers(ItemStack item) {
        return PDCLocationUtil.getLocations(item.getItemMeta(), RECEIVERS_KEY);
    }


}
