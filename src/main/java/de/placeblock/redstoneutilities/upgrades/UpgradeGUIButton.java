package de.placeblock.redstoneutilities.upgrades;

import de.placeblock.betterinventories.content.item.impl.SwitchGUIButton;
import de.placeblock.betterinventories.gui.GUI;
import de.placeblock.betterinventories.util.ItemBuilder;
import de.placeblock.redstoneutilities.Util;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class UpgradeGUIButton extends SwitchGUIButton {
    public static final ItemStack UPGRADE_ITEM = new ItemBuilder(Util.primary("Upgrades"), Material.NETHERITE_INGOT).build();
    public UpgradeGUIButton(GUI gui, Upgradeable upgradeable) {
        super(gui, UPGRADE_ITEM, player -> new UpgradeGUI(upgradeable, player));
    }
}
