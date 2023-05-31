package de.wattestaebchen.dyingrabbit99;

import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import de.wattestaebchen.dyingrabbit99.features.config.ConfigCmd;
import de.wattestaebchen.dyingrabbit99.features.find.FindCmd;
import de.wattestaebchen.dyingrabbit99.features.locations.LocationCmd;
import de.wattestaebchen.dyingrabbit99.features.messages.Messages;
import de.wattestaebchen.dyingrabbit99.features.portal.PortalCmd;
import de.wattestaebchen.dyingrabbit99.features.config.Config;
import de.wattestaebchen.dyingrabbit99.features.locations.Locations;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.NoSuchElementException;
import java.util.Objects;

public class DyingRabbit99 extends JavaPlugin {
	
	private static DyingRabbit99 instance;
	
	public static DyingRabbit99 get() { return instance; }
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		instance = this;
		
		new Locations();
		
		// Registering Events
		Bukkit.getPluginManager().registerEvents(new Messages(), this);
		
		// Registering Commands
		Objects.requireNonNull(getCommand("find")).setExecutor(new FindCmd());
		Objects.requireNonNull(getCommand("location")).setExecutor(new LocationCmd());
		Objects.requireNonNull(getCommand("config")).setExecutor(new ConfigCmd());
		Objects.requireNonNull(getCommand("portal")).setExecutor(new PortalCmd());
		
		// Setup Config
		saveDefaultConfig();
		
		
		Chat.sendToConsole(new Text("DyingRabbit99 [INDEV-1.0.3] erfolgreich geladen", Text.Type.SUCCESS));
		
	}
	
	
	@Override
	public void onDisable() {
		
		Config.save();
		Locations.save();
		
	}
	
	
	
	
	public static NamedTextColor getColor(String color) throws NoSuchElementException {
		return NamedTextColor.NAMES.valueOrThrow(color);
	}
	
	public static Location normalizeLocation(Location location) {
		return location.getBlock().getLocation();
	}
	
	
	
}
