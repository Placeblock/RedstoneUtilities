package de.placeblock.redstoneutilities.blockentity;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Interaction;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class BlockEntityRegistry {

    private final RedstoneUtilities plugin;
    @Getter
    private final Map<Interaction, BlockEntity<?, ?>> blockEntities = new HashMap<>();

    public void register(BlockEntity<?, ?> blockEntity) {
        this.blockEntities.put(blockEntity.getInteraction(), blockEntity);
    }

    public boolean has(Interaction interaction) {
        return this.blockEntities.containsKey(interaction);
    }

    public <B extends BlockEntity<B, BT>, BT extends BlockEntityType<B, BT>> B get(Interaction interaction, Class<B> bclazz) {
        return bclazz.cast(this.get(interaction));
    }

    public BlockEntity<?, ?> get(Interaction interaction) {
        if (!this.has(interaction)) {
            this.load(interaction);
        }
        return this.blockEntities.get(interaction);
    }

    public void load(Interaction interaction) {
        this.plugin.getLogger().info("Loading unknown BlockEntity at Location: " + interaction.getLocation());
        BlockEntityType<?, ?> type = this.plugin.getBlockEntityTypeRegistry().getType(interaction);
        if (type == null) {
            this.plugin.getLogger().warning("BlockEntityType is null");
            return;
        }
        BlockEntity<?, ?> blockEntity = type.getBlockEntity(interaction);
        this.blockEntities.put(interaction, blockEntity);
        blockEntity.load();
    }

    public void remove(Interaction interaction) {
        this.blockEntities.remove(interaction);
    }

}
