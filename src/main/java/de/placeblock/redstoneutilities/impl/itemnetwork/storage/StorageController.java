package de.placeblock.redstoneutilities.impl.itemnetwork.storage;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StorageController {

    @Setter
    @Getter
    private Set<StorageChest> storageChests = new HashSet<>();

    public List<ItemStack> getContents() {
        List<ItemStack> contents = new ArrayList<>();
        for (StorageChest storageChest : new ArrayList<>(this.storageChests)) {
            if (!this.check(storageChest)) continue;
            for (ItemStack itemStack : storageChest.getContents()) {
                if (itemStack == null) continue;
                ItemStack existing = getExisting(contents, itemStack);
                if (existing != null) {
                    existing.setAmount(existing.getAmount() + itemStack.getAmount());
                } else {
                    contents.add(itemStack);
                }
            }
        }
        return contents;
    }

    private boolean check(StorageChest storageChest) {
        if (storageChest.getLocation().getBlock().getType() == Material.TRAPPED_CHEST) {
            return true;
        } else {
            this.removeChest(storageChest);
            return false;
        }
    }

    public void removeChest(StorageChest storageChest) {
        this.storageChests.remove(storageChest);
    }

    public void addChest(StorageChest storageChest) {
        this.storageChests.add(storageChest);
    }

    public int removeItem(ItemStack item) {
        for (StorageChest storageChest : this.storageChests) {
            int left = storageChest.removeItem(item);
            if (left == 0) break;
            item.setAmount(left);
        }
        return item.getAmount();
    }

    public void addItem(ItemStack item) {
        for (StorageChest storageChest : this.storageChests) {
            int didntFit = storageChest.addItem(item);
            if (didntFit == 0) break;
            item.setAmount(didntFit);
        }
    }

    public static ItemStack getExisting(List<ItemStack> list, ItemStack item) {
        for (ItemStack itemStack : list) {
            if (itemStack.isSimilar(item)) {
                return itemStack;
            }
        }
        return null;
    }
}
