package de.placeblock.redstoneutilities.blockentity;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Interaction;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class BlockEntityRegistry {

    private final RedstoneUtilities plugin;
    @Getter
    private final Map<Location, BlockEntity<?, ?>> blockEntities = new HashMap<>();

    public void register(BlockEntity<?, ?> blockEntity) {
        this.blockEntities.put(blockEntity.getBlockLocation(), blockEntity);
    }

    public boolean has(Location location) {
        return this.blockEntities.containsKey(location);
    }

    public <B extends BlockEntity<B, BT>, BT extends BlockEntityType<B, BT>> B get(Location location, Class<B> bclazz) {
        return bclazz.cast(this.get(location.toBlockLocation()));
    }

    public BlockEntity<?, ?> get(Location location) {
        location = location.toBlockLocation();
        if (!this.has(location)) {
            this.load(location);
        }
        return this.blockEntities.get(location);
    }

    public void load(Location location) {
        this.plugin.getLogger().info("Loading unknown BlockEntity at Location: " + location);
        Interaction interaction = Util.getInteraction(location);
        if (interaction == null) {
            this.plugin.getLogger().warning("Interaction is not existing");
            return;
        }
        BlockEntityType<?, ?> type = this.plugin.getBlockEntityTypeRegistry().getType(interaction);
        if (type == null) {
            this.plugin.getLogger().warning("BlockEntityType is null");
            return;
        }
        BlockEntity<?, ?> blockEntity = type.getBlockEntity(interaction);
        this.blockEntities.put(location, blockEntity);
        blockEntity.load();
    }

    public void remove(Location location) {
        this.blockEntities.remove(location.toBlockLocation());
    }

}
