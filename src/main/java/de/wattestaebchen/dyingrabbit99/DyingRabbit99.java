package de.wattestaebchen.dyingrabbit99;

import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import de.wattestaebchen.dyingrabbit99.features.config.Config;
import de.wattestaebchen.dyingrabbit99.features.config.ConfigCmd;
import de.wattestaebchen.dyingrabbit99.features.find.FindCmd;
import de.wattestaebchen.dyingrabbit99.features.locations.LocationCmd;
import de.wattestaebchen.dyingrabbit99.features.locations.LocationsConfig;
import de.wattestaebchen.dyingrabbit99.features.messages.Messages;
import de.wattestaebchen.dyingrabbit99.features.portal.PortalCmd;
import de.wattestaebchen.dyingrabbit99.features.portal.PortalsConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.NoSuchElementException;
import java.util.Objects;

public class DyingRabbit99 extends JavaPlugin {
	
	public static final String VERSION = "INDEV-1.2.2";
	
	private static DyingRabbit99 instance;
	public static DyingRabbit99 get() { return instance; }
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		instance = this;
		
		// Registering Events
		Bukkit.getPluginManager().registerEvents(new Messages(), this);
		
		// Registering Commands
		Objects.requireNonNull(getCommand("find")).setExecutor(new FindCmd());
		Objects.requireNonNull(getCommand("location")).setExecutor(new LocationCmd());
		Objects.requireNonNull(getCommand("config")).setExecutor(new ConfigCmd());
		Objects.requireNonNull(getCommand("portal")).setExecutor(new PortalCmd());
		
		// Setup Configs
		Config.init();
		LocationsConfig.init();
		PortalsConfig.init();
		
		Chat.sendToConsole(new Text("DyingRabbit99 [" + VERSION + "] erfolgreich geladen", Text.Type.SUCCESS));
		
	}
	
	public static boolean errorOnEnable = false;
	
	@Override
	public void onDisable() {
		
		if(errorOnEnable) {
			Chat.sendToConsole(new Text("Beim Start von DyingRabbit99 [" + VERSION + "] ist ein Fehler aufgetreten und das Plugin wurde wieder heruntergefahren", Text.Type.ERROR));
			return;
		}
		
		Config.save();
		LocationsConfig.save();
		PortalsConfig.save();
		
		Chat.sendToConsole(new Text("DyingRabbit99 [" + VERSION + "] wurde ordnungsgemäß heruntergefahren", Text.Type.INFO));
		
	}
	
	
	
	
	public static NamedTextColor getColor(String color) throws NoSuchElementException {
		return NamedTextColor.NAMES.valueOrThrow(color);
	}
	
	public static Location normalizeLocation(Location location) {
		return location.getBlock().getLocation();
	}
	
	public static Text.Type enviromentToTextType(World.Environment e) {
		return switch(e) {
			case NORMAL -> Text.Type.OVERWORLD;
			case NETHER -> Text.Type.NETHER;
			case THE_END -> Text.Type.THE_END;
			case CUSTOM -> Text.Type.SPECIAL;
		};
	}
	
	
}
