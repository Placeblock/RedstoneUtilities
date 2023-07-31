package de.placeblock.redstoneutilities.blockentity;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class BlockEntityTypeRegistry {
    private static final NamespacedKey TYPE_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "RU_TYPE");

    @Getter
    private final Map<String, BlockEntityType<?, ?>> blockEntityTypes = new HashMap<>();

    public void register(BlockEntityType<?, ?> type) {
        this.blockEntityTypes.put(type.getName(), type);
    }

    public BlockEntityType<?, ?> getBlockEntityType(String name) {
        return this.blockEntityTypes.get(name);
    }

    public BlockEntityType<?, ?> getBlockEntityType(ItemStack item) {
        for (BlockEntityType<?, ?> blockEntityType : this.blockEntityTypes.values()) {
            if (blockEntityType.getItemStack().isSimilar(item)) {
                return blockEntityType;
            }
        }
        return null;
    }

    public BlockEntityType<?, ?> getType(Interaction interaction) {
        PersistentDataContainer pdc = interaction.getPersistentDataContainer();
        String typeName = pdc.get(TYPE_KEY, PersistentDataType.STRING);
        if (typeName == null) return null;
        return this.getBlockEntityType(typeName.toLowerCase());
    }

    public static void setType(Interaction interaction, BlockEntityType<?, ?> blockEntityType) {
        setType(interaction, blockEntityType.getName());
    }

    public static String getType(Entity entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        return pdc.get(TYPE_KEY, PersistentDataType.STRING);
    }

    public static void setType(Entity entity, String name) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        pdc.set(TYPE_KEY, PersistentDataType.STRING, name);
    }
}
