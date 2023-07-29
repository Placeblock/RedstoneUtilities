package de.placeblock.redstoneutilities.autocrafting;

import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeUtil {

    public static int findBestSlot(Inventory inventory, Recipe recipe, ItemStack item) {
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            List<Integer> possibleSlots = getPossibleSlotsShaped(inventory, shapedRecipe, item);
            Integer bestSlot = getBestSlot(inventory, possibleSlots);
            if (bestSlot != null) return bestSlot;
        } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            if (!hasPossibleChoice(item, recipe)) return -1;
            List<RecipeChoice> missingChoicesShapeless = getMissingChoicesShapeless(inventory, shapelessRecipe);
            for (RecipeChoice choice : missingChoicesShapeless) {
                if (choice.test(item)) return getEmptySlot(inventory);
            }
            List<Integer> existingSlots = getExistingSlotsShapeless(inventory, item);
            Integer bestSlot = getBestSlot(inventory, existingSlots);
            if (bestSlot != null) return bestSlot;
        }
        return -1;
    }

    private static boolean hasPossibleChoice(ItemStack item, Recipe recipe) {
        for (RecipeChoice choice : ((ShapelessRecipe) recipe).getChoiceList()) {
            if (choice.test(item)) return true;
        }
        return false;
    }

    private static int getEmptySlot(Inventory inventory) {
        for (int i = 0; i < inventory.getContents().length; i++) {
            if (inventory.getContents()[i] == null) {
                return i;
            }
        }
        return -1;
    }

    private static List<RecipeChoice> getMissingChoicesShapeless(Inventory inventory, ShapelessRecipe recipe) {
        List<ItemStack> items = new ArrayList<>(Arrays.asList(inventory.getContents()));
        List<RecipeChoice> missingChoices = new ArrayList<>();
        for (RecipeChoice choice : recipe.getChoiceList()) {
            ItemStack chosenItem = null;
            for (ItemStack item : items) {
                if (item != null && choice.test(item)) {
                    chosenItem = item;
                }
            }
            if (chosenItem != null) {
                items.remove(chosenItem);
                continue;
            }
            missingChoices.add(choice);
        }
        return missingChoices;
    }

    private static Integer getBestSlot(Inventory inventory, List<Integer> possibleSlots) {
        if (possibleSlots.size() == 0) return null;
        int bestSlot = -1;
        int bestSlotAmount = Integer.MAX_VALUE;
        for (Integer possibleSlot : possibleSlots) {
            ItemStack invitem = inventory.getItem(possibleSlot);
            int slotAmount = invitem == null ? 0 : invitem.getAmount();
            if (slotAmount < bestSlotAmount) {
                bestSlot = possibleSlot;
                bestSlotAmount = slotAmount;
            }
        }
        return bestSlot;
    }

    private static List<Integer> getExistingSlotsShapeless(Inventory inventory, ItemStack item) {
        List<Integer> existingSlots = new ArrayList<>();
        for (int i = 0; i < inventory.getContents().length; i++) {
            ItemStack invitem = inventory.getContents()[i];
            if (!item.isSimilar(invitem) ||
                    (invitem != null && invitem.getAmount() >= item.getType().getMaxStackSize())) continue;
            existingSlots.add(i);
        }
        return existingSlots;
    }

    private static List<Integer> getPossibleSlotsShaped(Inventory inventory, ShapedRecipe recipe, ItemStack item) {
        List<Integer> possibleSlots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            RecipeChoice choice = getChoice(recipe, i);
            ItemStack invitem = inventory.getItem(i);
            if (choice == null) continue;
            if (((invitem == null) ||
                    (choice.test(invitem) && item.isSimilar(invitem) && invitem.getAmount() < item.getType().getMaxStackSize())) &&
                    choice.test(item)) {
                possibleSlots.add(i);
            }
        }
        return possibleSlots;
    }

    public static RecipeChoice getChoice(ShapedRecipe shapedRecipe, int index) {
        int rowi = index/3;
        if (rowi >= shapedRecipe.getShape().length) return null;
        String row = shapedRecipe.getShape()[rowi];
        if (row.length() <= index%3) return null;
        char character = row.charAt(index%3);
        return shapedRecipe.getChoiceMap().get(character);
    }

    public static boolean canAddItem(Inventory inventory, Recipe recipe) {
        for (ItemStack content : inventory.getContents()) {
            ItemStack result = recipe.getResult();
            if (content == null ||
                (content.isSimilar(result) &&
                content.getAmount() <= content.getMaxStackSize() - result.getAmount())) return true;
        }
        return false;
    }
}
