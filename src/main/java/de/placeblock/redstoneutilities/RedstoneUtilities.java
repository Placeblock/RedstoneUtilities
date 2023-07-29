package de.placeblock.redstoneutilities;
import de.placeblock.redstoneutilities.autocrafting.AutoCraftingManager;
import de.placeblock.redstoneutilities.blockentity.*;
import de.placeblock.redstoneutilities.chunkloader.ChunkLoaderManager;
import de.placeblock.redstoneutilities.filter.FilterManager;
import de.placeblock.redstoneutilities.upgrades.Upgrade;
import de.placeblock.redstoneutilities.upgrades.UpgradeItems;
import de.placeblock.redstoneutilities.wireless.WirelessManager;
import lombok.Getter;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class RedstoneUtilities extends JavaPlugin {

    public static final TextColor PRIMARY_COLOR = TextColor.color(131, 95, 250);
    public static final TextColor INFERIOR_COLOR = TextColor.color(153, 153, 153);

    @Getter
    private static RedstoneUtilities instance;

    private BlockEntityListener blockEntityListener;

    private BlockEntityTypeRegistry blockEntityTypeRegistry;
    private BlockEntityRegistry blockEntityRegistry;
    private TextInputHandler textInputHandler;

    private WirelessManager wirelessManager;
    private AutoCraftingManager autoCraftingManager;
    private FilterManager filterManager;
    private ChunkLoaderManager chunkLoaderManager;

    @Override
    public void onEnable() {
        RedstoneUtilities.instance = this;
        this.saveDefaultConfig();

        this.blockEntityListener = new BlockEntityListener(this);
        this.blockEntityTypeRegistry = new BlockEntityTypeRegistry();
        this.blockEntityRegistry = new BlockEntityRegistry(this);
        this.getLogger().info("Started BlockEntity(Type) Registry");

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(this.blockEntityListener, this);
        this.textInputHandler = new TextInputHandler();
        pluginManager.registerEvents(this.textInputHandler, this);
        this.getLogger().info("Started TextInputHandler");

        for (Upgrade upgrade : Upgrade.values()) {
            UpgradeItems.registerRecipes(upgrade);
        }
        this.getLogger().info("Registered Upgrade Recipes");

        if (this.shouldLoadBlockEntity("wireless")) {
            this.wirelessManager = new WirelessManager();
            this.wirelessManager.setup(this);
        }
        if (this.shouldLoadBlockEntity("autocrafting")) {
            this.autoCraftingManager = new AutoCraftingManager();
            this.autoCraftingManager.setup(this);
        }
        if (this.shouldLoadBlockEntity("filter")) {
            this.filterManager = new FilterManager();
            this.filterManager.setup(this);
        }
        if (this.shouldLoadBlockEntity("chunkloader")) {
            this.chunkLoaderManager = new ChunkLoaderManager();
            this.chunkLoaderManager.setup(this);
        }
        this.getLogger().info("Created BlockEntity Managers");
    }

    private void startAutoSave() {
        int interval = this.getConfig().getInt("blockentity-autosave-interval", -1);
        if (interval == -1) return;
        if (interval < 20) {
            this.getLogger().warning("Autosave interval was set to <20... stopping autosave");
            return;
        }
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            this.getLogger().info("Storing Data in Entities");
            for (BlockEntity<?, ?> blockEntity : this.blockEntityRegistry.getBlockEntities().values()) {
                blockEntity.store();
            }
        }, interval, interval);
        this.getLogger().info("Started BlockEntity Save-Scheduler");

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

        if (this.wirelessManager != null) {
            this.wirelessManager.disable();
        }
        if (this.autoCraftingManager != null) {
            this.autoCraftingManager.disable();
        }
        if (this.filterManager != null) {
            this.filterManager.disable();
        }
        if (this.chunkLoaderManager != null) {
            this.chunkLoaderManager.disable();
        }
        this.getLogger().info("Disabled BlockEntity Manager");
    }

    private boolean shouldLoadBlockEntity(String name) {
        FileConfiguration config = this.getConfig();
        ConfigurationSection blockentities = config.getConfigurationSection("blockentities");
        if (blockentities == null || !blockentities.contains(name)) return false;
        return blockentities.getBoolean(name);
    }

}