package de.wattestaebchen.dyingrabbit99.files;

import de.wattestaebchen.dyingrabbit99.Chat;
import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class Locations {

	private static Locations instance;

	private FileConfiguration config = null;
	private File file;

	public Locations() {

		instance = this;

	}

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
				Chat.sendToConsole(new Chat.Text("Saving locations failed", Chat.Type.ERROR));
			}
		}
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
