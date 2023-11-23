package de.wattestaebchen.dyingrabbit99.features.config;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class Config {
	
	private Config() {}
	
	public static void init() throws OutdatedConfigException {
		DyingRabbit99.get().saveDefaultConfig();
		
		String version = getVersion();
		if(version == null) {
			throw new OutdatedConfigException(null, null);
		}
		
		while(!DyingRabbit99.VERSION.equals(version)) {
			String newVersion = update(version);
			if(newVersion != null) {
				get().set("version", newVersion);
				save();
			} else {
				throw new OutdatedConfigException(null, version);
			}
		}
	}
	
	private static FileConfiguration get() { return DyingRabbit99.get().getConfig(); }
	public static void save() { DyingRabbit99.get().saveConfig(); }
	public static void reload() { DyingRabbit99.get().reloadConfig(); }
	public static boolean reset() {
		File file = new File(DyingRabbit99.get().getDataFolder(), "config.yml");
		if(file.delete()) {
			DyingRabbit99.get().saveDefaultConfig();
			reload();
			return true;
		}
		return false;
	}
	
	
	public static String getVersion() { return get().getString("version"); }
	
	public static String getDefaultMessageColor() {
		return get().getString("messageColors.default");
	}
	public static String getSuccessMessageColor() {
		return get().getString("messageColors.success");
	}
	public static String getErrorMessageColor() {
		return get().getString("messageColors.error");
	}
	public static String getInfoMessageColor() {
		return get().getString("messageColors.info");
	}
	public static String getClickableMessageColor() {
		return get().getString("messageColors.clickable");
	}
	public static String getOverworldMessageColor() {
		return get().getString("messageColors.overworld");
	}
	public static String getNetherMessageColor() {
		return get().getString("messageColors.nether");
	}
	
	
	public static String getOnPlayerJoinMessage() {
		return get().getString("onPlayerJoinMessage");
	}
	public static boolean getPrintDeathCordsPublic() { return get().getBoolean("printDeathCordsPublic"); }
	public static void setPrintDeathCordsPublic(boolean value) { get().set("printDeathCordsPublic", value); }
	
	
	
	
	private static String update(String version) {
		if(version.equals("INDEV-1.1.2")) {
			// Nothing to do here
			return "INDEV-1.1.3";
		}
		return null;
	}
	
}
