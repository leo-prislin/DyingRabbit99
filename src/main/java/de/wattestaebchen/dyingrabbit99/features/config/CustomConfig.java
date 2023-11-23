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
	
	protected CustomConfig(String name, String filePath) throws OutdatedConfigException {
		this.name = name;
		this.file = new File(DyingRabbit99.get().getDataFolder(), filePath);
		try {
			if(file.createNewFile()) {
				config = YamlConfiguration.loadConfiguration(file);
				config.set("version", DyingRabbit99.VERSION);
			} else {
				config = YamlConfiguration.loadConfiguration(file);
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		String version = config.getString("version");
		if(version == null) {
			throw new OutdatedConfigException(name, null);
		}
		while(!DyingRabbit99.VERSION.equals(version)) {
			String newVersion = update(version);
			if(newVersion != null) {
				config.set("version", newVersion);
				saveConfig();
			} else {
				throw new OutdatedConfigException(name, version);
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
	
	
	protected abstract String update(String version);
	
}
