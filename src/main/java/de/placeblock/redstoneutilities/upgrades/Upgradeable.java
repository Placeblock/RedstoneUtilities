package de.placeblock.redstoneutilities.upgrades;

import org.bukkit.entity.Interaction;

import java.util.List;

public interface Upgradeable {

    Interaction getInteraction();

    List<String> getUpgrades();

    default void storeUpgrades() {

    }

    default void loadUpgrades() {

    }

}
