package de.placeblock.redstoneutilities.blockentity;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Interaction;

import java.util.*;

@RequiredArgsConstructor
public class BlockEntityRegistry {

    private final RedstoneUtilities plugin;
    @Getter
    private final Map<UUID, BlockEntity<?, ?>> blockEntities = new HashMap<>();

    public void register(BlockEntity<?, ?> blockEntity) {
        this.blockEntities.put(blockEntity.getUuid(), blockEntity);
    }

    public boolean has(Interaction interaction) {
        return this.blockEntities.containsKey(interaction.getUniqueId());
    }

    public <B extends BlockEntity<B, BT>, BT extends BlockEntityType<B, BT>> B get(Interaction interaction, Class<B> bclazz) {
        BlockEntity<?, ?> blockEntity = this.get(interaction);
        if (blockEntity == null) return null;
        if (bclazz.isAssignableFrom(blockEntity.getClass())) {
            return bclazz.cast(blockEntity);
        } else {
            return null;
        }
    }

    public <B extends BlockEntity<B, BT>, BT extends BlockEntityType<B, BT>> List<B> get(Class<B> bclazz) {
        List<B> blockEntities = new ArrayList<>();
        for (BlockEntity<?, ?> blockEntity : this.blockEntities.values()) {
            if (bclazz.isAssignableFrom(blockEntity.getClass())) {
                blockEntities.add((B) blockEntity);
            }
        }
        return blockEntities;
    }

    public BlockEntity<?, ?> get(Interaction interaction) {
        if (!this.has(interaction)) {
            this.load(interaction);
        }
        return this.blockEntities.get(interaction.getUniqueId());
    }

    public void load(Interaction interaction) {
        this.plugin.getLogger().info("Loading unknown BlockEntity at Location: " + interaction.getLocation());
        BlockEntityType<?, ?> type = this.plugin.getBlockEntityTypeRegistry().getType(interaction);
        if (type == null) {
            this.plugin.getLogger().warning("BlockEntityType is null");
            return;
        }
        BlockEntity<?, ?> blockEntity = type.getBlockEntity(interaction.getUniqueId());
        this.blockEntities.put(interaction.getUniqueId(), blockEntity);
        blockEntity.load();
    }

    public void remove(Interaction interaction) {
        this.remove(interaction.getUniqueId());
    }

    public void remove(UUID uuid) {
        this.blockEntities.remove(uuid);
    }

}
