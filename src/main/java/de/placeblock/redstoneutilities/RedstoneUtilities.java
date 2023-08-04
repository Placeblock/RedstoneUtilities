package de.placeblock.redstoneutilities;
import de.placeblock.redstoneutilities.blockentity.*;
import de.placeblock.redstoneutilities.command.BlockEntityStructureCommand;
import de.placeblock.redstoneutilities.command.NearbyEntityUUIDCommand;
import de.placeblock.redstoneutilities.connector.ConnectorHandler;
import de.placeblock.redstoneutilities.connector.ConnectorRecipe;
import de.placeblock.redstoneutilities.impl.autocrafting.AutoCraftingManager;
import de.placeblock.redstoneutilities.impl.chunkloader.ChunkLoaderManager;
import de.placeblock.redstoneutilities.impl.filter.FilterManager;
import de.placeblock.redstoneutilities.impl.itemnetwork.ItemNetworkManager;
import de.placeblock.redstoneutilities.impl.teleporter.TeleporterManager;
import de.placeblock.redstoneutilities.impl.wireless.WirelessManager;
import de.placeblock.redstoneutilities.upgrades.Upgrade;
import de.placeblock.redstoneutilities.upgrades.UpgradeItems;
import lombok.Getter;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.function.Supplier;

@Getter
public class RedstoneUtilities extends JavaPlugin {

    public static final TextColor PRIMARY_COLOR = TextColor.color(131, 95, 250);
    public static final TextColor INFERIOR_COLOR = TextColor.color(153, 153, 153);

    @Getter
    private static RedstoneUtilities instance;

    private BlockEntityListener blockEntityListener;

    private BlockEntityTypeRegistry blockEntityTypeRegistry;
    private BlockEntityRegistry blockEntityRegistry;
    private ConnectorHandler connectorHandler;
    private TextInputHandler textInputHandler;

    private BlockEntityManagerRegistry managerRegistry;

    @Override
    public void onEnable() {
        RedstoneUtilities.instance = this;
        this.saveDefaultConfig();

        this.createRegistries();
        this.createManagers();
        this.registerListeners();
        this.registerRecipes();
        this.registerCommands();
    }

    private void createManagers() {
        this.checkAndCreateManager(AutoCraftingManager.AUTO_CRAFTING_NAME, AutoCraftingManager::new);
        this.checkAndCreateManager(ChunkLoaderManager.CHUNK_LOADER_NAME, ChunkLoaderManager::new);
        this.checkAndCreateManager(FilterManager.FILTER_NAME, FilterManager::new);
        this.checkAndCreateManager(TeleporterManager.TELEPORTER_NAME, TeleporterManager::new);
        this.checkAndCreateManager(WirelessManager.WIRELESS_NAME, WirelessManager::new);
        this.checkAndCreateManager(ItemNetworkManager.ITEM_NETWORK_NAME, ItemNetworkManager::new);
    }

    private void checkAndCreateManager(String name, Supplier<BlockEntityManager> constructor) {
        if (this.shouldLoadManager(name)) {
            BlockEntityManager blockEntityManager = constructor.get();
            this.managerRegistry.register(name, blockEntityManager);
            blockEntityManager.setup(this);
            this.getLogger().info("Registered " + name + " Manager");
        }
    }

    private void createRegistries() {
        this.blockEntityTypeRegistry = new BlockEntityTypeRegistry();
        this.blockEntityRegistry = new BlockEntityRegistry(this);
        this.getLogger().info("Started BlockEntity(Type) Registry");

        this.managerRegistry = new BlockEntityManagerRegistry();
        this.getLogger().info("Started Manager Registry");
    }

    private void registerRecipes() {
        new ConnectorRecipe().register();

        for (Upgrade upgrade : Upgrade.values()) {
            UpgradeItems.registerRecipes(upgrade);
        }
        this.getLogger().info("Registered Recipes");
    }

    private void registerListeners() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        this.blockEntityListener = new BlockEntityListener(this);
        pluginManager.registerEvents(this.blockEntityListener, this);
        this.textInputHandler = new TextInputHandler();
        pluginManager.registerEvents(this.textInputHandler, this);
        this.connectorHandler = new ConnectorHandler();
        this.connectorHandler.start(this);
        pluginManager.registerEvents(this.connectorHandler, this);
        this.getLogger().info("Registered Listeners");
    }

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("bes")).setExecutor(new BlockEntityStructureCommand());
        Objects.requireNonNull(this.getCommand("euuid")).setExecutor(new NearbyEntityUUIDCommand());
        this.getLogger().info("Registered Commands");
    }

    @Override
    public void onDisable() {
        for (BlockEntityType<?, ?> blockEntityType : this.blockEntityTypeRegistry.getBlockEntityTypes().values()) {
            blockEntityType.disable();
        }
        this.getLogger().info("Disabled BlockEntity Types");

        for (BlockEntity<?, ?> blockEntity : this.blockEntityRegistry.getBlockEntities().values()) {
            blockEntity.disable();
            blockEntity.store();
        }
        this.getLogger().info("Disabled BlockEntities");

        HandlerList.unregisterAll(this.textInputHandler);
        HandlerList.unregisterAll(this.blockEntityListener);
        this.getLogger().info("Stopped TextInputHandler");

        for (BlockEntityManager manager : this.managerRegistry.getManagers().values()) {
            manager.disable();
        }
        this.managerRegistry.removeAll();
        this.getLogger().info("Disabled BlockEntity Manager");
    }

    private boolean shouldLoadManager(String name) {
        FileConfiguration config = this.getConfig();
        ConfigurationSection blockentities = config.getConfigurationSection("blockentities");
        if (blockentities == null || !blockentities.contains(name)) return false;
        return blockentities.getBoolean(name);
    }

}