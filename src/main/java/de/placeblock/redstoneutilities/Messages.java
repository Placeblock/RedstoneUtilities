package de.placeblock.redstoneutilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;

import java.util.List;

public class Messages {

    public static final Component PLACE_RECEIVER = Component.text("Mit einem ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("Redstone Verbinder").color(RedstoneUtilities.PRIMARY_COLOR))
            .append(Component.text(" kannst du ").color(RedstoneUtilities.INFERIOR_COLOR))
            .append(Component.text("Sender und Empfänger verbinden").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component CONNECT_CANCEL = Component.text("[Shift] + [Rechtsklick Luft]").color(RedstoneUtilities.PRIMARY_COLOR)
            .append(Component.text(" = ").color(RedstoneUtilities.INFERIOR_COLOR))
            .append(Component.text("Abbrechen").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component CONNECTED = Component.text("Du hast den ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("Sender mit dem Empfänger verbunden ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component DISCONNECTED = Component.text("Du hast den ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("Sender von dem Empfänger getrennt ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component NOT_ENOUGH_REDSTONE = Component.text("Du hast ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("nicht genug Redstone! ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component REDSTONE_RECEIVED = Component.text("Dir wurde das ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("bezahlte Redstone zurückerstattet! ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component ONLY_RECEIVER_INFOMETER = Component.text("Du kannst ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("nur Empfänger hinzufügen! ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component REMOVED_FROM_INFOMETER = Component.text("Empfänger ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("aus Infometer entfernt! ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component ADDED_TO_INFOMETER = Component.text("Empfänger ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("zu Infometer hinzugefügt! ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component RENAME_TIMEOUT = Component.text("Du kannst nun wieder ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text(" Nachrichten schreiben! ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component RENAME = Component.text("Bitte ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("gebe einen neuen Namen ein! ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component RENAMED = Component.text("Du hast ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("den Empfänger umbenannt! ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static Component getInfometerReceiver(List<Location> senders, String name) {
        return Component.text("Empfänger (" + name + ")")
                .color(RedstoneUtilities.PRIMARY_COLOR)
                .decorate(TextDecoration.BOLD)
                .append(Component.newline())
                .append(Component.text("Registrierte Sender:")
                        .decoration(TextDecoration.BOLD, false)
                        .color(RedstoneUtilities.PRIMARY_COLOR))
                .append(Component.newline())
                .append(formatLocations(senders));
    }
    public static Component getInfometerSender(List<Location> receivers) {
        return Component.text("Sender")
                .color(RedstoneUtilities.PRIMARY_COLOR)
                .decorate(TextDecoration.BOLD)
                .append(Component.newline())
                .append(Component.text("Registrierte Empfänger:")
                        .decoration(TextDecoration.BOLD, false)
                        .color(RedstoneUtilities.PRIMARY_COLOR))
                .append(Component.newline())
                .append(formatLocations(receivers));
    }

    private static Component formatLocations(List<Location> locations) {
        Component component = Component.empty();
        for (Location location : locations) {
            String worldName = location.getWorld().getName();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            String content = "Welt: " + worldName + " X: " + x + " Y: " + y + " Z: " + z;
            component = component
                    .append(Component.text(content)
                        .color(RedstoneUtilities.INFERIOR_COLOR))
                        .decoration(TextDecoration.BOLD, false)
                    .append(Component.newline());
        }
        return component;
    }

}
