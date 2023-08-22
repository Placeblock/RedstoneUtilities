package de.placeblock.redstoneutilities.impl.teleporter;

import de.placeblock.betterinventories.content.pane.impl.simple.SimpleGUIPane;
import de.placeblock.betterinventories.gui.impl.CanvasGUI;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.gui.components.RenameGUIButton;
import de.placeblock.redstoneutilities.upgrades.UpgradeGUIButton;
import org.bukkit.event.inventory.InventoryType;

public class TeleporterGUI extends CanvasGUI {

    private final TeleporterBlockEntity blockEntity;
    public TeleporterGUI(TeleporterBlockEntity blockEntity) {
        super(RedstoneUtilities.getInstance(), Util.primary("Teleporter"), InventoryType.HOPPER);
        this.blockEntity = blockEntity;

        SimpleGUIPane canvas = this.getCanvas();
        canvas.setSectionAt(1, new RenameGUIButton(this, (player, name) -> {
            this.blockEntity.setTeleporterName(name);
            for (TeleporterBlockEntity teleporter : this.blockEntity.getSelfTargetTeleporters()) {
                teleporter.removeTargetTeleporterEntity();
                teleporter.summonTargetTeleporterEntity();
            }
        }));
        canvas.setSectionAt(3, new UpgradeGUIButton(this, blockEntity));

        this.update();
    }
}
