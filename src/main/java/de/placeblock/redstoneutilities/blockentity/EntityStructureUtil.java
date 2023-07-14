package de.placeblock.redstoneutilities.blockentity;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.pdc.UUIDDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntityStructureUtil {
    private static final NamespacedKey ENTITIES_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "entities");
    private static final NamespacedKey UUID_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "uuid");

    public static void addEntity(Interaction blockEntity, Entity entity) {
        addEntity(blockEntity, entity.getUniqueId());
    }

    public static void addEntity(Interaction blockEntity, UUID uuid) {
        List<UUID> uuids = getEntities(blockEntity);
        if (uuids == null) uuids = new ArrayList<>();
        uuids.add(uuid);
        setEntities(blockEntity, uuids);
    }

    public static boolean removeEntity(Interaction blockEntity, Entity entity) {
        return removeEntity(blockEntity, entity.getUniqueId());
    }

    public static boolean removeEntity(Interaction blockEntity, UUID uuid) {
        List<UUID> uuids = getEntities(blockEntity);
        if (uuids == null) return false;
        uuids.remove(uuid);
        setEntities(blockEntity, uuids);
        return true;
    }

    public static List<UUID> getEntities(Interaction blockEntity) {
        PersistentDataContainer pdc = blockEntity.getPersistentDataContainer();
        if (!pdc.has(ENTITIES_KEY)) return null;
        PersistentDataContainer[] pdcs = pdc.get(ENTITIES_KEY, PersistentDataType.TAG_CONTAINER_ARRAY);
        List<UUID> uuids = new ArrayList<>();
        assert pdcs != null;
        for (PersistentDataContainer uuidPDC : pdcs) {
            uuids.add(getUUID(uuidPDC));
        }
        return uuids;
    }

    public static void setEntities(Interaction blockEntity, List<UUID> uuids) {
        PersistentDataContainer[] pdcs = new PersistentDataContainer[uuids.size()];
        for (int i = 0; i < uuids.size(); i++) {
            pdcs[i] = convertToPDC(blockEntity, uuids.get(i));
        }
        blockEntity.getPersistentDataContainer().set(ENTITIES_KEY, PersistentDataType.TAG_CONTAINER_ARRAY, pdcs);
    }

    private static PersistentDataContainer convertToPDC(Interaction blockEntity, UUID uuid) {
        PersistentDataAdapterContext adapterContext = blockEntity.getPersistentDataContainer().getAdapterContext();
        PersistentDataContainer pdc = adapterContext.newPersistentDataContainer();
        pdc.set(UUID_KEY, new UUIDDataType(), uuid);
        return pdc;
    }

    private static UUID getUUID(PersistentDataContainer pdc) {
        return pdc.get(UUID_KEY, new UUIDDataType());
    }
}
