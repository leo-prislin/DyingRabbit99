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
		
		String oldVersion = getVersion();
		if(oldVersion == null) {
			throw new OutdatedConfigException(name, null);
		}
		
		while(!DyingRabbit99.VERSION.equals(getVersion())) {
			String newVersion = update(getVersion());
			if(newVersion != null) {
				config.set("version", newVersion);
			} else {
				throw new OutdatedConfigException(name, getVersion());
			}
		}
		saveConfig();
		if(!oldVersion.equals(getVersion())) {
			Chat.sendToConsole(new Text(
					"Die " + name + "-Config wurde von v" + oldVersion + " auf v" + getVersion() + " aktualisiert",
					Text.Type.INFO
			));
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
	
	public String getVersion() {
		try {
			return config.getString("version");
		} catch(NullPointerException e) {
			throw new RuntimeException(
					"Die Methode CustomConfig.getVersion() darf nicht aufgerufen werden," +
							" bevor CustomConfig.config initialisiert wurde.", e
			);
		}
	}
	
	
	protected abstract String update(String version);
	
}
