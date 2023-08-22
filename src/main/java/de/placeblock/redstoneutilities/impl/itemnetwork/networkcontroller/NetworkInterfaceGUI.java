package de.placeblock.redstoneutilities.impl.itemnetwork.networkcontroller;

import de.placeblock.betterinventories.builder.content.PaginatorBuilder;
import de.placeblock.betterinventories.content.item.ClickData;
import de.placeblock.betterinventories.content.item.GUIButton;
import de.placeblock.betterinventories.content.pane.impl.paginator.PaginatorControlsPosition;
import de.placeblock.betterinventories.content.pane.impl.paginator.PaginatorGUIPane;
import de.placeblock.betterinventories.gui.GUI;
import de.placeblock.betterinventories.gui.impl.ChestGUI;
import de.placeblock.betterinventories.util.ItemBuilder;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class NetworkInterfaceGUI extends ChestGUI {
    private final PaginatorGUIPane paginator;
    private final NetworkControllerBlockEntity blockEntity;

    public NetworkInterfaceGUI(NetworkControllerBlockEntity networkController) {
        super(RedstoneUtilities.getInstance(), Component.text("Network Interface"), 1, 6);
        this.blockEntity = networkController;
        this.paginator = new PaginatorBuilder(this)
                .adoptMinMax(this.getCanvas())
                .defaultControls(PaginatorControlsPosition.RIGHT)
                .build();
        this.getCanvas().setSection(this.paginator);

        List<ItemStack> contents = networkController.getStorageController().getContents();
        this.updateContents(contents);
        this.blockEntity.getStorageController().addInterfaceGUI(this);
    }

    public void updateContents(List<ItemStack> contents) {
        this.paginator.clearItems();
        this.paginator.addItems(item -> new NetworkItemButton(this, item), contents);
        this.update();
    }

    @Override
    public void onClose(Player player) {
        this.blockEntity.getStorageController().removeInterfaceGUI(this);
    }

    public static class NetworkItemButton extends GUIButton {
        private final ItemStack initItem;

        public NetworkItemButton(GUI gui, ItemStack item) {
            super(gui, getItem(item));
            this.initItem = item;
        }

        private static ItemStack getItem(ItemStack item) {
            TextComponent displayName;
            if (item.getItemMeta().hasDisplayName()) {
                displayName = (TextComponent) item.getItemMeta().displayName();
            } else {
                displayName = Component.text(item.getType().name().replace('_', ' ').toLowerCase());
            }
            displayName = displayName.append(Component.text(" x" + item.getAmount())
                    .color(RedstoneUtilities.PRIMARY_COLOR))
                    .decoration(TextDecoration.ITALIC, false);
            return new ItemBuilder(displayName, item.getType(), Math.min(item.getAmount(), 64))
                    .build();
        }

        @Override
        public void onClick(ClickData data) {

        }
    }
}
