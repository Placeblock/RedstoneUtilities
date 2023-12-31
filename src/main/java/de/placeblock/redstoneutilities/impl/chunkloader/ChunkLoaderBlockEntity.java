package de.placeblock.redstoneutilities.impl.chunkloader;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import de.placeblock.redstoneutilities.upgrades.Upgrade;
import de.placeblock.redstoneutilities.upgrades.UpgradeGUI;
import de.placeblock.redstoneutilities.upgrades.Upgradeable;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.*;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;

public class ChunkLoaderBlockEntity extends BlockEntity<ChunkLoaderBlockEntity, ChunkLoaderBlockEntityType> implements Upgradeable {
    public static final Vector CRYSTAL_VEC = new Vector(0.41, 0, 0);
    public static final ItemStack CRYSTAL_STACK = new ItemStack(Material.END_CRYSTAL);
    @Getter
    @Setter
    Map<Upgrade, Integer> upgrades = new HashMap<>();


    public ChunkLoaderBlockEntity(BlockEntityType<ChunkLoaderBlockEntity, ChunkLoaderBlockEntityType> type, UUID uuid, Location location) {
        super(type, uuid, location);
    }

    @Override
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        UpgradeGUI upgradeGUI = new UpgradeGUI(this, player);
        upgradeGUI.showPlayer(player);
    }

    public Chunk getCenterChunk() {
        return this.getBlockLocation().getChunk();
    }

    public List<Chunk> getChunks() {
        int reach = this.getUpgradeLevel(Upgrade.EFFICIENCY, 0)/3;
        return Util.getChunks(this.getCenterChunk(), reach);
    }

    public void setForceLoad() {
        for (Chunk chunk : this.getChunks()) {
            chunk.addPluginChunkTicket(RedstoneUtilities.getInstance());
        }
    }

    public void removeForceLoad() {
        for (Chunk chunk : this.getChunks()) {
            chunk.removePluginChunkTicket(RedstoneUtilities.getInstance());
        }
    }

    public void tickChunks() {
        List<Chunk> chunks = this.getChunks();
        Chunk firstChunk = chunks.get(0);
        Integer randomTickSpeed = firstChunk.getWorld().getGameRuleValue(GameRule.RANDOM_TICK_SPEED);
        ServerLevel serverLevel = ((CraftWorld) firstChunk.getWorld()).getHandle();
        if (randomTickSpeed == null) return;
        for (Chunk chunk : chunks) {
            LevelChunk levelChunk = serverLevel.getChunk(chunk.getX(), chunk.getZ());
            serverLevel.tickChunk(levelChunk, randomTickSpeed);
        }
    }

    @Override
    public void summon(Location location) {
        Location blockDisplayLoc = location.toBlockLocation().add(0.1, 0, 0.1);
        Location chunkLoaderCenterLoc = location.toCenterLocation().add(0, -0.1, 0);
        Location interactionLoc = chunkLoaderCenterLoc.clone().add(0, -0.405, 0);
        World world = location.getWorld();
        this.uuid = world.spawn(interactionLoc, Interaction.class, i -> {
            i.setInteractionWidth(0.81F);
            i.setInteractionHeight(0.81F);
        }).getUniqueId();
        world.spawn(blockDisplayLoc, BlockDisplay.class, bd -> {
            RespawnAnchor respawnAnchor = (RespawnAnchor) Material.RESPAWN_ANCHOR.createBlockData();
            respawnAnchor.setCharges(respawnAnchor.getMaximumCharges());
            bd.setBlock(respawnAnchor);
            bd.setBrightness(new Display.Brightness(15, 15));
            bd.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(0.8F, 0.8F, 0.8F), new AxisAngle4f()));
            this.entityStructure.add(bd.getUniqueId());
        });
        Util.summonCircleItemDisplays(chunkLoaderCenterLoc, 4, CRYSTAL_VEC, CRYSTAL_STACK, 0.5F, (id, rotation) ->
                this.entityStructure.add(id.getUniqueId()));
    }

    @Override
    public void store() {
        super.store();
        this.storeUpgrades();
    }

    @Override
    public void load() {
        super.load();
        this.loadUpgrades();
        this.setForceLoad();
    }

    @Override
    public void disable() {
        this.removeForceLoad();
    }

    @Override
    public void remove(Player player, boolean drop) {
        this.dropUpgradeItems();
        super.remove(player, drop);
    }

    @Override
    public void beforeUpgrade(Upgrade upgrade, Integer level) {
        this.removeForceLoad();
    }

    @Override
    public void afterUpgrade(Upgrade upgrade, Integer level) {
        this.setForceLoad();
        this.onChange();
    }

    @Override
    public List<Upgrade> getAllowedUpgrades() {
        return List.of(Upgrade.EFFICIENCY);
    }
}
