package de.placeblock.redstoneutilities.upgrades;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Upgrade {
    EFFICIENCY("Effizienz", Material.SOUL_CAMPFIRE, UpgradeItems.getItems(6, UpgradeItems::getEfficiencyItem)),
    SPEED("Geschwindigkeit", Material.CLOCK, UpgradeItems.getItems(6, UpgradeItems::getSpeedItem));

    private final String name;
    private final Material material;
    private final List<ItemStack> items;

    public int getMaxLevel() {
        return this.items.size();
    }

    public ItemStack getItem(int level) {
        return this.items.get(level);
    }

    public static Upgrade getUpgrade(Material material) {
        for (Upgrade upgrade : Upgrade.values()) {
            if (upgrade.getMaterial() == material) {
                return upgrade;
            }
        }
        return null;
    }
}
