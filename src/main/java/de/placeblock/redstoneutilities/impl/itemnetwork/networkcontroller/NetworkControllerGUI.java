package de.placeblock.redstoneutilities.impl.itemnetwork.networkcontroller;

import de.placeblock.betterinventories.content.item.impl.SwitchGUIButton;
import de.placeblock.betterinventories.content.pane.impl.simple.SimpleGUIPane;
import de.placeblock.betterinventories.gui.impl.CanvasGUI;
import de.placeblock.betterinventories.util.ItemBuilder;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.upgrades.UpgradeGUIButton;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

public class NetworkControllerGUI extends CanvasGUI {
    public NetworkControllerGUI(NetworkControllerBlockEntity networkController) {
        super(RedstoneUtilities.getInstance(), Component.text("Network Controller")
                .color(RedstoneUtilities.PRIMARY_COLOR), InventoryType.HOPPER);

        SimpleGUIPane canvas = this.getCanvas();
        canvas.setSectionAt(2, new UpgradeGUIButton(this, networkController));
        canvas.setSectionAt(4, new SwitchGUIButton(this,
                new ItemBuilder(Util.primary("Interface"), Material.CHEST).build(),
                () -> new NetworkInterfaceGUI(networkController)));

        this.update();
    }
}
