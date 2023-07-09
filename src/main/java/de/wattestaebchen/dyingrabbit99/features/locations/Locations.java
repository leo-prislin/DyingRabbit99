package de.wattestaebchen.dyingrabbit99.features.locations;

import de.wattestaebchen.dyingrabbit99.features.config.CustomConfig;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Set;

public final class Locations extends CustomConfig {

	private static Locations instance;
	/*
	private FileConfiguration config = null;
	private File file;
	 */
	private Locations() {
		super("locations", "locations.yml");
	}
	
	public static void init() {
		instance = new Locations();
	}
	
	/*
	public static FileConfiguration get() {
		if(instance.config == null) {
			if(instance.file == null) {
				instance.file = new File(DyingRabbit99.get().getDataFolder(), "locations.yml");
			}
			instance.config = YamlConfiguration.loadConfiguration(instance.file);
		}
		return instance.config;
	}
	
	public static void save() {
		if(instance.config != null && instance.file != null) {
			try {
				instance.config.save(instance.file);
			} catch (IOException e) {
				Chat.sendToConsole(new Text("Saving locations failed", Text.Type.ERROR));
			}
		}
	}
	 */
	public static FileConfiguration get() {
		return instance.getConfig();
	}
	public static void save() {
		instance.saveConfig();
	}
	
	
	
	public static void setLocation(String name, Location location) {
		get().set(name, location);
	}
	
	public static void removeLocation(String name) {
		get().set(name, null);
	}

	public static boolean isSet(String name) {
		return get().isSet(name);
	}
	
	public static Location getLocation(String name) {
		return get().getLocation(name);
	}
	
	public static Set<String> listLocations() {
		return get().getKeys(false);
	}
	
}
