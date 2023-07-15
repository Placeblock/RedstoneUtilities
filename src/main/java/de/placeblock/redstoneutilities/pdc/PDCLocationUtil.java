package de.placeblock.redstoneutilities.pdc;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.pdc.UUIDDataType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Interaction;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PDCLocationUtil {
    public static final NamespacedKey WORLD_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "world");
    public static final NamespacedKey X_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "x");
    public static final NamespacedKey Y_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "y");
    public static final NamespacedKey Z_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "z");

    public static void addLocation(PersistentDataHolder holder, Location location, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();

        PersistentDataContainer[] persistentDataContainers = pdc.get(key, PersistentDataType.TAG_CONTAINER_ARRAY);

        List<PersistentDataContainer> locationsPDC;
        if (persistentDataContainers == null) {
            locationsPDC = new ArrayList<>();
        } else {
            locationsPDC = new ArrayList<>(Arrays.asList(persistentDataContainers));
        }

        PersistentDataContainer locationPDC = getLocationPDC(location, pdc);
        locationsPDC.add(locationPDC);

        setLocations(holder, locationsPDC.toArray(new PersistentDataContainer[]{}), key);
    }

    public static void removeLocation(PersistentDataHolder holder, Location location, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();

        PersistentDataContainer[] persistentDataContainers = pdc.get(key, PersistentDataType.TAG_CONTAINER_ARRAY);
        assert persistentDataContainers != null;

        List<PersistentDataContainer> receiversPdc = new ArrayList<>(Arrays.asList(persistentDataContainers));

        receiversPdc.removeIf((receiverPdc) -> {
            Location receiverLoc = getLocation(receiverPdc);
            return location.toBlockLocation().equals(receiverLoc);
        });
        setLocations(holder, receiversPdc.toArray(new PersistentDataContainer[]{}), key);
    }

    public static List<Location> getLocations(PersistentDataHolder holder, NamespacedKey key) {
        List<Location> locations = new ArrayList<>();

        PersistentDataContainer pdc = holder.getPersistentDataContainer();

        if (!pdc.has(key)) {
            return locations;
        }

        PersistentDataContainer[] locationsPDC = pdc.get(key, PersistentDataType.TAG_CONTAINER_ARRAY);

        assert locationsPDC != null;
        for (PersistentDataContainer locationPDC : locationsPDC) {
            Location location = getLocation(locationPDC);
            if (location == null) continue;
            locations.add(location);
        }

        return locations;
    }

    public static Location getLocation(PersistentDataHolder holder, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        PersistentDataContainer locationPDC = pdc.get(key, PersistentDataType.TAG_CONTAINER);
        if (locationPDC == null) return null;
        return getLocation(locationPDC);
    }

    private static Location getLocation(PersistentDataContainer receiverPdc) {
        UUID worldUUID = receiverPdc.get(WORLD_KEY, new UUIDDataType());
        Integer x = receiverPdc.get(X_KEY, PersistentDataType.INTEGER);
        Integer y = receiverPdc.get(Y_KEY, PersistentDataType.INTEGER);
        Integer z = receiverPdc.get(Z_KEY, PersistentDataType.INTEGER);
        if (worldUUID == null || x == null || y == null || z == null) {
            RedstoneUtilities.getInstance().getLogger().warning("Tried to access receiver in sender with insuffiecient data.");
            return null;
        }
        World world = Bukkit.getWorld(worldUUID);
        return new Location(world, x, y, z);
    }

    public static void setLocation(PersistentDataHolder holder, Location location, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.TAG_CONTAINER, getLocationPDC(location, pdc));
    }

    public static void setLocations(PersistentDataHolder holder, List<Location> locations, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        PersistentDataContainer[] locationsPDC = new PersistentDataContainer[locations.size()];
        for (int i = 0; i < locationsPDC.length; i++) {
            locationsPDC[i] = getLocationPDC(locations.get(i), pdc);
        }
        setLocations(holder, locationsPDC, key);
    }

    private static void setLocations(PersistentDataHolder holder, PersistentDataContainer[] locationsPDC, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.TAG_CONTAINER_ARRAY, locationsPDC);
    }
    @NotNull
    private static PersistentDataContainer getLocationPDC(Location location, PersistentDataContainer pdc) {
        PersistentDataContainer receiverPdc = pdc.getAdapterContext().newPersistentDataContainer();
        receiverPdc.set(WORLD_KEY, new UUIDDataType(), location.getWorld().getUID());
        receiverPdc.set(X_KEY, PersistentDataType.INTEGER, location.getBlockX());
        receiverPdc.set(Y_KEY, PersistentDataType.INTEGER, location.getBlockY());
        receiverPdc.set(Z_KEY, PersistentDataType.INTEGER, location.getBlockZ());
        return receiverPdc;
    }
}
