package de.wattestaebchen.dyingrabbit99.features.config;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import de.wattestaebchen.dyingrabbit99.features.menu.Menu;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class Config {
	
	private Config() {}
	
	public static void init() throws OutdatedConfigException {
		DyingRabbit99.get().saveDefaultConfig();
		
		if(getVersion() == null) {
			throw new OutdatedConfigException(null, null);
		}
		
		String oldVersion = getVersion();
		while(!DyingRabbit99.VERSION.equals(getVersion())) {
			String newVersion = update(getVersion());
			if(newVersion != null) {
				get().set("version", newVersion);
			} else {
				throw new OutdatedConfigException(null, getVersion());
			}
		}
		save();
		if(!oldVersion.equals(getVersion())) {
			Chat.sendToConsole(new Text("Die Config wurde von v" + oldVersion + " auf v" + getVersion() + " aktualisiert", Text.Type.INFO));
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
	public static String getSpecialMessageColor() {
		return get().getString("messageColors.special");
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
	public static String getOverworldMessageColor() {
		return get().getString("messageColors.overworld");
	}
	public static String getNetherMessageColor() {
		return get().getString("messageColors.nether");
	}
	public static String getTheEndMessageColor() { return get().getString("messageColors.the_end"); }
	
	
	public static String getOnPlayerJoinMessage() {
		return get().getString("onPlayerJoinMessage");
	}
	public static boolean getPrintDeathCordsPublic() { return get().getBoolean("printDeathCordsPublic"); }
	public static void setPrintDeathCordsPublic(boolean value) { get().set("printDeathCordsPublic", value); }
	
	
	
	
	private static String update(String version) {
		if(version.equals("INDEV-1.1.2") || version.equals("INDEV-1.1.3")) {
			// Nothing to do here
			return "INDEV-1.2.0";
		}
		else if(version.equals("INDEV-1.2.0")) {
			get().set("messageColors.the_end", "dark_blue");
			get().set("messageColors.special", "gold");
			get().set("messageColors.clickable", null);
			return "INDEV-1.2.1";
		}
		else if(version.equals("INDEV-1.2.1")) {
			get().set("messageColors", null);
			return "INDEV-1.2.2";
		}
		else return null;
	}
	
	
	public static Menu getMenu() {
		return new Menu(3, "DyingRabbit99 - Config", true)
				.setChildMenu(9+4, Material.TOTEM_OF_UNDYING, "Todesort", new Menu(3, "DR99 - Config - Todesort", true)
						.setEntry(9+3, Material.GREEN_WOOL, "öffentlich", event -> setPrintDeathCordsPublic(true))
						.setEntry(9+5, Material.RED_WOOL, "privat", event -> setPrintDeathCordsPublic(false))
				);
	}
	
}
