package de.wattestaebchen.dyingrabbit99.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PortalCmd extends Cmd {
	
	private final HashMap<String, Simulation> simulations = new HashMap<String, Simulation>();
	
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
			return true;
		}
		simulations.put(name, new Simulation());
		return true;
	}
	
	@SubCommandExecutor(label = "sim remove", cmdParams = {"sender"})
	public boolean simRemove(CommandSender sender, String name) {
		if(!simulations.containsKey(name)) {
			return true;
		}
		simulations.remove(name);
		return true;
	}
	
	@SubCommandExecutor(label = "sim rename", cmdParams = {"sender"})
	public boolean simRename(CommandSender sender, String oldName, String newName) {
		if(!simulations.containsKey(oldName)) {
			return true;
		}
		simulations.put(newName, simulations.remove(oldName));
		return true;
	}
	
	@SubCommandExecutor(label = "sim checkout", cmdParams = {"sender"})
	public boolean simCheckout(CommandSender sender, String name) {
		if(sender instanceof Player p) {
			if(!simulations.containsKey(name)) {
				return true;
			}
			playerStates.put(p.getUniqueId(), simulations.get(name));
		}
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
