package de.wattestaebchen.dyingrabbit99;

import de.wattestaebchen.dyingrabbit99.files.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class Events implements Listener {
	
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
			DyingRabbit99.broadcastMessage(
					Component.text().content(event.getPlayer().getName() + " ist bei den Koordinaten\nx: " + loc.getBlockX() + ", y: " + loc.getBlockY() + ", z: " + loc.getBlockZ() + "\ngestorben.").build(),
					DyingRabbit99.MessageType.DEFAULT
			);
		}
		else {
			DyingRabbit99.sendMessage(
					event.getPlayer(),
					Component.text().content("Du bist bei den Koordinaten\nx: " + loc.getBlockX() + ", y: " + loc.getBlockY() + ", z: " + loc.getBlockZ() + "\ngestorben.").build(),
					DyingRabbit99.MessageType.DEFAULT
			);
		}
	}
	
}
