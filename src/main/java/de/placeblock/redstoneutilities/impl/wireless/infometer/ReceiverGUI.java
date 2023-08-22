package de.placeblock.redstoneutilities.impl.wireless.infometer;

import de.placeblock.betterinventories.content.item.GUIItem;
import de.placeblock.betterinventories.content.pane.impl.simple.SimpleGUIPane;
import de.placeblock.betterinventories.gui.impl.CanvasGUI;
import de.placeblock.betterinventories.util.ItemBuilder;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.gui.components.RenameGUIButton;
import de.placeblock.redstoneutilities.impl.wireless.receiver.ReceiverBlockEntity;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class ReceiverGUI extends CanvasGUI {

    private static ItemStack getReceiverItem(ReceiverBlockEntity receiver) {
        String name = receiver.getWirelessName();
        Material material = receiver.getWirelessType();
        if (material == null) material = Material.CALIBRATED_SCULK_SENSOR;
        return new ItemBuilder(Util.primary(name), material).build();
    }

    private final ReceiverBlockEntity receiver;

    public ReceiverGUI(ReceiverBlockEntity receiver) {
        super(RedstoneUtilities.getInstance(), Util.primary("Infometer"), InventoryType.HOPPER);
        this.receiver = receiver;

        SimpleGUIPane canvas = this.getCanvas();
        canvas.setSectionAt(0, new RenameGUIButton(this, (player, name) -> this.receiver.setWirelessName(name)));
        canvas.setSectionAt(2, new GUIItem(this, getReceiverItem(this.receiver)));
        canvas.setSectionAt(4, new TogglePowerGUIButton(this, this.receiver));
        this.update();
    }
}
