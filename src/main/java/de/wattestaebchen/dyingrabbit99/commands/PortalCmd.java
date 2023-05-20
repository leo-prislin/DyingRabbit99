package de.wattestaebchen.dyingrabbit99.commands;

import de.wattestaebchen.dyingrabbit99.Chat;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PortalCmd extends Cmd {
	
	private final HashMap<String, Simulation> simulations = new HashMap<>();
	
	private static class Simulation {
		private final ArrayList<Block> portals = new ArrayList<>();
	}
	private final HashMap<UUID, Simulation> playerStates = new HashMap<>();
	
	@SubCommandExecutor(label = "sim", cmdParams = {"sender"})
	public boolean sim(CommandSender sender) {
		
		return true;
	}
	
	@SubCommandExecutor(label = "sim create", cmdParams = {"sender"})
	public boolean simCreate(CommandSender sender, String name) {
		if(simulations.containsKey(name)) {
			Chat.send(
					sender,
					new Chat.Text("Es existiert bereits eine Simulation mit diesem Namen. Du kannst diese", Chat.Type.DEFAULT)
							.append(new Chat.Text(" umbenennen,", ClickEvent.suggestCommand("/sim rename " + name + "<newName>")))
							.append(new Chat.Text(" löschen", ClickEvent.suggestCommand("/sim delete " + name)))
							.append(new Chat.Text(" oder dir einen anderen Namen aussuchen.", Chat.Type.DEFAULT))
			);
			return true;
		}
		Simulation simulation = new Simulation();
		simulations.put(name, simulation);
		if(sender instanceof Player p) {
			playerStates.put(p.getUniqueId(), simulation);
		}
		Chat.send(sender, new Chat.Text("Simulation erfolgreich erstellt.", Chat.Type.SUCCESS));
		return true;
	}
	
	@SubCommandExecutor(label = "sim delete", cmdParams = {"sender"})
	public boolean simRemove(CommandSender sender, String name) {
		if(!simulations.containsKey(name)) {
			Chat.send(sender, new Chat.Text("Es existiert keine Simulation mit diesem Namen.", Chat.Type.ERROR));
			return true;
		}
		Simulation simulation = simulations.remove(name);
		if(sender instanceof Player p && playerStates.get(p.getUniqueId()) == simulation) {
			playerStates.remove(p.getUniqueId());
		}
		Chat.send(sender, new Chat.Text("Simulation erfolgreich gelöscht.", Chat.Type.SUCCESS));
		return true;
	}
	
	@SubCommandExecutor(label = "sim rename", cmdParams = {"sender"})
	public boolean simRename(CommandSender sender, String oldName, String newName) {
		if(!simulations.containsKey(oldName)) {
			Chat.send(sender, new Chat.Text("Es existiert keine Simulation mit diesem Namen.", Chat.Type.ERROR));
			return true;
		}
		simulations.put(newName, simulations.remove(oldName));
		Chat.send(sender, new Chat.Text("Simulation erfolgreich umbenannt.", Chat.Type.SUCCESS));
		return true;
	}
	
	@SubCommandExecutor(label = "sim checkout", cmdParams = {"sender"})
	public boolean simCheckout(CommandSender sender, String name) {
		if(sender instanceof Player p) {
			if(!simulations.containsKey(name)) {
				Chat.send(sender, new Chat.Text("Es existiert keine Simulation mit diesem Namen.", Chat.Type.ERROR));
				return true;
			}
			playerStates.put(p.getUniqueId(), simulations.get(name));
		}
		else Chat.send(sender, new Chat.Text("Dieser Command ist nur für Spieler verfügbar.", Chat.Type.ERROR));
		return true;
	}
	
	
	
	@SubCommandExecutor(label = "add", cmdParams = {"sender"})
	public boolean add(CommandSender sender) {
		if(sender instanceof Player p) {
			
			if(!playerStates.containsKey(p.getUniqueId())) {
				return true;
			}
			
			Location playerLoc = p.getLocation();
			
			Block portalBlock = null;
			for(int x = -1; x < 1; x++) {
				for(int z = -1; z < 1; z++) {
					Block block = playerLoc.getBlock().getRelative(x, 0, z);
					if(block.getType() == Material.NETHER_PORTAL) {
						portalBlock = block;
					}
				}
			}
			if(portalBlock == null) {
				return true;
			}
			
			// Get portal bottom
			while(portalBlock.getRelative(0, -1, 0).getType() == Material.NETHER_PORTAL) {
				portalBlock = portalBlock.getRelative(0, -1, 0);
			}
			
			boolean orientation = getPortalOrientation(portalBlock);
			if(orientation) {
				// Get portal bounds on x-axis
				int lowerBound = portalBlock.getX();
				while(portalBlock.getRelative(lowerBound-1, 0, 0).getType() == Material.NETHER_PORTAL) {
					lowerBound--;
				}
				int higherBound = portalBlock.getX();
				while(portalBlock.getRelative(higherBound+1, 0, 0).getType() == Material.NETHER_PORTAL) {
					higherBound++;
				}
				// Set portals center block as portalBlock
				portalBlock = portalBlock.getWorld().getBlockAt((lowerBound+higherBound)/2, portalBlock.getY(), portalBlock.getZ());
			}
			else {
				// Get portal bounds on z-axis
				int lowerBound = portalBlock.getZ();
				while(portalBlock.getRelative(0, 0, lowerBound-1).getType() == Material.NETHER_PORTAL) {
					lowerBound--;
				}
				int higherBound = portalBlock.getZ();
				while(portalBlock.getRelative(0, 0, higherBound+1).getType() == Material.NETHER_PORTAL) {
					higherBound++;
				}
				// Set portals center block as portalBlock
				portalBlock = portalBlock.getWorld().getBlockAt(portalBlock.getX(), portalBlock.getY(), (lowerBound+higherBound)/2);
			}
			
			playerStates.get(p.getUniqueId()).portals.add(portalBlock);
			
		}
		Chat.send(sender, new Chat.Text("Dieser Command ist nur für Spieler verfügbar.", Chat.Type.ERROR));
		return true;
	}
	
	
	/**
	 * Returns true if the portals orientation is x, false otherwise.
	 * Throws a RuntimeException if block isn´t a Nether_Portal.
	 */
	private boolean getPortalOrientation(Block block) {
		String data = block.getBlockData().getAsString();
		if(data.startsWith("minecraft:nether_portal[axis=")) {
			return data.charAt(29) == 'x';
		}
		else throw new RuntimeException("The portal´s BlockData should start with \"minecraft:nether_portal[axis=\"... but is actually \"" + data + "\".");
	}
	
}
