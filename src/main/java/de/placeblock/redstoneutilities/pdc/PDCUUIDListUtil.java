package de.placeblock.redstoneutilities.pdc;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PDCUUIDListUtil {
    private static final NamespacedKey UUID_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "uuid");

    public static void addUUID(PersistentDataHolder holder, UUID uuid, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();

        PersistentDataContainer[] persistentDataContainers = pdc.get(key, PersistentDataType.TAG_CONTAINER_ARRAY);

        List<PersistentDataContainer> uuidsPDC;
        if (persistentDataContainers == null) {
            uuidsPDC = new ArrayList<>();
        } else {
            uuidsPDC = new ArrayList<>(Arrays.asList(persistentDataContainers));
        }

        PersistentDataContainer locationPDC = getUUIDPDC(uuid, pdc);
        uuidsPDC.add(locationPDC);

        setUUIDs(holder, uuidsPDC.toArray(new PersistentDataContainer[]{}), key);
    }

    public static void removeUUID(PersistentDataHolder holder, UUID uuid, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();

        PersistentDataContainer[] persistentDataContainers = pdc.get(key, PersistentDataType.TAG_CONTAINER_ARRAY);
        assert persistentDataContainers != null;

        List<PersistentDataContainer> pdcList = new ArrayList<>(Arrays.asList(persistentDataContainers));

        pdcList.removeIf((receiverPdc) -> {
            UUID uuid2 = getUUID(receiverPdc);
            return uuid.equals(uuid2);
        });
        setUUIDs(holder, pdcList.toArray(new PersistentDataContainer[]{}), key);
    }

    public static List<UUID> getUUIDs(PersistentDataHolder holder, NamespacedKey key) {
        List<UUID> uuids = new ArrayList<>();

        PersistentDataContainer pdc = holder.getPersistentDataContainer();

        if (!pdc.has(key)) {
            return uuids;
        }

        PersistentDataContainer[] uuidsPDC = pdc.get(key, PersistentDataType.TAG_CONTAINER_ARRAY);

        assert uuidsPDC != null;
        for (PersistentDataContainer locationPDC : uuidsPDC) {
            UUID uuid = getUUID(locationPDC);
            if (uuid == null) continue;
            uuids.add(uuid);
        }

        return uuids;
    }

    public static UUID getUUID(PersistentDataHolder holder, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        PersistentDataContainer uuidPDC = pdc.get(key, PersistentDataType.TAG_CONTAINER);
        if (uuidPDC == null) return null;
        return getUUID(uuidPDC);
    }

    public static UUID getUUID(PersistentDataContainer receiverPdc) {
        UUID uuid = receiverPdc.get(UUID_KEY, new UUIDDataType());
        if (uuid == null) {
            RedstoneUtilities.getInstance().getLogger().warning("Tried to access UUID in PDC with insuffiecient data.");
            return null;
        }
        return uuid;
    }

    public static void setUUID(PersistentDataHolder holder, UUID uuid, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.TAG_CONTAINER, getUUIDPDC(uuid, pdc));
    }

    public static void setUUIDs(PersistentDataHolder holder, List<UUID> uuids, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        PersistentDataContainer[] uuidsPDC = new PersistentDataContainer[uuids.size()];
        for (int i = 0; i < uuidsPDC.length; i++) {
            uuidsPDC[i] = getUUIDPDC(uuids.get(i), pdc);
        }
        setUUIDs(holder, uuidsPDC, key);
    }

    private static void setUUIDs(PersistentDataHolder holder, PersistentDataContainer[] locationsPDC, NamespacedKey key) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.TAG_CONTAINER_ARRAY, locationsPDC);
    }

    public static PersistentDataContainer getUUIDPDC(UUID uuid, PersistentDataContainer pdc) {
        PersistentDataContainer receiverPdc = pdc.getAdapterContext().newPersistentDataContainer();
        receiverPdc.set(UUID_KEY, new UUIDDataType(), uuid);
        return receiverPdc;
    }
}
