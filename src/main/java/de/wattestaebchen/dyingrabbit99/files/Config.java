package de.wattestaebchen.dyingrabbit99.files;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
	
	private Config() {}
	
	private static FileConfiguration get() { return DyingRabbit99.get().getConfig(); }
	
	
	
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
	
	
	public static String getOnPlayerJoinMessage() {
		return get().getString("onPlayerJoinMessage");
	}
	public static boolean getPrintDeathCordsPublic() { return get().getBoolean("printDeathCordsPublic"); }
	
}
