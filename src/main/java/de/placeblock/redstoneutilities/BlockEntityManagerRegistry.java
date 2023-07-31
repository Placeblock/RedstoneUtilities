package de.placeblock.redstoneutilities;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class BlockEntityManagerRegistry {
    @Getter
    private final Map<String, BlockEntityManager> managers = new HashMap<>();

    public <M extends BlockEntityManager> M get(String string, Class<M> clazz) {
        BlockEntityManager blockEntityManager = this.get(string);
        if (!(clazz.isAssignableFrom(blockEntityManager.getClass()))) return null;
        return (M) blockEntityManager;
    }

    public BlockEntityManager get(String string) {
        return this.managers.get(string);
    }

    public void register(String string, BlockEntityManager blockEntityManager) {
        this.managers.put(string, blockEntityManager);
    }

    public void removeAll() {
        this.managers.clear();
    }

}
