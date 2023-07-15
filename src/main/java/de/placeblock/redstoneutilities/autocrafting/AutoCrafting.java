package de.placeblock.redstoneutilities.autocrafting;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.Bukkit;

public class AutoCrafting {

    public void setup(RedstoneUtilities plugin) {
        new AutoCrafterRecipe().register();
    }

}
