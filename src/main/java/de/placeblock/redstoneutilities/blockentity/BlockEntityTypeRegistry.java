package de.placeblock.redstoneutilities.blockentity;

import lombok.Getter;
import org.bukkit.entity.Interaction;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BlockEntityTypeRegistry {
    @Getter
    private final Map<String, BlockEntityType<?>> blockEntityTypes = new HashMap<>();

    public void register(BlockEntityType<?> type) {
        this.blockEntityTypes.put(type.getName(), type);
    }

    public BlockEntityType<?> getBlockEntityType(String name) {
        return this.blockEntityTypes.get(name);
    }

    public BlockEntityType<?> getBlockEntityType(ItemStack item) {
        for (BlockEntityType<?> blockEntityType : this.blockEntityTypes.values()) {
            if (blockEntityType.getItemStack().isSimilar(item)) {
                return blockEntityType;
            }
        }
        return null;
    }


    public <T extends BlockEntity> T getBlockEntity(String name, Interaction interaction, Class<T> blockEntityClass) {
        BlockEntity blockEntity = this.getBlockEntity(name, interaction);
        return blockEntityClass.cast(blockEntity);
    }
    public BlockEntity getBlockEntity(String name, Interaction interaction) {
        BlockEntityType<?> blockEntityType = this.blockEntityTypes.get(name);
        if (blockEntityType == null) return null;
        return blockEntityType.loadBlockEntity(interaction);
    }
}
