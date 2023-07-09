package de.placeblock.redstoneutilities.wireless;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Interaction;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import java.util.List;

public class WirelessPDCUtil {

    public static final NamespacedKey RECEIVERS_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "receivers");
    public static final NamespacedKey SENDERS_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "senders");
    public static final NamespacedKey NAME_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "name");
    public static final NamespacedKey MATERIAL_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "material");

    public static List<Location> getReceivers(Interaction interaction) {
        return PDCLocationUtil.getLocations(interaction, RECEIVERS_KEY);
    }
    public static List<Location> getSenders(Interaction interaction) {
        return PDCLocationUtil.getLocations(interaction, SENDERS_KEY);
    }

    public static void addReceiver(Interaction interaction, Location location) {
        PDCLocationUtil.addLocation(interaction, location, RECEIVERS_KEY);
    }

    public static void addSender(Interaction interaction, Location location) {
        PDCLocationUtil.addLocation(interaction, location, SENDERS_KEY);
    }

    public static void removeReceiver(Interaction interaction, Location location) {
        PDCLocationUtil.removeLocation(interaction, location, RECEIVERS_KEY);
    }

    public static void removeSender(Interaction interaction, Location location) {
        PDCLocationUtil.removeLocation(interaction, location, SENDERS_KEY);
    }

    public static void setName(Interaction interaction, String name) {
        PersistentDataContainer pdc = interaction.getPersistentDataContainer();
        pdc.set(NAME_KEY, PersistentDataType.STRING, name);
    }

    public static String getName(Interaction interaction) {
        PersistentDataContainer pdc = interaction.getPersistentDataContainer();
        String name = pdc.get(NAME_KEY, PersistentDataType.STRING);
        return name == null ? "Unbenannt" : name;
    }

    public static void setType(Interaction interaction, Material mat) {
        PersistentDataContainer pdc = interaction.getPersistentDataContainer();
        pdc.set(MATERIAL_KEY, PersistentDataType.STRING, mat.name());
    }

    public static Material getType(Interaction interaction) {
        PersistentDataContainer pdc = interaction.getPersistentDataContainer();
        String materialName = pdc.get(MATERIAL_KEY, PersistentDataType.STRING);
        return materialName == null ? Material.CALIBRATED_SCULK_SENSOR : Material.valueOf(materialName);
    }

}
