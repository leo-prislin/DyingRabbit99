package de.wattestaebchen.dyingrabbit99.features.portal;

import de.wattestaebchen.dyingrabbit99.chat.Text;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

abstract class Portal implements ConfigurationSerializable {
	
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
	
	/*
	@Override
	public @NotNull Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<>();
		if(this instanceof RealPortal rp) {
			map.put("real", true);
			map.put("world", rp.getWorld().getName());
			map.put("location", rp.getLocation());
		}
		else {
			ImaginaryPortal ip = (ImaginaryPortal) this; 
			map.put("real", false);
			map.put("name", ip.getName());
			map.put("location", ip.getLocation());
		}
		return map;
	}
	
	public static Portal deserialize(Map<String, Object> map) {
		try {
			if((boolean)map.get("real")) {
				return new RealPortal(
						Bukkit.getWorld(
								(String) map.get("world")
						).getBlockAt(
								(Location) map.get("location")
						)
				);
			}
			else {
				return new ImaginaryPortal(
						(String) map.get("name"),
						(Location) map.get("location")
				);
			}
		}
		catch(NullPointerException e) {
			return null;
		}
	}
	 */
	
}
