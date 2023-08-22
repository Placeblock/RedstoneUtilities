package de.placeblock.redstoneutilities.impl.itemnetwork.networkcontroller;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import de.placeblock.redstoneutilities.impl.itemnetwork.storage.StorageChest;
import de.placeblock.redstoneutilities.impl.itemnetwork.storage.StorageController;
import de.placeblock.redstoneutilities.upgrades.Upgrade;
import de.placeblock.redstoneutilities.upgrades.Upgradeable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Interaction;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class NetworkControllerBlockEntity extends BlockEntity<NetworkControllerBlockEntity, NetworkControllerBlockEntityType> implements Upgradeable {
    @Getter
    @Setter
    Map<Upgrade, Integer> upgrades = new HashMap<>();
    @Getter
    private StorageController storageController;
    private BukkitTask particleTask;

    public NetworkControllerBlockEntity(BlockEntityType<NetworkControllerBlockEntity, NetworkControllerBlockEntityType> type, UUID uuid, Location location) {
        super(type, uuid, location);
    }

    @Override
    public void onInteract(PlayerInteractAtEntityEvent event) {
        NetworkControllerGUI networkControllerGUI = new NetworkControllerGUI(this);
        networkControllerGUI.showPlayer(event.getPlayer());
    }

    @Override
    public void summon(Location location) {
        Location interactionLoc = location.toCenterLocation().add(0, -0.3, 0);
        World world = location.getWorld();
        this.uuid = world.spawn(interactionLoc, Interaction.class, i -> {
            i.setInteractionWidth(0.55F);
            i.setInteractionHeight(0.55F);
        }).getUniqueId();
    }

    private Set<StorageChest> getNewStorageChests() {
        Set<StorageChest> storageChests = new HashSet<>();
        int upgradeLevel = this.getUpgradeLevel(Upgrade.EFFICIENCY, 0);
        double maxDistance = Math.pow(upgradeLevel*3+3, 2);
        for (Chunk chestChunk : this.getChestChunks()) {
            for (BlockState tileEntity : chestChunk.getTileEntities()) {
                if (!(tileEntity instanceof Chest chest)) continue;
                Location location = chest.getLocation();
                if (location.distanceSquared(this.location) > maxDistance) continue;
                storageChests.add(new StorageChest(this.getStorageController(), chest.getLocation(), chest.getInventory()));
            }
        }
        return storageChests;
    }

    private List<Chunk> getChestChunks() {
        return Util.getChunks(this.location.getChunk(), 2);
    }

    @Override
    public void beforeUpgrade(Upgrade upgrade, Integer level) {

    }

    @Override
    public void afterUpgrade(Upgrade upgrade, Integer level) {
        this.onChange();
        this.updateStorageChests();
    }

    public void updateStorageChests() {
        Set<StorageChest> newStorageChests = this.getNewStorageChests();
        this.storageController.setStorageChests(newStorageChests);
        System.out.println("UPDATE CHESTS");
    }

    @Override
    public void load() {
        super.load();
        this.loadUpgrades();
        this.storageController = new StorageController();
        this.updateStorageChests();
        this.startParticleTask(Color.YELLOW, 0);
        this.startParticleTask(Color.BLUE, 5);
    }

    private void startParticleTask(Color color, long delay) {
        World world = this.location.getWorld();
        this.particleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(RedstoneUtilities.getInstance(), () -> {
            for (StorageChest storageChest : this.storageController.getStorageChests()) {
                Location chestLocation = storageChest.getLocation();
                Util.particleLine(chestLocation.toCenterLocation(), this.getCenterLocation(), loc ->
                        world.spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 0, new Particle.DustOptions(color, 1F)));
            }
        }, delay, 10);
    }

    @Override
    public void store() {
        super.store();
        this.storeUpgrades();
    }

    @Override
    public void disable() {
        this.particleTask.cancel();
        super.disable();
    }

    @Override
    public List<Upgrade> getAllowedUpgrades() {
        return List.of(Upgrade.EFFICIENCY);
    }
}
