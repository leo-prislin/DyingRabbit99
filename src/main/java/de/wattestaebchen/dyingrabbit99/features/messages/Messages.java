package de.wattestaebchen.dyingrabbit99.features.messages;

import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import de.wattestaebchen.dyingrabbit99.features.config.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class Messages implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			event.getPlayer().sendMessage(Component.text(Objects.requireNonNull(Config.getOnPlayerJoinMessage())));
		}
		catch(NullPointerException ignored) {}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Location loc = event.getPlayer().getLocation();
		if(Config.getPrintDeathCordsPublic()) {
			Chat.broadcast(new Text(
				event.getPlayer().getName() + " ist bei den Koordinaten" +
						"\nx: " + loc.getBlockX() +
						", y: " + loc.getBlockY() +
						", z: " + loc.getBlockZ() +
						"\ngestorben.",
				Text.Type.DEFAULT
			));
		}
		else {
			Chat.send(event.getPlayer(), new Text(
					"Du bist bei den Koordinaten" +
							"\nx: " + loc.getBlockX() +
							", y: " + loc.getBlockY() +
							", z: " + loc.getBlockZ() +
							"\ngestorben.",
					Text.Type.DEFAULT
			));
		}
	}
	
}
