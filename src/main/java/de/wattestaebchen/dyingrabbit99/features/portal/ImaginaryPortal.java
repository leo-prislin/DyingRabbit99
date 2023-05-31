package de.wattestaebchen.dyingrabbit99.features.portal;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import org.bukkit.Location;
import org.bukkit.World;

class ImaginaryPortal extends Portal {
	
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
