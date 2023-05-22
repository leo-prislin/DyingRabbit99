package de.wattestaebchen.dyingrabbit99.commands;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class PortalCmd extends Cmd {
	
	
	private static abstract class Portal {
		
		public abstract Location getLocation();
		public abstract World getWorld();
		public boolean isInNether() {
			return getWorld().getEnvironment() == World.Environment.NETHER;
		}
		
		
		/** The returned Location may have floating point coordinates. */
		public Location getOptimalCorrespondingLocation(World otherWorld) {
			if(getWorld().getEnvironment() == otherWorld.getEnvironment()) {
				throw new IllegalArgumentException("Parameter otherWorld must not be of the same environment as this portal.");
			}
			if(isInNether()) {
				return new Location(otherWorld, getLocation().getX()*8, getLocation().getY(), getLocation().getZ()*8);
			}
			else {
				return new Location(otherWorld, getLocation().getX()/8.0, getLocation().getY(), getLocation().getZ()/8.0);
			}
		}
		/** Checks if the otherPortal could be reached when entering this portal and no better options exist. */
		public boolean isPortalCompatible(Portal otherPortal) {
			if(getWorld().getEnvironment() == otherPortal.getWorld().getEnvironment()) {
				return false;
			}
			if(isInNether()) {
				Location optimalCorrespondingLocation = getOptimalCorrespondingLocation(otherPortal.getWorld());
				return Math.abs(optimalCorrespondingLocation.getBlockX() - otherPortal.getLocation().getBlockX()) <= 128 &&
						Math.abs(optimalCorrespondingLocation.getBlockZ() - otherPortal.getLocation().getBlockZ()) <= 128;
			}
			else {
				Location optimalCorrespondingLocation = getOptimalCorrespondingLocation(otherPortal.getWorld());
				return Math.abs(optimalCorrespondingLocation.getBlockX() - otherPortal.getLocation().getBlockX()) <= 16 &&
						Math.abs(optimalCorrespondingLocation.getBlockZ() - otherPortal.getLocation().getBlockZ()) <= 16;
			}
		}
		
		/** @throws IllegalArgumentException If parameter otherPortals is empty. */
		public Portal getClosestPortal(Collection<Portal> otherPortals) {
			Optional<Portal> closest = otherPortals.stream().min(
					Comparator.comparingDouble(
							(otherPortal) -> getOptimalCorrespondingLocation(otherPortal.getWorld()).distanceSquared(otherPortal.getLocation())
					)
			);
			if(closest.isPresent()) {
				return closest.get();
			}
			else {
				throw new IllegalArgumentException("Parameter otherPortals must not be empty.");
			}
		}
		
		public Text toText() {
			return new Text(this.toString(), (isInNether() ? Text.Type.NETHER : Text.Type.OVERWORLD));
		}
		
	}
	
	private static class RealPortal extends Portal {
		
		private final Block centeredBlock;
		public Block getBlock() {
			return centeredBlock;
		}
		
		public RealPortal(Block anyPortalBlock) {
			if(anyPortalBlock.getType() != Material.NETHER_PORTAL) {
				throw new IllegalArgumentException("Parameter anyPortalBlock must be of Type NETHER_PORTAL.");
			}
			// Adjust Y
			while(anyPortalBlock.getRelative(0, -1, 0).getType() == Material.NETHER_PORTAL) {
				anyPortalBlock = anyPortalBlock.getRelative(0, -1, 0);
			}
			// Adjust X
			if(getOrientation(anyPortalBlock)) {
				int lowerBound = 0;
				while(anyPortalBlock.getRelative(lowerBound-1, 0, 0).getType() == Material.NETHER_PORTAL) {
					lowerBound--;
				}
				int higherOffset = 0;
				while(anyPortalBlock.getRelative(higherOffset+1, 0, 0).getType() == Material.NETHER_PORTAL) {
					higherOffset++;
				}
				anyPortalBlock = anyPortalBlock.getWorld().getBlockAt((higherOffset+anyPortalBlock.getX())/2 + (lowerBound+anyPortalBlock.getX())/2, anyPortalBlock.getY(), anyPortalBlock.getZ());
			}
			// Adjust Z
			else {
				int lowerOffset = 0;
				while(anyPortalBlock.getRelative(0, 0, lowerOffset-1).getType() == Material.NETHER_PORTAL) {
					lowerOffset--;
				}
				int higherOffset = 0;
				while(anyPortalBlock.getRelative(0, 0, higherOffset+1).getType() == Material.NETHER_PORTAL) {
					higherOffset++;
				}
				anyPortalBlock = anyPortalBlock.getWorld().getBlockAt(anyPortalBlock.getX(), anyPortalBlock.getY(), (higherOffset+anyPortalBlock.getZ())/2 + (lowerOffset+anyPortalBlock.getZ())/2);
			}
			this.centeredBlock = anyPortalBlock;
		}
		
		public boolean exists() {
			return getBlock().getType() == Material.NETHER_PORTAL;
		}
		
		@Override
		public World getWorld() {
			return getBlock().getWorld();
		}
		@Override
		public Location getLocation() {
			return getBlock().getLocation();
		}
		
		/** Returns true if the portals orientation is x, false otherwise. This method is equal to RealPortal.getOrientation(getBlock)). */
		private boolean getOrientation() {
			return RealPortal.getOrientation(getBlock());
		}
		/**
		 * Returns true if the portals orientation is x, false otherwise.
		 * @throws RuntimeException If parameter portalBlock isn´t of type Nether_Portal.
		 */
		private static boolean getOrientation(Block somePortalBlock) {
			String data = somePortalBlock.getBlockData().getAsString();
			if(data.startsWith("minecraft:nether_portal[axis=")) {
				return data.charAt(29) == 'x';
			}
			else throw new RuntimeException("The portal´s BlockData should start with \"minecraft:nether_portal[axis=\"... but is actually \"" + data + "\".");
		}
		
		
		@Override
		public String toString() {
			return "[P]" + (isInNether() ? " N " : " OW ") +
					"<" + getLocation().getX() + " " + getLocation().getY() + " " + getLocation().getZ() + ">";
		}
		
		@Override
		public boolean equals(Object otherPortal) {
			if(otherPortal instanceof RealPortal p) {
				return this == otherPortal || getBlock().equals(p.getBlock());
			}
			return false;
		}
	}
	
	private static class ImaginaryPortal extends Portal {
		
		private String name;
		public String getName() {
			return name;
		}
		public void rename(String newName) {
			this.name = newName;
		}
		
		private final Location location;
		@Override
		public Location getLocation() {
			return location;
		}
		
		public ImaginaryPortal(String name, Location location) {
			if(name == null || location == null) {
				throw new IllegalArgumentException("Parameters must not be null.");
			}
			this.name = name;
			this.location = DyingRabbit99.normalizeLocation(location);
		}
		
		@Override
		public World getWorld() {
			return getLocation().getWorld();
		}
		@Override
		public boolean isInNether() {
			return getWorld().getEnvironment() == World.Environment.NETHER;
		}
		
		@Override
		public String toString() {
			return "[" + getName() + "]" + (isInNether() ? " N " : " OW ") +
					"<" + getLocation().getX() + " " + getLocation().getY() + " " + getLocation().getZ() + ">";
		}
		
		@Override
		public boolean equals(Object otherPortal) {
			if(otherPortal instanceof ImaginaryPortal p) {
				return this == otherPortal || (getName().equals(p.getName()) && getLocation().equals(p.getLocation()));
			}
			return false;
		}
		
	}
	
	private final ArrayList<Portal> portals = new ArrayList<>();
	private List<RealPortal> getRealPortals() {
		return portals.stream().filter((portal) -> portal instanceof RealPortal).map((portal) -> (RealPortal) portal).toList();
	}
	private List<ImaginaryPortal> getImaginaryPortals() {
		return portals.stream().filter((portal) -> portal instanceof ImaginaryPortal).map((portal) -> (ImaginaryPortal) portal).toList();
	}
	
	private ImaginaryPortal getImaginaryPortalByName(String name) {
		return getImaginaryPortals().stream().filter((portal) -> portal.getName().equals(name)).findFirst().orElse(null);
	}
	
	@SubCommandExecutor(label = "test", cmdParams = {"sender"})
	public boolean test(CommandSender sender) {
		var a = new Text("a", Text.Type.OVERWORLD, TextDecoration.BOLD);
		var b = new Text("b", Text.Type.OVERWORLD);
		Chat.send(sender, a.append(b));
		return true;
	}
	
	@CommandExecutor(cmdParams = {"sender"})
	public boolean execute(CommandSender sender) {
		
		Chat.send(
				sender,
				new Text("Simuliere...", Text.Type.DEFAULT)
						.nl().appendDefault("Portale:")
						.appendCollection(portals, (portal) -> {
							List<Portal> compatiblePortals = portals.stream().filter(portal::isPortalCompatible).toList();
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
	
	@SubCommandExecutor(label = "scan", cmdParams = {"sender"})
	public boolean scan(CommandSender sender) {
		return scan(sender, 5, 5, 5);
	}
	
	@SubCommandExecutor(label = "scan", cmdParams = {"sender"})
	public boolean scan(CommandSender sender, Integer xBounds, Integer yBounds, Integer zBounds) {
		if(sender instanceof Player p) {
			
			if(xBounds*yBounds*zBounds > 1000) {
				Chat.send(sender, new Text("Bitte beschränke dich auf einen kleineren Suchbereich.", Text.Type.ERROR));
				return true;
			}
			
			Location playerLoc = p.getLocation();
			
			// Remove portals that don´t exist anymore
			int removedPortals = 0;
			for(int i = 0; i < portals.size(); i++) {
				Portal portal = portals.get(i);
				if(portal instanceof RealPortal realPortal && !realPortal.exists()) {
					portals.remove(portal);
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
							if(!portals.contains(portal)) {
								portals.add(portal);
								addedPortals++;
							}
						}
					}
				}
			}
			
			Chat.send(sender, new Text(
					"Es wurden " + removedPortals + " Portale, die nicht mehr existieren, entfernt und " +
							addedPortals + " neue Portale registriert.",
					Text.Type.SUCCESS
			));
			
		}
		else {
			Chat.send(sender, new Text("Dieser Command ist nur für Spieler verfügbar.", Text.Type.ERROR));
		}
		return true;
	}
	
	
	@SubCommandExecutor(label = "sim add", cmdParams = {"sender"})
	public boolean simAdd(CommandSender sender, String name){
		if(sender instanceof Player p) {
			
			if(getImaginaryPortals().stream().anyMatch((portal) -> portal.getName().equals(name))) {
				Chat.send(sender, new Text("Es existiert bereits ein Portal mit diesem Namen.", Text.Type.ERROR));
				return true;
			}
			portals.add(new ImaginaryPortal(name, new Location(p.getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ())));
			Chat.send(sender, new Text("Portal erfolgreich hinzugefügt.", Text.Type.SUCCESS));
		}
		else {
			Chat.send(sender, new Text("Dieser Command ist nur für Spieler verfügbar.", Text.Type.ERROR));
		}
		return true;
	}
	
	@SubCommandExecutor(label = "sim remove", cmdParams = {"sender"})
	public boolean simRemove(CommandSender sender, String name) {
		ImaginaryPortal portal = getImaginaryPortalByName(name);
		if(portal == null) {
			Chat.send(sender, new Text("Es existiert kein Portal mit diesem Namen.", Text.Type.ERROR));
			return true;
		}
		portals.remove(portal);
		Chat.send(sender, new Text("Portal erfolgreich entfernt.", Text.Type.SUCCESS));
		return true;
	}
	
	@SubCommandExecutor(label = "sim rename", cmdParams = {"sender"})
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
