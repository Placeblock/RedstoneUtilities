package de.placeblock.redstoneutilities.blockentity;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.pdc.UUIDDataType;
import org.bukkit.Bukkit;
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

    public static List<UUID> getEntityUUIDs(Interaction blockEntity) {
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

    public static List<Entity> getEntities(Interaction blockEntity, String type) {
        List<UUID> entityUUIDs = getEntityUUIDs(blockEntity);
        if (entityUUIDs == null) return null;
        List<Entity> typeEntities = new ArrayList<>();
        for (UUID entityUUID : entityUUIDs) {
            Entity entity = Bukkit.getEntity(entityUUID);
            if (entity == null) continue;
            if (type.equals(BlockEntityTypeRegistry.getType(entity))) {
                typeEntities.add(entity);
            }
        }
        return typeEntities;
    }

    public static List<UUID> getEntityUUIDs(Interaction blockEntity, String type) {
        List<Entity> entities = getEntities(blockEntity, type);
        if (entities == null) return null;
        List<UUID> uuids = new ArrayList<>();
        for (Entity entity : entities) {
            uuids.add(entity.getUniqueId());
        }
        return uuids;
    }

    public static UUID getEntityUUID(Interaction blockEntity, String type) {
        List<Entity> entities = getEntities(blockEntity, type);
        if (entities == null || entities.isEmpty()) return null;
        return entities.get(0).getUniqueId();
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
