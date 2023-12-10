package de.wattestaebchen.dyingrabbit99.features.locations;

import de.wattestaebchen.dyingrabbit99.Cmd;
import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class LocationCmd extends Cmd {	
	
	@CommandExecutor(label = "set")
	public boolean set(CommandSender sender, String name) {
		if(sender instanceof Player p) {
			boolean overwritten = LocationsConfig.isSet(name);
			LocationsConfig.setLocation(name, p.getLocation().getBlock().getLocation());
			if(overwritten) Chat.send(sender, new Text("Eintrag erfolgreich überschrieben.", Text.Type.SUCCESS));
			else Chat.send(sender, new Text("Eintrag erfolgreich erstellt.", Text.Type.SUCCESS));
		}
		else {
			Chat.send(sender, new Text(
					"Diese Version dieses Befehls ist nur für Spieler verfügbar. " +
							"Bitte übergib zusätzlich die Koordinaten als Argumente.",
					Text.Type.ERROR)
			);
		}
		return true;
	}
	@CommandExecutor(label = "set")
	public boolean set(CommandSender sender, String name, Integer x, Integer y, Integer z) {
		if(sender instanceof Player p) {
			boolean overwritten = LocationsConfig.isSet(name);
			Location location = new Location(p.getWorld(), x, y, z);
			LocationsConfig.setLocation(name, location);
			if(overwritten) Chat.send(sender, new Text("Eintrag erfolgreich überschrieben.", Text.Type.SUCCESS));
			else Chat.send(sender, new Text("Eintrag erfolgreich erstellt.", Text.Type.SUCCESS));
		}
		else {
			Chat.send(sender, new Text(
					"Diese Version dieses Befehls ist nur für Spieler verfügbar. " +
							"Bitte übergib zusätzlich den Namen der Welt als Argument.",
					Text.Type.ERROR)
			);
		}
		return true;
	}
	
	@CommandExecutor(label = "set")
	public boolean set(CommandSender sender, String name, String worldName, Integer x, Integer y, Integer z) {
		boolean overwritten = LocationsConfig.isSet(name);
		World world = DyingRabbit99.get().getServer().getWorld(worldName);
		if(world == null) {
			Chat.send(
					sender,
					new Text("Es existiert keine Welt mit diesem Namen.", Text.Type.ERROR).nl()
							.appendDefault("Folgende Welten existieren:")
							.appendCollection(DyingRabbit99.get().getServer().getWorlds(), w -> new Text().nl().appendDefault(w.getName()))
			);
			return true;
		}
		Location location = new Location(world, x, y, z);
		LocationsConfig.setLocation(name, location);
		if(overwritten) Chat.send(sender, new Text("Eintrag erfolgreich überschrieben.", Text.Type.SUCCESS));
		else Chat.send(sender, new Text("Eintrag erfolgreich erstellt.", Text.Type.SUCCESS));
		return true;
	}
	
	@CommandExecutor(label = "remove")
	public boolean remove(CommandSender sender, String name) {
		if(LocationsConfig.get().isSet(name)) {
			LocationsConfig.removeLocation(name);
			Chat.send(sender, new Text("Der Eintrag wurde erfolgreich gelöscht.", Text.Type.ERROR));
		}
		else {
			Chat.send(sender, new Text("Es existiert kein Eintrag mit diesem Namen.", Text.Type.ERROR));
		}
		return true;
	}
	
	@CommandExecutor(label = "get")
	public boolean get(CommandSender sender, String name) {
		Location location = LocationsConfig.getLocation(name);
		if(location == null) {
			Chat.send(sender, new Text("Es existiert kein Eintrag mit diesem Namen.", Text.Type.ERROR));
			return true;
		}
		
		if(location.getWorld() == null) {
			Chat.send(sender, new Text(
					"Der Punkt " + name + " befindet sich bei den Koordinaten:" +
							"\nx: " +location.getBlockX() +
							", y: " +location.getBlockY() +
							", z: " + location.getBlockZ(),
					Text.Type.DEFAULT
			));
		}
		else {
			Text text = new Text(
					"Der Punkt " + name + " befindet sich bei den Koordinaten:" +
							"\nWelt: " + location.getWorld().getEnvironment() + 
							", x: " +location.getBlockX() +
							", y: " +location.getBlockY() +
							", z: " +location.getBlockZ(),
					Text.Type.DEFAULT);
			if(sender instanceof Player p && p.getLocation().getWorld().equals(location.getWorld())) {
				Location playerLoc = p.getLocation().getBlock().getLocation();
				double euclideanDistance = ((double)(int)(playerLoc.distance(location)*100))/100;
				int manhattanDistance = Math.abs(playerLoc.getBlockX() - location.getBlockX())
						+ Math.abs(playerLoc.getBlockY() - location.getBlockY())
						+ Math.abs(playerLoc.getBlockZ() - location.getBlockZ());
				text.nl().appendDefault(
						"Du bist " + euclideanDistance +
								(euclideanDistance==1.0 ? " Block" : " Blöcke") + " (euklidisch) bzw. " +
								manhattanDistance + (manhattanDistance==1 ? " Block" : " Blöcke") +
								" (Manhattan) davon entfernt."
				);
			}
			Chat.send(sender, text);
		}
		return true;
	}
	
	@CommandExecutor(label = "list")
	public boolean list(CommandSender sender) {
		Collection<String> keys = LocationsConfig.listLocations();
		keys = keys.stream()
				.sorted((k0, k1) -> {
					int world = LocationsConfig.getLocation(k0).getWorld().getName().compareToIgnoreCase(LocationsConfig.getLocation(k1).getWorld().getName());
					return world == 0 ?
						k0.compareToIgnoreCase(k1) :
						world;	
				}).toList();
		Chat.send(
				sender,
				new Text("Liste aller gespeicherten Orte:", Text.Type.DEFAULT)
						.appendCollection(
								keys,
								(key) -> new Text().nl()
									.append(new Text(
											key,
											DyingRabbit99.enviromentToTextType(LocationsConfig.getLocation(key).getWorld().getEnvironment()),
											ClickEvent.runCommand("/location get " + key)
									))
						)
		);
		return true;
	}
	
	@CommandExecutor(label = "rename")
	public boolean rename(CommandSender sender, String name, String newName) {
		if(LocationsConfig.renameLocation(name, newName)) {
			Chat.send(sender, new Text("Eintrag erfolgreich umbenannt!", Text.Type.SUCCESS));
		}
		else {
			Chat.send(sender, new Text("Es existiert kein Eintrag mit diesem Namen.", Text.Type.ERROR));
		}
		return true;
	}
	
}
