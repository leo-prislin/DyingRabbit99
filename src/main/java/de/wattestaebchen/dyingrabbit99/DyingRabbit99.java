package de.wattestaebchen.dyingrabbit99;

import de.wattestaebchen.dyingrabbit99.commands.CordsCommand;
import de.wattestaebchen.dyingrabbit99.files.Config;
import de.wattestaebchen.dyingrabbit99.files.Coordinates;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class DyingRabbit99 extends JavaPlugin {
	
	private static DyingRabbit99 instance;
	
	public static DyingRabbit99 get() { return instance; }
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		instance = this;
		
		new Coordinates();
		
		// Registering Events
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		
		// Registering Commands
		Objects.requireNonNull(getCommand("cords")).setExecutor(new CordsCommand());
		
		// Setup Config
		saveDefaultConfig();
		
		
		DyingRabbit99.sendToConsole("DyingRabbit99 erfolgreich geladen", DyingRabbit99.MessageType.SUCCESS);
		
	}
	
	
	@Override
	public void onDisable() {
		
		saveConfig();
		Coordinates.save();
		
	}
	
	// Only static Methods!
	
	public static void sendToConsole(String message, MessageType type) {
		Bukkit.getConsoleSender().sendRichMessage(type.getPrefix() + message);
	}
	
	public static void sendMessage(CommandSender receiver, String message, MessageType type) {
		receiver.sendRichMessage(type.getPrefix() + message);
	}
	
	public enum MessageType {
		DEFAULT, SUCCESS, ERROR, INFO;
		
		public String getPrefix() {
			return "<light_purple>[DR99] " + switch (this) {
				case DEFAULT -> Config.getDefaultMessageColor();
				case SUCCESS -> Config.getSuccessMessageColor();
				case ERROR -> Config.getErrorMessageColor();
				case INFO -> Config.getInfoMessageColor();
			};
		}
	}
	
}
