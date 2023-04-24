package de.wattestaebchen.dyingrabbit99;

import de.wattestaebchen.dyingrabbit99.files.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.Objects;

public class Events implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			event.getPlayer().sendMessage(Component.text(Objects.requireNonNull(Config.getOnPlayerJoinMessage())));
		}
		catch(NullPointerException ignored) {}
	}
	
}
