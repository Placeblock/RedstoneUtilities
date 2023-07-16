package de.placeblock.redstoneutilities.autocrafting;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.gui.GUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AutoCrafterGUI extends GUI {
    public static final ItemStack RECIPE_CHANGE_ITEM;

    static {
        RECIPE_CHANGE_ITEM = Util.getItem(Material.CRAFTING_TABLE, Component.text("Rezept ändern"));
    }

    private final AutoCrafterBlockEntity blockEntity;
    public AutoCrafterGUI(Player player, AutoCrafterBlockEntity blockEntity) {
        super(player);
        this.blockEntity = blockEntity;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,  InventoryType.HOPPER, Component.text("AutoCrafter"));
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getSlot() == 2) {
            AutoCrafting autoCrafting = RedstoneUtilities.getInstance().getAutoCrafting();
            RecipeChangeManager recipeChangeManager = autoCrafting.getRecipeChangeManager();
            recipeChangeManager.register(this.player, this.blockEntity);
            this.player.openWorkbench(null, true);
        }
    }

    @Override
    public void setup() {
        this.inv.setItem(2, RECIPE_CHANGE_ITEM);
    }
}
