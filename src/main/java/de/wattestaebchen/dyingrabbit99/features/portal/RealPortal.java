package de.wattestaebchen.dyingrabbit99.features.portal;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

class RealPortal extends Portal {
	
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
