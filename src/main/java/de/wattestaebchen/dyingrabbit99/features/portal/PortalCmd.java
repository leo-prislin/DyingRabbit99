package de.wattestaebchen.dyingrabbit99.features.portal;

import de.wattestaebchen.dyingrabbit99.Cmd;
import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PortalCmd extends Cmd {
	
	private ArrayList<Portal> getPortals() {
		return PortalsConfig.getPortals();
	}
	private List<RealPortal> getRealPortals() {
		return getPortals().stream().filter((portal) -> portal instanceof RealPortal).map((portal) -> (RealPortal) portal).toList();
	}
	private List<ImaginaryPortal> getImaginaryPortals() {
		return getPortals().stream().filter((portal) -> portal instanceof ImaginaryPortal).map((portal) -> (ImaginaryPortal) portal).toList();
	}
	private ImaginaryPortal getImaginaryPortalByName(String name) {
		return getImaginaryPortals().stream().filter((portal) -> portal.getName().equals(name)).findFirst().orElse(null);
	}
	
	@CommandExecutor()
	public boolean execute(CommandSender sender) {
		
		Chat.send(
				sender,
				new Text("Simuliere...", Text.Type.DEFAULT)
						.nl().appendDefault("Portale:")
						.appendCollection(getPortals(), (portal) -> {
							List<Portal> compatiblePortals = getPortals().stream().filter(portal::isPortalCompatible).toList();
							Text text = Text.newLine()
									.append(portal.toText());
							if(compatiblePortals.isEmpty()) {
								text.nl().indent(1).appendDefault("Dieses Portal hat keine kompatiblen Portale.");
							}
							else {
								Portal closestPortal = portal.getClosestPortal(compatiblePortals);
								text.nl().indent(1).appendDefault("Kompatible Portale:")
										.appendCollection(
												compatiblePortals,
												(compatiblePortal) -> compatiblePortal.equals(closestPortal) ?
														Text.newLine().indent(2).append(compatiblePortal.toText().setDecorations(TextDecoration.BOLD)) :
														Text.newLine().indent(2).append(compatiblePortal.toText())
										);
							}
							return text;
						})
		);
		return true;
	}
	
	@CommandExecutor(label = "scan", playerOnly = true)
	public boolean scan(Player p) {
		return scan(p, 5, 5, 5);
	}
	
	@CommandExecutor(label = "scan", playerOnly = true)
	public boolean scan(Player p, Integer xBounds, Integer yBounds, Integer zBounds) {
		if(xBounds*yBounds*zBounds > 1000) {
			Chat.send(p, new Text("Bitte beschränke dich auf einen kleineren Suchbereich.", Text.Type.ERROR));
			return true;
		}
		
		Location playerLoc = p.getLocation();
		
		// Remove portals that don´t exist anymore
		int removedPortals = 0;
		for(int i = 0; i < getPortals().size(); i++) {
			Portal portal = getPortals().get(i);
			if(portal instanceof RealPortal realPortal && !realPortal.exists()) {
				getPortals().remove(portal);
				i--;
				removedPortals++;
			}
		}
		// Scan every single block in radius for portals
		int addedPortals = 0;
		for(int x = playerLoc.getBlockX()-xBounds; x <= playerLoc.getBlockX()+xBounds; x++) {
			for(int y = playerLoc.getBlockY()-yBounds; y <= playerLoc.getBlockY()+yBounds; y++) {
				for(int z = playerLoc.getBlockZ()-zBounds; z <= playerLoc.getBlockZ()+zBounds; z++) {
					Block block = playerLoc.getWorld().getBlockAt(x, y, z);
					// Add found nether portal
					if(block.getType() == Material.NETHER_PORTAL) {
						RealPortal portal = new RealPortal(block);
						if(!getPortals().contains(portal)) {
							getPortals().add(portal);
							addedPortals++;
						}
					}
				}
			}
		}
		
		Chat.send(p, new Text(
				"Es wurden " + removedPortals + " Portale, die nicht mehr existieren, entfernt und " +
						addedPortals + " neue Portale registriert.",
				Text.Type.SUCCESS
		));
		
		return true;
	}
	
	
	@CommandExecutor(label = "sim add", playerOnly = true)
	public boolean simAdd(Player p, String name){		
		if(getImaginaryPortals().stream().anyMatch((portal) -> portal.getName().equals(name))) {
			Chat.send(p, new Text("Es existiert bereits ein Portal mit diesem Namen.", Text.Type.ERROR));
			return true;
		}
		getPortals().add(new ImaginaryPortal(name, new Location(p.getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ())));
		Chat.send(p, new Text("Portal erfolgreich hinzugefügt.", Text.Type.SUCCESS));
		return true;
	}
	
	@CommandExecutor(label = "sim remove")
	public boolean simRemove(CommandSender sender, String name) {
		ImaginaryPortal portal = getImaginaryPortalByName(name);
		if(portal == null) {
			Chat.send(sender, new Text("Es existiert kein Portal mit diesem Namen.", Text.Type.ERROR));
			return true;
		}
		getPortals().remove(portal);
		Chat.send(sender, new Text("Portal erfolgreich entfernt.", Text.Type.SUCCESS));
		return true;
	}
	
	@CommandExecutor(label = "sim rename")
	public boolean simRename(CommandSender sender, String oldName, String newName) {
		ImaginaryPortal portal = getImaginaryPortalByName(oldName);
		if(portal == null) {
			Chat.send(sender, new Text("Es existiert kein Portal mit diesem Namen.", Text.Type.ERROR));
			return true;
		}
		portal.rename(newName);
		Chat.send(sender, new Text("Portal erfolgreich umbenannt.", Text.Type.SUCCESS));
		return true;
	}
	
}
