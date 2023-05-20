package de.wattestaebchen.dyingrabbit99.commands;

import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import de.wattestaebchen.dyingrabbit99.files.Locations;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class LocationCmd extends Cmd {	
	
	@SubCommandExecutor(label = "set", cmdParams = {"sender"})
	public boolean set(CommandSender sender, String name) {
		if(sender instanceof Player p) {
			boolean overwritten = Locations.isSet(name);
			Locations.setLocation(name, p.getLocation());
			if(overwritten) Chat.send(sender, new Text("Eintrag erfolgreich überschrieben.", Chat.Type.SUCCESS));
			else Chat.send(sender, new Text("Eintrag erfolgreich erstellt.", Chat.Type.SUCCESS));
		}
		else {
			Chat.send(sender, new Text(
					"Diese Version dieses Befehls ist nur für Spieler verfügbar." +
							"Bitte übergib zusätzlich die Koordinaten als Argumente.",
					Chat.Type.ERROR)
			);
		}
		return true;
	}
	@SubCommandExecutor(label = "set", cmdParams = {"sender"})
	public boolean set(CommandSender sender, String name, Integer x, Integer y, Integer z) {
		boolean overwritten = Locations.isSet(name);
		Location location = (sender instanceof Player p) ?
				new Location(p.getWorld(), x, y, z) :
				new Location(null, x, y, z);
		Locations.setLocation(name, location);
		if(overwritten) Chat.send(sender, new Text("Eintrag erfolgreich überschrieben.", Chat.Type.SUCCESS));
		else Chat.send(sender, new Text("Eintrag erfolgreich erstellt.", Chat.Type.SUCCESS));
		return true;
	}
	
	@SubCommandExecutor(label = "remove", cmdParams = {"sender"})
	public boolean remove(CommandSender sender, String name) {
		if(Locations.get().isSet(name)) {
			Locations.removeLocation(name);
			Chat.send(sender, new Text("Der Eintrag wurde erfolgreich gelöscht.", Chat.Type.ERROR));
		}
		else {
			Chat.send(sender, new Text("Es existiert kein Eintrag mit diesem Namen.", Chat.Type.ERROR));
		}
		return true;
	}
	
	@SubCommandExecutor(label = "get", cmdParams = {"sender"})
	public boolean get(CommandSender sender, String name) {
		Location location = Locations.getLocation(name);
		if(location == null) {
			Chat.send(sender, new Text("Es existiert kein Eintrag mit diesem Namen.", Chat.Type.ERROR));
			return true;
		}
		
		if(location.getWorld() == null) {
			Chat.send(sender, new Text(
					"Der Punkt " + name + " befindet sich bei den Koordinaten:" +
							"\nx: " +location.getBlockX() +
							", y: " +location.getBlockY() +
							", z: " + location.getBlockZ(),
					Chat.Type.DEFAULT
			));
		}
		else {
			Text text = new Text(
					"Der Punkt " + name + " befindet sich bei den Koordinaten:" +
							"\nWelt: " + location.getWorld().getEnvironment() + 
							", x: " +location.getBlockX() +
							", y: " +location.getBlockY() +
							", z: " +location.getBlockZ(),
					Chat.Type.DEFAULT);
			if(sender instanceof Player p && p.getLocation().getWorld().equals(location.getWorld())) {
				int directDistance = (int) p.getLocation().distance(location);
				text.nl().appendDefault(
						"Du bist " + directDistance +
								(directDistance==1 ? " Block" : " Blöcke") +
								" davon entfernt."
				);
			}
			Chat.send(sender, text);
		}
		return true;
	}
	
	@SubCommandExecutor(label = "list", cmdParams = {"sender"})
	public boolean list(CommandSender sender) {
		Set<String> keys = Locations.listLocations();
		Chat.send(
				sender,
				new Text("Liste aller gespeicherten Orte:", Chat.Type.DEFAULT)
						.appendCollection(keys, (key) -> new Text("\n" + key, ClickEvent.runCommand("/location get " + key)))
		);
		
		return true;
	}
	
}
