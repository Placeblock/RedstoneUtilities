package de.placeblock.redstoneutilities.wireless.infometer;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.pdc.PDCUUIDListUtil;
import de.placeblock.redstoneutilities.wireless.WirelessPDCUtil;
import de.placeblock.redstoneutilities.wireless.receiver.ReceiverBlockEntity;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class InfometerPDCUtil {
    public static final NamespacedKey RECEIVERS_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "receivers");

    public static void addReceiver(ItemStack item, ReceiverBlockEntity receiver) {
        ItemMeta itemMeta = item.getItemMeta();
        PDCUUIDListUtil.addUUID(itemMeta, receiver.getUuid(), RECEIVERS_KEY);
        item.setItemMeta(itemMeta);
    }

    public static void removeReceiver(ItemStack item, UUID receiverUUID) {
        ItemMeta itemMeta = item.getItemMeta();
        PDCUUIDListUtil.removeUUID(itemMeta, receiverUUID, RECEIVERS_KEY);
        item.setItemMeta(itemMeta);
    }

    public static List<ReceiverBlockEntity> getReceivers(ItemStack item) {
        List<UUID> receiverUUIDs = PDCUUIDListUtil.getUUIDs(item.getItemMeta(), RECEIVERS_KEY);
        return WirelessPDCUtil.getBlockEntities(receiverUUIDs, ReceiverBlockEntity.class);
    }

    public static List<UUID> getRemovedReceivers(ItemStack item) {
        List<UUID> receiverUUIDs = PDCUUIDListUtil.getUUIDs(item.getItemMeta(), RECEIVERS_KEY);
        return WirelessPDCUtil.getNonBlockEntities(receiverUUIDs);
    }


}
