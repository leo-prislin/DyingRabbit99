package de.wattestaebchen.dyingrabbit99;

import de.wattestaebchen.dyingrabbit99.commands.ConfigCmd;
import de.wattestaebchen.dyingrabbit99.commands.FindCmd;
import de.wattestaebchen.dyingrabbit99.commands.LocationCmd;
import de.wattestaebchen.dyingrabbit99.files.Config;
import de.wattestaebchen.dyingrabbit99.files.Locations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		
		// Registering Commands
		Objects.requireNonNull(getCommand("find")).setExecutor(new FindCmd());
		Objects.requireNonNull(getCommand("location")).setExecutor(new LocationCmd());
		Objects.requireNonNull(getCommand("config")).setExecutor(new ConfigCmd());
		
		// Setup Config
		saveDefaultConfig();
		
		
		DyingRabbit99.sendToConsole(Component.text().content("DyingRabbit99 [INDEV-1.1.0] erfolgreich geladen").build(), DyingRabbit99.MessageType.SUCCESS);
		
	}
	
	
	@Override
	public void onDisable() {
		
		Config.save();
		Locations.save();
		
	}
	
	// Only static Methods!
	
	public static void sendToConsole(TextComponent message, MessageType type) {
		if(type == null) {
			Bukkit.getConsoleSender().sendMessage(
					Component.text().content("[DR99] ").color(NamedTextColor.LIGHT_PURPLE).append(message)
			);
		}
		else {
			Bukkit.getConsoleSender().sendMessage(
					Component.text().content("[DR99] ").color(NamedTextColor.LIGHT_PURPLE).append(message.color(type.getColor()))
			);
		}
	}
	public static void sendMessage(CommandSender receiver, TextComponent message, MessageType type) {
		if(type == null) {
			receiver.sendMessage(
					Component.text().content("[DR99] ").color(NamedTextColor.LIGHT_PURPLE).append(message)
			);
		}
		else {
			receiver.sendMessage(
					Component.text().content("[DR99] ").color(NamedTextColor.LIGHT_PURPLE).append(message.color(type.getColor()))
			);
		}
	}
	public static void broadcastMessage(TextComponent message, MessageType type) {
		sendToConsole(message, type);
		for(Player p : Bukkit.getOnlinePlayers()) {
			sendMessage(p, message, type);
		}
	}
	
	public enum MessageType {
		DEFAULT, SUCCESS, ERROR, INFO, CLICKABLE;
		
		public NamedTextColor getColor() {
			return switch (this) {
				case DEFAULT -> DyingRabbit99.getColor(Config.getDefaultMessageColor());
				case SUCCESS -> DyingRabbit99.getColor(Config.getSuccessMessageColor());
				case ERROR -> DyingRabbit99.getColor(Config.getErrorMessageColor());
				case INFO -> DyingRabbit99.getColor(Config.getInfoMessageColor());
				case CLICKABLE -> DyingRabbit99.getColor(Config.getClickableMessageColor());
			};
		}
	}
	
	public static NamedTextColor getColor(String color) throws NoSuchElementException {
		return NamedTextColor.NAMES.valueOrThrow(color);
	}
	
	
	
}
