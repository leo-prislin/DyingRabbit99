package de.wattestaebchen.dyingrabbit99.commands;

import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PortalCmd extends Cmd {
	
	private abstract static class Portal {
		
		private boolean imaginary;
		private final Block centeredBlock;
		
		public Portal(Block anyPortalBlock) {
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
		
		public Block getBlock() {
			return centeredBlock;
		}
		public World getWorld() {
			return centeredBlock.getWorld();
		}
		public Location getLocation() {
			return centeredBlock.getLocation();
		}
		public boolean isInNether() {
			return centeredBlock.getWorld().getEnvironment() == World.Environment.NETHER;
		}
		
		/** The returned Location may have floating point coordinates. */
		public Location getOptimalCorrespondingLocation(World otherWorld) {
			if(centeredBlock.getWorld().getEnvironment() == otherWorld.getEnvironment()) {
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
							(otherPortal) -> getLocation().distanceSquared(otherPortal.getLocation())
					)
			);
			if(closest.isPresent()) {
				return closest.get();
			}
			else {
				throw new IllegalArgumentException("Parameter otherPortals must not be empty.");
			}
		}
		
		/**
		 * Returns true if the portals orientation is x, false otherwise.
		 * @throws RuntimeException If parameter portalBlock isn´t of type Nether_Portal.
		 */
		private boolean getOrientation() {
			return getOrientation(centeredBlock);
		}
		private static boolean getOrientation(Block somePortalBlock) {
			String data = somePortalBlock.getBlockData().getAsString();
			if(data.startsWith("minecraft:nether_portal[axis=")) {
				return data.charAt(29) == 'x';
			}
			else throw new RuntimeException("The portal´s BlockData should start with \"minecraft:nether_portal[axis=\"... but is actually \"" + data + "\".");
		}
		
	}
	
	private final ArrayList<Portal> ps = new ArrayList<>();
	
	
	private final ArrayList<Block> portals = new ArrayList<>();
	private final HashMap<String, Profile> profiles = new HashMap<>();
	
	private static class Profile {
		private final HashMap<String, Location> imaginaryPortals = new HashMap<>();
	}
	private final HashMap<UUID, Profile> playerStates = new HashMap<>();
	
	
	@CommandExecutor(cmdParams = {"sender"})
	public boolean execute(CommandSender sender) {
		if(sender instanceof Player p) {
			
			Profile profile = playerStates.get(p.getUniqueId());
			if(profile == null) {
				Chat.send(sender, new Text("Du hast kein Profil ausgewählt.", Text.Type.ERROR));
				return true;
			}
			
			Chat.send(
					sender,
					new Text("Simuliere...", Text.Type.DEFAULT)
							.nl().appendDefault("Echte Portale:")
							.appendCollection(portals, (portal) -> {
								// Real Portal
								
								List<Block> compatiblePortals = getCompatiblePortals(portal, portals);
								Optional<Block> closestPortalOptional = compatiblePortals.stream().min(
										Comparator.comparingDouble(
												p0 -> p0.getLocation().distanceSquared(portal.getLocation())
										)
								);
								
								
								
								Text text = Text.newLine()
										.append(realPortalToText(portal))
										.nl().indent(1).appendDefault("Kompatible Portale:")
										.appendCollection(
												getCompatiblePortals(portal, portals),
												(compatiblePortal) -> Text.newLine().indent(2).append(realPortalToText(compatiblePortal))
										);
								closestPortalOptional.ifPresent(
										block -> text.nl().indent(1).appendDefault("Korrespondierendes Portal:")
												.nl().indent(2).append(realPortalToText(closestPortalOptional.get()))
								);
								
								return text;
										
							})
							.nl().nl().appendDefault("Simulierte Portale:")
							.appendDefault("TODO")
			);
			
		}
		else {
			Chat.send(sender, new Text("Dieser Command ist nur für Spieler verfügbar.", Text.Type.ERROR));
		}
		return true;
	}
	
	@SubCommandExecutor(label = "profile create", cmdParams = {"sender"})
	public boolean profileCreate(CommandSender sender, String name) {
		if(profiles.containsKey(name)) {
			Chat.send(sender,	new Text("Es existiert bereits ein Profil mit diesem Namen.", Text.Type.ERROR));
			return true;
		}
		Profile profile = new Profile();
		profiles.put(name, profile);
		if(sender instanceof Player p) {
			playerStates.put(p.getUniqueId(), profile);
		}
		Chat.send(sender, new Text("Profil erfolgreich erstellt.", Text.Type.SUCCESS));
		return true;
	}
	
	@SubCommandExecutor(label = "profile delete", cmdParams = {"sender"})
	public boolean profileDelete(CommandSender sender, String name) {
		if(!profiles.containsKey(name)) {
			Chat.send(sender, new Text("Es existiert kein Profil mit diesem Namen.", Text.Type.ERROR));
			return true;
		}
		Profile profile = profiles.remove(name);
		if(sender instanceof Player p && playerStates.get(p.getUniqueId()) == profile) {
			playerStates.remove(p.getUniqueId());
		}
		Chat.send(sender, new Text("Profil erfolgreich gelöscht.", Text.Type.SUCCESS));
		return true;
	}
	
	@SubCommandExecutor(label = "profile rename", cmdParams = {"sender"})
	public boolean profileRename(CommandSender sender, String oldName, String newName) {
		if(!profiles.containsKey(oldName)) {
			Chat.send(sender, new Text("Es existiert kein Profil mit diesem Namen.", Text.Type.ERROR));
			return true;
		}
		profiles.put(newName, profiles.remove(oldName));
		Chat.send(sender, new Text("Profil erfolgreich umbenannt.", Text.Type.SUCCESS));
		return true;
	}
	
	@SubCommandExecutor(label = "profile checkout", cmdParams = {"sender"})
	public boolean profileCheckout(CommandSender sender, String name) {
		if(sender instanceof Player p) {
			if(!profiles.containsKey(name)) {
				Chat.send(sender, new Text("Es existiert kein Profil mit diesem Namen.", Text.Type.ERROR));
				return true;
			}
			playerStates.put(p.getUniqueId(), profiles.get(name));
		}
		else {
			Chat.send(sender, new Text("Dieser Command ist nur für Spieler verfügbar.", Text.Type.ERROR));
		}
		return true;
	}
	
	
	@SubCommandExecutor(label = "scan", cmdParams = {"sender"})
	public boolean scan(CommandSender sender, Integer xBounds, Integer yBounds, Integer zBounds) {
		if(sender instanceof Player p) {
			
			if(xBounds*yBounds*zBounds > 100) {
				Chat.send(sender, new Text("Bitte beschränke dich auf einen kleineren Suchbereich.", Text.Type.ERROR));
				return true;
			}
			
			Location playerLoc = p.getLocation();
			
			// Remove portals that don´t exist anymore
			int removedPortals = 0;
			for(int i = 0; i < portals.size(); i++) {
				Block portalBlock = portals.get(i);
				if(portalBlock.getType() != Material.NETHER_PORTAL) {
					portals.remove(portalBlock);
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
							block = getCenteredPortalBlock(block);
							if(!portals.contains(block)) {
								portals.add(block);
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
			Profile profile = playerStates.get(p.getUniqueId());
			if(profile == null) {
				Chat.send(sender, new Text("Du hast aktuell kein Simulations-Profil ausgewählt.", Text.Type.ERROR));
				return true;
			}
			if(profile.imaginaryPortals.containsKey(name)) {
				Chat.send(sender, new Text("Es existiert bereits ein Portal mit diesem Namen.", Text.Type.ERROR));
				return true;
			}
			profile.imaginaryPortals.put(name, new Location(p.getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()));
			Chat.send(sender, new Text("Portal erfolgreich registriert.", Text.Type.SUCCESS));
		}
		else {
			Chat.send(sender, new Text("Dieser Command ist nur für Spieler verfügbar.", Text.Type.ERROR));
		}
		return true;
	}
	
	@SubCommandExecutor(label = "sim remove", cmdParams = {"sender"})
	public boolean simRemove(CommandSender sender, String name) {
		if(sender instanceof Player p) {
			Profile profile = playerStates.get(p.getUniqueId());
			if(profile == null) {
				Chat.send(sender, new Text("Du hast aktuell kein Simulations-Profil ausgewählt.", Text.Type.ERROR));
				return true;
			}
			if(!profile.imaginaryPortals.containsKey(name)) {
				Chat.send(sender, new Text("Es existiert kein Portal mit diesem Namen.", Text.Type.ERROR));
				return true;
			}
			profile.imaginaryPortals.remove(name);
			Chat.send(sender, new Text("Portal erfolgreich entfernt.", Text.Type.SUCCESS));
		}
		else {
			Chat.send(sender, new Text("Dieser Command ist nur für Spieler verfügbar.", Text.Type.ERROR));
		}
		return true;
	}
	
	@SubCommandExecutor(label = "sim rename", cmdParams = {"sender"})
	public boolean simRename(CommandSender sender, String oldName, String newName) {
		if(sender instanceof Player p) {
			Profile profile = playerStates.get(p.getUniqueId());
			if(profile == null) {
				Chat.send(sender, new Text("Du hast aktuell kein Simulations-Profil ausgewählt.", Text.Type.ERROR));
				return true;
			}
			if(!profile.imaginaryPortals.containsKey(oldName)) {
				Chat.send(sender, new Text("Es existiert kein Portal mit diesem Namen.", Text.Type.ERROR));
				return true;
			}
			Location portal = profile.imaginaryPortals.remove(oldName);
			profile.imaginaryPortals.put(newName, portal);
			Chat.send(sender, new Text("Portal erfolgreich umbenannt.", Text.Type.SUCCESS));
		}
		else {
			Chat.send(sender, new Text("Dieser Command ist nur für Spieler verfügbar.", Text.Type.ERROR));
		}
		return true;
	}
	
	
	
	
	/**
	 * Returns true if the portals orientation is x, false otherwise.
	 * @throws RuntimeException If parameter portalBlock isn´t of type Nether_Portal.
	 */
	private boolean getPortalOrientation(Block portalBlock) {
		String data = portalBlock.getBlockData().getAsString();
		if(data.startsWith("minecraft:nether_portal[axis=")) {
			return data.charAt(29) == 'x';
		}
		else throw new RuntimeException("The portal´s BlockData should start with \"minecraft:nether_portal[axis=\"... but is actually \"" + data + "\".");
	}
	private Block getCenteredPortalBlock(Block anyPortalBlock) {
		if(anyPortalBlock.getType() != Material.NETHER_PORTAL) {
			throw new IllegalArgumentException("Parameter anyPortalBlock must be of Type NETHER_PORTAL.");
		}
		// Adjust Y
		while(anyPortalBlock.getRelative(0, -1, 0).getType() == Material.NETHER_PORTAL) {
			anyPortalBlock = anyPortalBlock.getRelative(0, -1, 0);
		}
		// Adjust X
		if(getPortalOrientation(anyPortalBlock)) {
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
		return anyPortalBlock;
	}
	
	
	private Text realPortalToText(Block portalBlock) {
		return new Text(
				"[P] " + (portalBlock.getWorld().getEnvironment() == World.Environment.NETHER ? "N" : "OW") +
						" <" + portalBlock.getX() + " " + portalBlock.getY() + " " + portalBlock.getZ() + ">",
				(portalBlock.getWorld().getEnvironment() == World.Environment.NETHER) ? Text.Type.NETHER : Text.Type.OVERWORLD
		);
	}
	private Text imaginaryPortalAsString(String name, Location portalLocation) {
		return new Text("[" + name + "] <" + portalLocation.getBlockX() + " " + portalLocation.getBlockY() + " " + portalLocation.getBlockZ() + ">", Text.Type.DEFAULT);
	}
	
	
	private List<Block> getCompatiblePortals(Block portal, Collection<Block> portals) {
		return portals.stream()
				.filter((block) -> isPortalCompatible(portal, block))
				.collect(Collectors.toList());
	}
	private boolean isPortalCompatible(Block portal0, Block portal1) {
		if(portal0.getWorld().getEnvironment() == World.Environment.NORMAL) {
			return portal1.getWorld().getEnvironment() == World.Environment.NETHER &&
					Math.abs(portal0.getX()/8.0 - portal1.getX()) <= 16.0 &&
					Math.abs(portal0.getZ()/8.0 - portal1.getZ()) <= 16.0;
		}
		else if(portal0.getWorld().getEnvironment() == World.Environment.NETHER) {
			return portal0.getWorld().getEnvironment() == World.Environment.NORMAL &&
					Math.abs(portal0.getX()*8 - portal1.getX()) <= 128 &&
					Math.abs(portal0.getZ()*8 - portal1.getZ()) <= 128;
		}
		else throw new RuntimeException("The portalBlock´s environment is neither in a NORMAL nor a NETHER world.");
	}
	
}
