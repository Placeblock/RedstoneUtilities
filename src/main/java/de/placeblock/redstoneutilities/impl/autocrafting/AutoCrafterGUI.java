package de.placeblock.redstoneutilities.impl.autocrafting;

import de.placeblock.redstoneutilities.BlockEntityManagerRegistry;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.gui.GUI;
import de.placeblock.redstoneutilities.upgrades.UpgradeGUI;
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
        if (event.getSlot() == 1) {
            BlockEntityManagerRegistry managerRegistry = RedstoneUtilities.getInstance().getManagerRegistry();
            AutoCraftingManager autoCraftingManager = managerRegistry.get(AutoCraftingManager.AUTO_CRAFTING_NAME, AutoCraftingManager.class);
            RecipeChangeManager recipeChangeManager = autoCraftingManager.getRecipeChangeManager();
            recipeChangeManager.register(this.player, this.blockEntity);
            this.player.openWorkbench(null, true);
        } else if (event.getSlot() == 3) {
            UpgradeGUI upgradeGUI = new UpgradeGUI(this.player, this.blockEntity);
            upgradeGUI.setup();
            upgradeGUI.register();
            upgradeGUI.show();
        }
    }

    @Override
    public void setup() {
        this.inv.setItem(1, RECIPE_CHANGE_ITEM);
        this.inv.setItem(3, UpgradeGUI.UPGRADE_ITEM);
    }
}
