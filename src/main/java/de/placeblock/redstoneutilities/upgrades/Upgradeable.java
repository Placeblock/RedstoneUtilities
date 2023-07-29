package de.placeblock.redstoneutilities.upgrades;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Interaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public interface Upgradeable {
    NamespacedKey UPGRADES_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "upgrades");
    NamespacedKey UPGRADE_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "upgrade");
    NamespacedKey LEVEL_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "level");

    Interaction getInteraction();

    Map<Upgrade, Integer> getUpgrades();

    void setUpgrades(Map<Upgrade, Integer> upgrades);

    void beforeUpgrade(Upgrade upgrade, Integer level);

    void afterUpgrade(Upgrade upgrade, Integer level);

    List<Upgrade> getAllowedUpgrades();

    default void setUpgrade(Upgrade upgrade, Integer level) {
        Integer beforeLevel = this.getUpgradeLevel(upgrade, 0);
        this.beforeUpgrade(upgrade, beforeLevel);
        this.getUpgrades().put(upgrade, level);
        this.afterUpgrade(upgrade, level);
    }

    default void upgrade(Upgrade upgrade) {
        int currentLevel = this.getUpgradeLevel(upgrade, 0);
        if (currentLevel == upgrade.getMaxLevel()) return;
        this.setUpgrade(upgrade, ++currentLevel);
    }

    default void storeUpgrades() {
        Interaction interaction = this.getInteraction();
        PersistentDataContainer pdc = interaction.getPersistentDataContainer();
        PersistentDataContainer[] upgradesPDC = new PersistentDataContainer[this.getUpgrades().size()];
        int i = 0;
        for (Map.Entry<Upgrade, Integer> upgradeEntry : this.getUpgrades().entrySet()) {
            PersistentDataContainer upgradePDC = pdc.getAdapterContext().newPersistentDataContainer();
            storeUpgradeData(upgradeEntry, upgradePDC);
            upgradesPDC[i] = upgradePDC;
            i++;
        }
        pdc.set(UPGRADES_KEY, PersistentDataType.TAG_CONTAINER_ARRAY, upgradesPDC);
    }

    private void storeUpgradeData(Map.Entry<Upgrade, Integer> upgradeEntry, PersistentDataContainer upgradePDC) {
        Upgrade upgrade = upgradeEntry.getKey();
        Integer level = upgradeEntry.getValue();
        upgradePDC.set(UPGRADE_KEY, PersistentDataType.STRING, upgrade.name());
        upgradePDC.set(LEVEL_KEY, PersistentDataType.INTEGER, level);
    }

    default void loadUpgrades() {
        Map<Upgrade, Integer> upgrades = new HashMap<>();
        Interaction interaction = this.getInteraction();
        PersistentDataContainer pdc = interaction.getPersistentDataContainer();
        PersistentDataContainer[] upgradesPDC = pdc.get(UPGRADES_KEY, PersistentDataType.TAG_CONTAINER_ARRAY);
        if (upgradesPDC != null) {
            for (PersistentDataContainer upgradePDC : upgradesPDC) {
                String upgradeName = upgradePDC.get(UPGRADE_KEY, PersistentDataType.STRING);
                Integer level = upgradePDC.get(LEVEL_KEY, PersistentDataType.INTEGER);
                Upgrade upgrade = Upgrade.valueOf(upgradeName);
                upgrades.put(upgrade, level);
            }
        }
        this.setUpgrades(upgrades);
    }

    default Integer getUpgradeLevel(Upgrade upgrade) {
        return this.getUpgrades().get(upgrade);
    }

    default Integer getUpgradeLevel(Upgrade upgrade, int fallback) {
        Integer upgradeLevel = this.getUpgradeLevel(upgrade);
        return Objects.requireNonNullElse(upgradeLevel, fallback);
    }

    default boolean hasReachedMaxLevel(Upgrade upgrade) {
        return this.getUpgradeLevel(upgrade, 0) >= upgrade.getMaxLevel();
    }

    default ItemStack getNextLevelItem(Upgrade upgrade) {
        return upgrade.getItem(this.getUpgradeLevel(upgrade, 0)+1);
    }


    default void dropUpgradeItems() {
        Interaction interaction = this.getInteraction();
        World world = interaction.getWorld();
        Map<Upgrade, Integer> upgrades = this.getUpgrades();
        for (Map.Entry<Upgrade, Integer> upgradeEntry : upgrades.entrySet()) {
            Upgrade upgrade = upgradeEntry.getKey();
            Integer level = upgradeEntry.getValue();
            for (int i = level; i > 0; i--) {
                ItemStack item = upgrade.getItem(level);
                world.dropItem(interaction.getLocation(), item);
            }
        }
    }
}
