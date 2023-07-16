package de.placeblock.redstoneutilities.wireless;

import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.blockentity.BlockEntityRegistry;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import de.placeblock.redstoneutilities.pdc.PDCUUIDListUtil;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.wireless.receiver.ReceiverBlockEntity;
import de.placeblock.redstoneutilities.wireless.sender.SenderBlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WirelessPDCUtil {

    public static final NamespacedKey RECEIVERS_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "receivers");
    public static final NamespacedKey SENDERS_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "senders");
    public static final NamespacedKey NAME_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "name");
    public static final NamespacedKey MATERIAL_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "material");

    public static List<ReceiverBlockEntity> getReceivers(Interaction interaction) {
        List<UUID> uuids = PDCUUIDListUtil.getUUIDs(interaction, RECEIVERS_KEY);
        return getBlockEntities(uuids, ReceiverBlockEntity.class);
    }
    public static List<SenderBlockEntity> getSenders(Interaction interaction) {
        List<UUID> uuids = PDCUUIDListUtil.getUUIDs(interaction, SENDERS_KEY);
        return getBlockEntities(uuids, SenderBlockEntity.class);
    }

    public static <B extends BlockEntity<B, BT>, BT extends BlockEntityType<B, BT>> List<B> getBlockEntities(List<UUID> uuids, Class<B> blockEntityClass) {
        List<B> blockEntities = new ArrayList<>();
        BlockEntityRegistry blockEntityRegistry = RedstoneUtilities.getInstance().getBlockEntityRegistry();
        for (UUID uuid : uuids) {
            Entity entity = Bukkit.getEntity(uuid);
            if (!(entity instanceof Interaction interaction)) continue;
            B rbe = blockEntityRegistry.get(interaction, blockEntityClass);
            if (rbe == null) continue;
            blockEntities.add(rbe);
        }
        return blockEntities;
    }

    public static List<UUID> getNonBlockEntities(List<UUID> uuids) {
        List<UUID> nonBlockEntities = new ArrayList<>();
        BlockEntityRegistry blockEntityRegistry = RedstoneUtilities.getInstance().getBlockEntityRegistry();
        for (UUID uuid : uuids) {
            Entity entity = Bukkit.getEntity(uuid);
            if (entity instanceof Interaction interaction) {
                BlockEntity<?, ?> rbe = blockEntityRegistry.get(interaction);
                if (rbe != null) continue;
            }
            nonBlockEntities.add(uuid);
        }
        return nonBlockEntities;
    }

    public static void addReceiver(Interaction interaction, UUID receiver) {
        PDCUUIDListUtil.addUUID(interaction, receiver, RECEIVERS_KEY);
    }

    public static void addSender(Interaction interaction, UUID sender) {
        PDCUUIDListUtil.addUUID(interaction, sender, SENDERS_KEY);
    }

    public static void removeReceiver(Interaction interaction, UUID receiver) {
        PDCUUIDListUtil.removeUUID(interaction, receiver, RECEIVERS_KEY);
    }

    public static void removeSender(Interaction interaction, UUID sender) {
        PDCUUIDListUtil.removeUUID(interaction, sender, SENDERS_KEY);
    }

    public static void setSenders(Interaction interaction, List<UUID> senders) {
        PDCUUIDListUtil.setUUIDs(interaction, senders, SENDERS_KEY);
    }

    public static void setReceivers(Interaction interaction, List<UUID> receivers) {
        PDCUUIDListUtil.setUUIDs(interaction, receivers, RECEIVERS_KEY);
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
        if (mat == null) return;
        PersistentDataContainer pdc = interaction.getPersistentDataContainer();
        pdc.set(MATERIAL_KEY, PersistentDataType.STRING, mat.name());
    }

    public static Material getType(Interaction interaction) {
        PersistentDataContainer pdc = interaction.getPersistentDataContainer();
        String materialName = pdc.get(MATERIAL_KEY, PersistentDataType.STRING);
        return materialName == null ? Material.CALIBRATED_SCULK_SENSOR : Material.valueOf(materialName);
    }

}
