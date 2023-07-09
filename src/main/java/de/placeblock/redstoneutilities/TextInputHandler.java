package de.placeblock.redstoneutilities;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TextInputHandler implements Listener {

    private final Map<Player, Consumer<String>> players = new HashMap<>();

    @EventHandler
    public void on(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (this.players.containsKey(player)) {
            event.setCancelled(true);
            String text = ((TextComponent) event.message()).content();
            this.players.get(player).accept(text);
            this.removePlayer(player);
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        this.players.remove(event.getPlayer());
    }

    public void addPlayer(Player player, Consumer<String> callback) {
        this.players.put(player, callback);
        player.sendMessage(Messages.RENAME);
        Bukkit.getScheduler().runTaskLaterAsynchronously(RedstoneUtilities.getInstance(), () -> {
            if (this.removePlayer(player)) {
                player.sendMessage(Messages.RENAME_TIMEOUT);
            }
        }, 20*60);
    }

    public boolean removePlayer(Player player) {
        return this.players.remove(player) != null;
    }

}
