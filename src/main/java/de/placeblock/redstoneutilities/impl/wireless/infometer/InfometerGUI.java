package de.placeblock.redstoneutilities.impl.wireless.infometer;

import de.placeblock.betterinventories.builder.content.PaginatorBuilder;
import de.placeblock.betterinventories.content.item.ClickData;
import de.placeblock.betterinventories.content.item.GUIButton;
import de.placeblock.betterinventories.content.pane.impl.paginator.PaginatorGUIPane;
import de.placeblock.betterinventories.gui.GUI;
import de.placeblock.betterinventories.gui.impl.ChestGUI;
import de.placeblock.betterinventories.util.ItemBuilder;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.impl.wireless.receiver.ReceiverBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class InfometerGUI extends ChestGUI {
    private final ItemStack infometerItem;

    public InfometerGUI(ItemStack infometerItem) {
        super(RedstoneUtilities.getInstance(), Util.primary("Infometer"), 1, 6);
        this.infometerItem = infometerItem;

        PaginatorGUIPane paginator = new PaginatorBuilder(this)
                .adoptMinMax(this.getCanvas())
                .build();
        this.getCanvas().setSection(paginator);

        this.checkReceivers();
        List<ReceiverBlockEntity> receivers = InfometerPDCUtil.getReceivers(this.infometerItem);
        for (ReceiverBlockEntity receiver : receivers) {
            paginator.addItem(new ReceiverGUIButton(this, receiver));
        }
        this.update();
    }

    private void checkReceivers() {
        List<UUID> removedReceivers = InfometerPDCUtil.getRemovedReceivers(this.infometerItem);
        for (UUID removedReceiver : removedReceivers) {
            InfometerPDCUtil.removeReceiver(this.infometerItem, removedReceiver);
        }
    }

    private static class ReceiverGUIButton extends GUIButton {
        private final ReceiverBlockEntity receiverBlockEntity;

        public ReceiverGUIButton(GUI gui, ReceiverBlockEntity receiverBlockEntity) {
            super(gui, getItem(receiverBlockEntity));
            this.receiverBlockEntity = receiverBlockEntity;
        }

        private static ItemStack getItem(ReceiverBlockEntity receiverBlockEntity) {
            String name = receiverBlockEntity.getWirelessName();
            Material material = receiverBlockEntity.getWirelessType();
            Location location = receiverBlockEntity.getCenterLocation();
            if (material == null) material = Material.CALIBRATED_SCULK_SENSOR;
            return new ItemBuilder(Util.primary(name + " (Empfänger)"), material)
                .lore(List.of(
                        Util.getLore("Welt: " + location.getWorld().getName()),
                        Util.getLore("X: " + location.getBlockX()),
                        Util.getLore("Y: " + location.getBlockY()),
                        Util.getLore("Z: " + location.getBlockZ())
                )).build();
        }

        @Override
        public void onClick(ClickData data) {
            ReceiverGUI receiverGUI = new ReceiverGUI(this.receiverBlockEntity);
            receiverGUI.showPlayer(data.getPlayer());
        }
    }

}
