package de.placeblock.redstoneutilities.wireless.infometer;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.gui.GUI;
import de.placeblock.redstoneutilities.wireless.InteractionPDCUtil;
import de.placeblock.redstoneutilities.wireless.PDCLocationUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class InfometerListGUI extends GUI {
    public static final int ROWS = 6;
    public static final NamespacedKey LOCATION_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "location");
    public static final TextComponent TITLE = Component.text("Infometer")
            .color(RedstoneUtilities.PRIMARY_COLOR)
            .decoration(TextDecoration.ITALIC, false);
    public static final URL ARROW_RIGHT;
    public static final URL ARROW_LEFT;

    public static final ItemStack FILLER_ITEM;
    public static final ItemStack NEXT_ITEM;
    public static final ItemStack PREVIOUS_ITEM;


    static {
        ItemStack fillerItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerItemMeta = fillerItem.getItemMeta();
        fillerItemMeta.displayName(Component.empty());
        fillerItem.setItemMeta(fillerItemMeta);
        FILLER_ITEM = fillerItem;

        try {
            ARROW_LEFT = new URL("http://textures.minecraft.net/texture/d02cf0dea4d06ef1de255bb815f64268d68bb3b903d60621ae92e007e288198c");
            ARROW_RIGHT = new URL("http://textures.minecraft.net/texture/65da184546a05de96a4d1fb820f72c09f1597df56d88a4ab35e9b4a2c780ab84");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        Component displayName = Component.text("Nächste Seite")
                .color(RedstoneUtilities.PRIMARY_COLOR)
                .decoration(TextDecoration.ITALIC, false);
        NEXT_ITEM = Util.getHead(displayName, ARROW_RIGHT);
        PREVIOUS_ITEM = Util.getHead(displayName, ARROW_LEFT);
    }
    private final ItemStack item;
    private List<ItemStack> items;
    private boolean paginator;
    private int page = 0;

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 9*ROWS, TITLE);
    }

    public InfometerListGUI(Player player, ItemStack item) {
        super(player);
        this.item = item;
    }

    public void setup() {
        this.initializeItems(item);
        this.paginator = this.items.size() > 9*ROWS;
        this.setItems();
    }

    private void setItems() {
        this.inv.clear();
        if (this.paginator) {
            for (int i = 9*(ROWS-1); i < 9*(ROWS-1)+8; i++) {
                this.inv.setItem(i, FILLER_ITEM);
            }
            this.inv.setItem(9*ROWS-2, PREVIOUS_ITEM);
            this.inv.setItem(9*ROWS-1, NEXT_ITEM);
        }
        int itemSlots = getItemSlots();
        for (int i = 0; i < itemSlots && i+itemSlots*this.page < this.items.size(); i++) {
            this.inv.setItem(i, this.items.get(i+itemSlots*this.page));
        }
        this.update();
    }

    private void initializeItems(ItemStack item) {
        List<Location> receivers = ItemPDCUtil.getReceivers(item);
        List<ItemStack> items = new ArrayList<>();
        for (Location receiver : receivers) {
            Interaction receiverInteraction = Util.getInteraction(receiver);
            if (receiverInteraction == null) {
                ItemPDCUtil.removeReceiver(item, receiver);
                continue;
            }
            Material material = InteractionPDCUtil.getType(receiverInteraction);
            String name = InteractionPDCUtil.getName(receiverInteraction);
            items.add(this.getItem(name, material, receiver));
        }
        this.items = items;
    }

    private ItemStack getItem(String name, Material material, Location location) {
        Component title = Component.text(name + " (Empfänger)")
                .color(RedstoneUtilities.PRIMARY_COLOR)
                .decoration(TextDecoration.ITALIC, false);
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        PDCLocationUtil.setLocation(itemMeta, location, LOCATION_KEY);
        itemMeta.lore(List.of(
                Util.getLore("World: " + location.getWorld().getName()),
                Util.getLore("X: " + location.getBlockX()),
                Util.getLore("Y: " + location.getBlockY()),
                Util.getLore("Z: " + location.getBlockZ())
        ));
        itemMeta.displayName(title);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private void nextPage() {
        int pages = getPages();
        this.page = (this.page+1) % pages;
    }

    private void previousPage() {
        int pages = getPages();
        this.page = (this.page-1) % pages;
        if (this.page < 0) this.page += pages;
    }

    private int getPages() {
        return (int) Math.ceil((double) this.items.size() / this.getItemSlots());
    }

    private int getItemSlots() {
        return Math.min(this.paginator ? 9*(ROWS-1) : 9*ROWS, this.items.size());
    }


    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(this.inv)) return;
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (this.paginator) {
            if (item.isSimilar(NEXT_ITEM)) {
                this.nextPage();
                this.setItems();
                return;
            } else if (item.isSimilar(PREVIOUS_ITEM)) {
                this.previousPage();
                this.setItems();
                return;
            }
        }
        Location location = PDCLocationUtil.getLocation(item.getItemMeta(), LOCATION_KEY);
        Interaction interaction = Util.getInteraction(location);
        this.inv.close();
        InfometerGUI infometerGUI = new InfometerGUI(this.player, interaction);
        infometerGUI.setup();
        infometerGUI.register();
        infometerGUI.show();
    }

}
