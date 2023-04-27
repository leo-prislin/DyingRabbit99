package de.wattestaebchen.dyingrabbit99.commands;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import de.wattestaebchen.dyingrabbit99.files.Locations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

@Cmd.CommandAnnotation(labels = {"location", "loc"})
public class LocationCmd extends Cmd {	
	
	@SubCommandAnnotation(label = "set")
	@RequiresInfo(info = {"sender"})
	public boolean set(CommandSender sender, String name) {
		if(sender instanceof Player p) {
			boolean overwritten = Locations.isSet(name);
			Locations.setLocation(name, p.getLocation());
			if(overwritten) DyingRabbit99.sendMessage(sender, Component.text().content("Eintrag erfolgreich überschrieben.").build(), DyingRabbit99.MessageType.SUCCESS);
			else DyingRabbit99.sendMessage(sender, Component.text().content("Eintrag erfolgreich erstellt.").build(), DyingRabbit99.MessageType.SUCCESS);
		}
		else {
			DyingRabbit99.sendMessage(sender, Component.text().content("Diese Version dieses Befehls ist nur für Spieler verfügbar. Bitte übergib zusätzlich die Koordinaten als Argumente.").build(), DyingRabbit99.MessageType.ERROR);
		}
		return true;
	}
	@SubCommandAnnotation(label = "set")
	@RequiresInfo(info = {"sender"})
	public boolean set(CommandSender sender, String name, Integer x, Integer y, Integer z) {
		boolean overwritten = Locations.isSet(name);
		Location location = (sender instanceof Player p) ?
				new Location(p.getWorld(), x, y, z) :
				new Location(null, x, y, z);
		Locations.setLocation(name, location);
		if(overwritten) DyingRabbit99.sendMessage(sender, Component.text().content("Eintrag erfolgreich überschrieben.").build(), DyingRabbit99.MessageType.SUCCESS);
		else DyingRabbit99.sendMessage(sender, Component.text().content("Eintrag erfolgreich erstellt.").build(), DyingRabbit99.MessageType.SUCCESS);
		return true;
	}
	
	@SubCommandAnnotation(label = "remove")
	@RequiresInfo(info = {"sender"})
	public boolean remove(CommandSender sender, String name) {
		if(Locations.get().isSet(name)) {
			Locations.removeLocation(name);
			DyingRabbit99.sendMessage(sender, Component.text().content("Der Eintrag wurde erfolgreich gelöscht.").build(), DyingRabbit99.MessageType.SUCCESS);
		}
		else {
			DyingRabbit99.sendMessage(sender, Component.text().content("Es existiert kein Eintrag mit diesem Namen.").build(), DyingRabbit99.MessageType.ERROR);
		}
		return true;
	}
	
	@SubCommandAnnotation(label = "get")
	@RequiresInfo(info = {"sender"})
	public boolean get(CommandSender sender, String name) {
		Location location = Locations.getLocation(name);
		if(location == null) {
			DyingRabbit99.sendMessage(sender, Component.text().content("Es existiert kein Eintrag mit diesem Namen.").build(), DyingRabbit99.MessageType.ERROR);
			return true;
		}
		
		if(location.getWorld() == null) {
			DyingRabbit99.sendMessage(sender, Component.text().content("Der Punkt " + name + " befindet sich bei den Koordinaten:\nx: " +location.getBlockX()+ ", y: " +location.getBlockY()+ ", z: " + location.getBlockZ()).build(), DyingRabbit99.MessageType.DEFAULT);
		}
		else {
			String msg = "Der Punkt " + name + " befindet sich bei den Koordinaten:\nWelt: " + location.getWorld().getEnvironment() + ", x: " +location.getBlockX()+ ", y: " +location.getBlockY()+ ", z: " +location.getBlockZ();
			if(sender instanceof Player p && p.getLocation().getWorld().equals(location.getWorld())) {
				int directDistance = (int) p.getLocation().distance(location);
				msg += "\nDu bist " + directDistance + (directDistance==1 ? " Block" : " Blöcke") + " davon entfernt.";
			}
			DyingRabbit99.sendMessage(sender, Component.text().content(msg).build(), DyingRabbit99.MessageType.DEFAULT);
			
		}
		return true;
	}
	
	@SubCommandAnnotation(label = "list")
	@RequiresInfo(info = {"sender"})
	public boolean list(CommandSender sender) {
		Set<String> keys = Locations.listLocations();
		TextComponent.Builder tc = Component.text().content("Liste aller gespeicherten Orte:");
		for(String key : keys) {
			tc
					.appendNewline()
					.append(Component.text().content(key).color(DyingRabbit99.MessageType.CLICKABLE.getColor()).clickEvent(ClickEvent.runCommand("cords get " + key)));
		}
		DyingRabbit99.sendMessage(sender, tc.build(), DyingRabbit99.MessageType.DEFAULT);
		return true;
	}
	
}
