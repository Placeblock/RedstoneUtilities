package de.placeblock.redstoneutilities.wireless.infometer;

import de.placeblock.redstoneutilities.Messages;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.gui.GUI;
import de.placeblock.redstoneutilities.wireless.InteractionPDCUtil;
import de.placeblock.redstoneutilities.wireless.Wireless;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InfometerGUI extends GUI {
    public static final ItemStack RENAME_ITEM;
    public static final ItemStack ON_ITEM;
    public static final ItemStack OFF_ITEM;

    static {
        RENAME_ITEM = Util.getItem(Material.PAPER, Component.text("Umbenennen"));
        ON_ITEM = Util.getItem(Material.LIME_WOOL, Component.text("Aktuell: AN"));
        OFF_ITEM = Util.getItem(Material.RED_WOOL, Component.text("Aktuell: AUS"));
    }

    private final Interaction interaction;

    public InfometerGUI(Player player, Interaction interaction) {
        super(player);
        this.interaction = interaction;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 9);
    }

    @Override
    public void setup() {
        this.inv.setItem(4, this.getReceiverItem());
        this.inv.setItem(2, RENAME_ITEM);
        this.setToggleItem();
    }

    private ItemStack getReceiverItem() {
        String name = InteractionPDCUtil.getName(this.interaction);
        Material material = InteractionPDCUtil.getType(this.interaction);
        return Util.getItem(material, Component.text(name));
    }

    private void setToggleItem() {
        RedstoneWire redstoneWire = Util.getRedstone(this.interaction);
        if (redstoneWire == null) return;
        if (redstoneWire.getPower() > 0) {
            this.inv.setItem(6, ON_ITEM);
        } else {
            this.inv.setItem(6, OFF_ITEM);
        }
    }


    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(this.inv)) return;
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        Wireless wireless = RedstoneUtilities.getInstance().getWireless();
        if (item.isSimilar(ON_ITEM)) {
            wireless.setPower(this.interaction, 0);
            this.setToggleItem();
        } else if (item.isSimilar(OFF_ITEM)) {
            wireless.setPower(this.interaction, 15);
            this.setToggleItem();
        } else if (item.isSimilar(RENAME_ITEM)) {
            this.inv.close();
            wireless.getTextInputHandler().addPlayer(this.player, (text) -> {
                InteractionPDCUtil.setName(this.interaction, text);
                player.sendMessage(Messages.RENAMED);
            });
        }
    }
}
