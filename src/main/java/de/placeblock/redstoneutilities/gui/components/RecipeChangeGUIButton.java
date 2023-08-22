package de.placeblock.redstoneutilities.gui.components;

import de.placeblock.betterinventories.content.item.ClickData;
import de.placeblock.betterinventories.content.item.GUIButton;
import de.placeblock.betterinventories.gui.GUI;
import de.placeblock.betterinventories.util.ItemBuilder;
import de.placeblock.redstoneutilities.BlockEntityManagerRegistry;
import de.placeblock.redstoneutilities.Recipeable;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.impl.autocrafting.AutoCraftingManager;
import de.placeblock.redstoneutilities.impl.autocrafting.RecipeChangeManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RecipeChangeGUIButton extends GUIButton {
    public static final ItemStack RECIPE_CHANGE_ITEM = new ItemBuilder(Util.primary("Rezept ändern"), Material.CRAFTING_TABLE).build();

    private final Recipeable recipeable;

    public RecipeChangeGUIButton(GUI gui, Recipeable recipeable) {
        super(gui, RECIPE_CHANGE_ITEM);
        this.recipeable = recipeable;
    }

    @Override
    public void onClick(ClickData data) {
        Player player = data.getPlayer();
        BlockEntityManagerRegistry managerRegistry = RedstoneUtilities.getInstance().getManagerRegistry();
        AutoCraftingManager autoCraftingManager = managerRegistry.get(AutoCraftingManager.AUTO_CRAFTING_NAME, AutoCraftingManager.class);
        RecipeChangeManager recipeChangeManager = autoCraftingManager.getRecipeChangeManager();
        recipeChangeManager.register(player, this.recipeable);
        player.openWorkbench(null, true);
    }
}
