package de.placeblock.redstoneutilities.upgrades;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class UpgradeItems {
    public static final List<Material> levelMaterials = List.of(Material.COAL, Material.IRON_INGOT,
            Material.EMERALD, Material.GOLD_INGOT, Material.DIAMOND, Material.NETHERITE_INGOT);

    public static List<ItemStack> getItems(int amount, Function<Integer, ItemStack> itemGenerator) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            items.add(itemGenerator.apply(i));
        }
        return items;
    }

    public static ItemStack getEfficiencyItem(int level) {
        ItemStack item = Util.getItem(Material.SOUL_CAMPFIRE, Component.text("Effizienz Upgrade " + level));
        item.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 1);
        return item;
    }

    public static ItemStack getSpeedItem(int level) {
        ItemStack item = Util.getItem(Material.CLOCK, Component.text("Geschwindigkeits Upgrade " + level));
        item.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 1);
        return item;
    }

    public static void registerRecipes(Upgrade upgrade) {
        for (int level = 1; level <= upgrade.getMaxLevel(); level++) {
            Material material = levelMaterials.get(level-1);
            registerUpgradeRecipe(upgrade, level, material);
        }
    }

    private static void registerUpgradeRecipe(Upgrade upgrade, int level, Material levelMat) {
        NamespacedKey key = new NamespacedKey(RedstoneUtilities.getInstance(), upgrade.toString()+"_upgrade_"+(level-1));
        ItemStack item = upgrade.getItem(level).clone();
        item.setAmount(2);
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("LPL", "PCP", "LPL");
        recipe.setIngredient('P', Material.PAPER);
        recipe.setIngredient('L', levelMat);
        recipe.setIngredient('C', upgrade.getMaterial());
        Bukkit.addRecipe(recipe);
    }
}
