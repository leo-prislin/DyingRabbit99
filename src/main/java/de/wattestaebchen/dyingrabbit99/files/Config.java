package de.wattestaebchen.dyingrabbit99.files;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
	
	public Config() {
		DyingRabbit99.get().saveDefaultConfig();
	}
	
	public static FileConfiguration get() { return DyingRabbit99.get().getConfig(); }
	public static void save() { DyingRabbit99.get().saveConfig(); }
	
}
