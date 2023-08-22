package de.placeblock.redstoneutilities.impl.autocrafting;

import de.placeblock.betterinventories.content.pane.impl.simple.SimpleGUIPane;
import de.placeblock.betterinventories.gui.impl.CanvasGUI;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.gui.components.RecipeChangeGUIButton;
import de.placeblock.redstoneutilities.upgrades.UpgradeGUIButton;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryType;

public class AutoCrafterGUI extends CanvasGUI {
    public AutoCrafterGUI(AutoCrafterBlockEntity blockEntity) {
        super(RedstoneUtilities.getInstance(),
                Component.text("AutoCrafter").color(RedstoneUtilities.PRIMARY_COLOR),
                InventoryType.HOPPER);
        SimpleGUIPane canvas = this.getCanvas();
        canvas.setSectionAt(1, new RecipeChangeGUIButton(this, blockEntity));
        canvas.setSectionAt(3, new UpgradeGUIButton(this, blockEntity));
        this.update();
    }
}
