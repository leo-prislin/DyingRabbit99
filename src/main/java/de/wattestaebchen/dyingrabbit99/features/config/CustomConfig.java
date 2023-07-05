package de.wattestaebchen.dyingrabbit99.features.config;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class CustomConfig {
	
	private FileConfiguration config = null;
	private File file;
	private final String name, filePath;
	
	protected CustomConfig(String name, String filePath) {
		this.name = name;
		this.filePath = filePath;
	}
	
	
	protected FileConfiguration getConfig() {
		if(config == null) {
			if(file == null) {
				file = new File(DyingRabbit99.get().getDataFolder(), filePath);
			}
			config = YamlConfiguration.loadConfiguration(file);
		}
		return config;
	}
	
	protected void saveConfig() {
		if(config != null && file != null) {
			try {
				config.save(file);
			} catch(IOException e) {
				Chat.sendToConsole(new Text("Saving CustomConfig \"" + name + "\" failed", Text.Type.ERROR));
			}
		}
	}
	
}
