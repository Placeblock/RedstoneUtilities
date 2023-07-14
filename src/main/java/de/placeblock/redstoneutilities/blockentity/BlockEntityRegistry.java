package de.placeblock.redstoneutilities.blockentity;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class BlockEntityRegistry {

    private final Map<Location, BlockEntity<?, ?>> blockEntities = new HashMap<>();

    public void register(BlockEntity<?, ?> blockEntity) {
        this.blockEntities.put(blockEntity.getBlockLocation(), blockEntity);
    }

    public boolean has(Location location) {
        return this.blockEntities.containsKey(location);
    }

    public <B extends BlockEntity<B, BT>, BT extends BlockEntityType<B>> B get(Location location, Class<B> bclazz) {
        return bclazz.cast(this.blockEntities.get(location.toBlockLocation()));
    }

    public BlockEntity<?, ?> get(Location location) {
        return this.get(location, BlockEntity.class);
    }

    public void remove(Location location) {
        this.blockEntities.remove(location.toBlockLocation());
    }

}
