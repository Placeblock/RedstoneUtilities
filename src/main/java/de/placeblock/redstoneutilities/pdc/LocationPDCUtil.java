package de.placeblock.redstoneutilities.pdc;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class LocationPDCUtil {
    private static final NamespacedKey X_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "x");
    private static final NamespacedKey Y_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "y");
    private static final NamespacedKey Z_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "z");
    private static final NamespacedKey WORLD_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "world");

    public static Location getLocation(PersistentDataHolder pdh, NamespacedKey key) {
        PersistentDataContainer pdc = pdh.getPersistentDataContainer();
        PersistentDataContainer locationPDC = pdc.get(key, PersistentDataType.TAG_CONTAINER);
        if (locationPDC == null) return null;
        return getLocationFromPDC(locationPDC);
    }

    public static void setLocation(PersistentDataHolder pdh, NamespacedKey key, Location location) {
        PersistentDataContainer pdc = pdh.getPersistentDataContainer();
        PersistentDataContainer locationPDC = getPDCFromLocation(location, pdc);
        pdc.set(key, PersistentDataType.TAG_CONTAINER, locationPDC);
    }

    private static Location getLocationFromPDC(PersistentDataContainer pdc) {
        Double x = pdc.get(X_KEY, PersistentDataType.DOUBLE);
        Double y = pdc.get(Y_KEY, PersistentDataType.DOUBLE);
        Double z = pdc.get(Z_KEY, PersistentDataType.DOUBLE);
        PersistentDataContainer worldPDC = pdc.get(WORLD_KEY, PersistentDataType.TAG_CONTAINER);
        if (x == null || y == null || z == null || worldPDC == null) return null;
        UUID worldUUID = PDCUUIDListUtil.getUUID(worldPDC);
        if (worldUUID == null) return null;
        World world = Bukkit.getWorld(worldUUID);
        return new Location(world, x, y, z);
    }

    private static PersistentDataContainer getPDCFromLocation(Location location, PersistentDataContainer parentpdc) {
        PersistentDataContainer pdc = parentpdc.getAdapterContext().newPersistentDataContainer();
        pdc.set(X_KEY, PersistentDataType.DOUBLE, location.getX());
        pdc.set(Y_KEY, PersistentDataType.DOUBLE, location.getY());
        pdc.set(Z_KEY, PersistentDataType.DOUBLE, location.getZ());
        UUID uuid = location.getWorld().getUID();
        pdc.set(WORLD_KEY, PersistentDataType.TAG_CONTAINER, PDCUUIDListUtil.getUUIDPDC(uuid, pdc));
        return pdc;
    }

}
