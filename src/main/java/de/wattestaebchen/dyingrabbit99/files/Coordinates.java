package de.wattestaebchen.dyingrabbit99.files;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class Coordinates {

	private static Coordinates instance;

	private FileConfiguration config = null;
	private File file;

	public Coordinates() {

		instance = this;

	}

	public static FileConfiguration get() {
		if(instance.config == null) {
			if(instance.file == null) {
				instance.file = new File(DyingRabbit99.get().getDataFolder(), "coordinates.yml");
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
				DyingRabbit99.sendToConsole(Component.text().content("Saving Coordinates failed").build(), DyingRabbit99.MessageType.ERROR);
			}
		}
	}
	
	
	// TODO world name is saved instead of uid
	
	public static void addCords(String name, String world, int x, int y, int z) {
		get().createSection(name, Map.of("world", world, "x", x, "y", y, "z", z));
	}

	public static void removeCords(String name) {
		get().set(name, null);
	}

	public static boolean isSet(String name) {
		return get().isSet(name);
	}

	/** Returns an String[] with { world, x, y, z } or null if there is no entry for the given name. x, y, z are parsable to int. A corrupted coordinates.yml file may result in values being zero. world values: 0 -> overworld, 1 -> nether, 2 -> end */
	public static String[] getCords(String name) {
		ConfigurationSection section = get().getConfigurationSection(name);
		if(section == null) return null;
		return new String[] { section.getString("world"), section.getString("x"), section.getString("y"), section.getString("z") };
	}
	
	public static Set<String> listCords() {
		return get().getKeys(false);
	}
	
}
