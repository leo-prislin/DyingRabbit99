package de.wattestaebchen.dyingrabbit99.features.config;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class CustomConfig {
	
	private final YamlConfiguration config;
	private final File file;
	private final String name;
	
	protected CustomConfig(String name, String filePath) throws OutdatedVersionException {
		this.name = name;
		this.file = new File(DyingRabbit99.get().getDataFolder(), filePath);
		this.config = YamlConfiguration.loadConfiguration(file);
		this.config.set("version", DyingRabbit99.VERSION);
		
		String version = config.getString("version");
		if(!DyingRabbit99.VERSION.equals(version)) {
			if(version != null && update(version)) {
				config.set("version", DyingRabbit99.VERSION);
				saveConfig();
			} else {
				throw new OutdatedVersionException(name, version);
			}
		}
	}
	
	
	protected YamlConfiguration getConfig() {
		return config;
	}
	
	protected void saveConfig() {
		try {
			config.save(file);
		} catch(IOException e) {
			Chat.sendToConsole(new Text("Saving CustomConfig \"" + name + "\" failed", Text.Type.ERROR));
		}
	}
	
	
	protected abstract boolean update(String version);
	
}
