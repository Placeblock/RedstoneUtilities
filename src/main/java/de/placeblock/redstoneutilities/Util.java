package de.placeblock.redstoneutilities;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

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

    public static void removeEntities(List<UUID> uuids) {
        for (UUID uuid : uuids) {
            Entity entity = Bukkit.getEntity(uuid);
            if (entity != null) {
                entity.remove();
            }
        }
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
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.displayName(displayName.color(RedstoneUtilities.PRIMARY_COLOR).decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(itemMeta);
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

    public static void rotateVectors(Location centerLocation, int count, Vector vector, BiConsumer<Location, Float> callback) {
        for (int i = 0; i < count; i+=1) {
            float angle = (float) (i * ((Math.PI * 2) / count));
            Vector rotationVec = vector.clone().rotateAroundY(angle);
            Location location = centerLocation.clone().add(rotationVec);
            callback.accept(location, angle);
        }
    }

    public static void summonCircleItemDisplays(Location centerLocation, int count, Vector vector, ItemStack itemStack, float size, BiConsumer<ItemDisplay, Float> callback) {
        World world = centerLocation.getWorld();
        rotateVectors(centerLocation, count, vector, (location, rotation) ->
            world.spawn(location, ItemDisplay.class, id -> {
                id.setItemStack(itemStack);
                id.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f((float) (Math.PI/2+rotation), 0F, 1F, 0F), new Vector3f(size, size, size), new AxisAngle4f()));
                callback.accept(id, rotation);
            })
        );
    }

    public static List<Chunk> getChunks(Chunk centerChunk, int reach) {
        List<Chunk> chunks = new ArrayList<>();
        World world = centerChunk.getWorld();
        int centerChunkX = centerChunk.getX();
        int centerChunkZ = centerChunk.getZ();
        for (int x = -reach; x <= reach; x++) {
            for (int z = -reach; z <= reach; z++) {
                Chunk chunk = world.getChunkAt(centerChunkX+x, centerChunkZ+z);
                chunks.add(chunk);
            }
        }
        return chunks;
    }
}
