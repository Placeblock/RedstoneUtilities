package de.placeblock.redstoneutilities.impl.itemnetwork.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
@RequiredArgsConstructor
public class StorageChest {

    private final Location location;
    private final Inventory inventory;

    public List<ItemStack> getContents() {
        return Arrays.stream(this.inventory.getContents()).toList();
    }

    public int removeItem(ItemStack itemStack) {
        HashMap<Integer, ItemStack> leftItems = this.inventory.removeItem(itemStack);
        if (leftItems.size() == 0) return 0;
        return leftItems.get(0).getAmount();
    }

    public int addItem(ItemStack itemStack) {
        HashMap<Integer, ItemStack> notFit = this.inventory.addItem(itemStack);
        if (notFit.size() == 0) return 0;
        return notFit.get(0).getAmount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StorageChest that = (StorageChest) o;
        return Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }
}
