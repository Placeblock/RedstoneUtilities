package de.placeblock.redstoneutilities;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;

public class Util {

    public static Interaction getInteraction(Location location) {
        return getNearbyEntity(location, Interaction.class);
    }

    private static <T extends Entity> T getNearbyEntity(Location location, Class<T> entity) {
        Collection<T> nearbyEntities = getNearbyEntities(location, entity);
        for (T nearbyEntity : nearbyEntities) {
            return nearbyEntity;
        }
        return null;
    }

    private static <T extends Entity> Collection<T> getNearbyEntities(Location location, Class<T> entity) {
        return location.getNearbyEntitiesByType(entity, 0.3);
    }

    public static ItemStack getHead(Component displayName, URL url) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = ((SkullMeta) item.getItemMeta());
        PlayerProfile playerProfile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures playerTextures = playerProfile.getTextures();
        playerTextures.setSkin(url);
        playerProfile.setTextures(playerTextures);
        skullMeta.setPlayerProfile(playerProfile);
        skullMeta.displayName(displayName);
        item.setItemMeta(skullMeta);
        return item;
    }

    public static ItemStack getItem(Material material, Component displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta skullMeta = item.getItemMeta();
        skullMeta.displayName(displayName.color(RedstoneUtilities.PRIMARY_COLOR).decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(skullMeta);
        return item;
    }

    public static RedstoneWire getRedstone(Interaction interaction) {
        Location location = interaction.getLocation();
        Block block = location.getBlock();
        if (block.getType() != Material.REDSTONE_WIRE) return null;
        return (RedstoneWire) block.getBlockData();
    }

    public static Component getLore(String text) {
        return Component.text(text)
                .color(RedstoneUtilities.INFERIOR_COLOR)
                .decoration(TextDecoration.ITALIC, false);
    }
}
