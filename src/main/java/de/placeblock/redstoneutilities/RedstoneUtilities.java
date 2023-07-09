package de.placeblock.redstoneutilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntityTypeRegistry;
import de.placeblock.redstoneutilities.blockentity.BlockEntityListener;
import de.placeblock.redstoneutilities.blockentity.BlockEntityUtil;
import de.placeblock.redstoneutilities.blockentity.EntityStructureUtil;
import de.placeblock.redstoneutilities.wireless.Wireless;
import lombok.Getter;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class RedstoneUtilities extends JavaPlugin {

    public static final TextColor PRIMARY_COLOR = TextColor.color(131, 95, 250);
    public static final TextColor INFERIOR_COLOR = TextColor.color(153, 153, 153);

    @Getter
    private static RedstoneUtilities instance;

    private BlockEntityListener blockEntityListener;

    private BlockEntityUtil blockEntityUtil;
    private EntityStructureUtil entityStructureUtil;
    private BlockEntityTypeRegistry blockEntityTypeRegistry;

    private Wireless wireless;

    @Override
    public void onEnable() {
        RedstoneUtilities.instance = this;

        this.blockEntityListener = new BlockEntityListener();
        this.blockEntityUtil = new BlockEntityUtil();
        this.entityStructureUtil = new EntityStructureUtil();
        this.blockEntityTypeRegistry = new BlockEntityTypeRegistry();
        this.getServer().getPluginManager().registerEvents(this.blockEntityListener, this);

        this.wireless = new Wireless(this);
        this.wireless.setup();
    }

    @Override
    public void onDisable() {
        this.wireless.disable();
    }

}