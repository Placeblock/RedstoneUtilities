package de.placeblock.redstoneutilities;

import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.impl.wireless.receiver.ReceiverBlockEntity;
import de.placeblock.redstoneutilities.impl.wireless.sender.SenderBlockEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;

import java.util.List;

public class Messages {
    public static final Component CONNECT_CANCEL = Component.text("[Shift] + [Rechtsklick Luft]").color(RedstoneUtilities.PRIMARY_COLOR)
            .append(Component.text(" = ").color(RedstoneUtilities.INFERIOR_COLOR))
            .append(Component.text("Abbrechen").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component CONNECT_CANCELLED = Component.text("Du hast das Verbinden ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("abgebrochen").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component CANNOT_CONNECT_SELF = Component.text("Du kannst diesen Block ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("nicht mit sich selbst verbinden").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component TELEPORTER_TOO_FAR_AWAY = Component.text("Die Teleporter sind ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("zu weit auseinander. ").color(RedstoneUtilities.PRIMARY_COLOR))
            .append(Component.text("Effizienz Upgrades können die Distanz erweitern!").color(RedstoneUtilities.INFERIOR_COLOR));

    public static final Component CONNECTED = Component.text("Du hast die ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("Verbindung aufgebaut").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component DISCONNECTED = Component.text("Du hast die ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("Verbindung getrennt ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component NOT_ENOUGH_RESSOURCES = Component.text("Du hast ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("nicht genug Ressourcen! ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component RESSOURCES_RECEIVED = Component.text("Dir wurden die ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("bezahlten Kosten zurückerstattet! ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component REMOVED_FROM_INFOMETER = Component.text("Empfänger ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("aus Infometer entfernt! ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component ADDED_TO_INFOMETER = Component.text("Empfänger ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("zu Infometer hinzugefügt! ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component RENAME_TIMEOUT = Component.text("Du kannst nun wieder ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text(" Nachrichten schreiben! ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component RENAME = Component.text("Bitte ").color(RedstoneUtilities.INFERIOR_COLOR)
            .append(Component.text("gebe einen neuen Namen ein! ").color(RedstoneUtilities.PRIMARY_COLOR));

    public static final Component RENAMED = Util.primary("Erfolgreich umbenannt!");

    public static Component getInfometerReceiver(List<SenderBlockEntity> senders, String name) {
        return Component.text("Empfänger (" + name + ")")
                .color(RedstoneUtilities.PRIMARY_COLOR)
                .decorate(TextDecoration.BOLD)
                .append(Component.newline())
                .append(Component.text("Registrierte Sender:")
                        .decoration(TextDecoration.BOLD, false)
                        .color(RedstoneUtilities.PRIMARY_COLOR))
                .append(Component.newline())
                .append(formatLocations(senders.stream().map(BlockEntity::getCenterLocation).toList()));
    }
    public static Component getInfometerSender(List<ReceiverBlockEntity> receivers) {
        return Component.text("Sender")
                .color(RedstoneUtilities.PRIMARY_COLOR)
                .decorate(TextDecoration.BOLD)
                .append(Component.newline())
                .append(Component.text("Registrierte Empfänger:")
                        .decoration(TextDecoration.BOLD, false)
                        .color(RedstoneUtilities.PRIMARY_COLOR))
                .append(Component.newline())
                .append(formatLocations(receivers.stream().map(BlockEntity::getCenterLocation).toList()));
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
