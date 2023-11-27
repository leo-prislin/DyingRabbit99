package de.wattestaebchen.dyingrabbit99.features.locations;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import de.wattestaebchen.dyingrabbit99.features.config.CustomConfig;
import de.wattestaebchen.dyingrabbit99.features.config.OutdatedConfigException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public final class LocationsConfig extends CustomConfig {

	private static LocationsConfig instance;
	
	public static void init() throws OutdatedConfigException {
		instance = new LocationsConfig();
		if(!get().isConfigurationSection("locations")) {
			get().createSection("locations");
		}
		save();
	}
	
	public static FileConfiguration get() {
		return instance.getConfig();
	}
	public static void save() {
		instance.saveConfig();
	}
	
	
	public static void setLocation(String name, Location location) {
		get().set("locations." + name, location);
		save();
	}
	public static void removeLocation(String name) {
		get().set("locations." + name, null);
		save();
	}
	public static boolean isSet(String name) {
		return get().isSet("locations." + name);
	}
	public static Location getLocation(String name) {
		return get().getLocation("locations." + name);
	}
	public static boolean renameLocation(String name, String newName) {
		Location loc = get().getLocation("locations." + name);
		if(loc != null) {
			get().set("locations." + name, null);
			get().set("locations." + newName, loc);
			return true;
		}
		else return false;
	}
	public static Set<String> listLocations() {
		try {
			return Objects.requireNonNull(get().getConfigurationSection("locations")).getKeys(false);
		}
		catch(NullPointerException e) {
			Chat.sendToConsole(new Text("Die locations-Config ist fehlerhaft. Bitte überprüfen!", Text.Type.ERROR));
			return new HashSet<>();
		}
	}
	
	
	
	
	private LocationsConfig() throws OutdatedConfigException {
		super("locations", "locations.yml");
	}
	
	@Override
	protected String update(String version) {
		if(version.equals("INDEV-1.1.2") || version.equals("INDEV-1.1.3")) {
			// Nothing to do here
			return "INDEV-1.2.0";
		}
		else if(version.equals("INDEV-1.2.0")) {
			getConfig().createSection("locations");
			for(String key : getConfig().getKeys(false)) {
				Location loc = getConfig().getLocation(key);
				if(loc != null) {
					if(loc.getWorld() == null) {
						List<World> ws = DyingRabbit99.get().getServer().getWorlds();
						if(!ws.isEmpty()) {
							loc.setWorld(ws.get(0));
							Chat.sendToConsole(new Text("Die Welt der location " + key + " wurde geändert. " +
									"Bitte, überprüfe, ob sie nach wie vor stimmt.", Text.Type.INFO));
						} else {
							throw new RuntimeException("Es existiert keine Server-Welt. WTF???");
						}
					}
					getConfig().set(key, null);
					getConfig().set("locations." + key, loc);
				}
			}
			return "INDEV-1.2.1";
		}
		else return null;
	}
}
