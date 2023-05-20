package de.wattestaebchen.dyingrabbit99.chat;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import de.wattestaebchen.dyingrabbit99.files.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Chat {
	
	private Chat() {}
	
	
	
	public enum Type {
		
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
	
	
	
	
	private static TextComponent build(Text text) {
		return Component.text().content("[DR99] ").color(NamedTextColor.LIGHT_PURPLE).append(text.build()).build();
	}
	public static void sendToConsole(Text text) {
		Bukkit.getConsoleSender().sendMessage(build(text));
	}
	public static void send(CommandSender receiver, Text text) {
		receiver.sendMessage(build(text));
	}
	public static void broadcast(Text text) {
		sendToConsole(text);
		for(Player p : Bukkit.getOnlinePlayers()) {
			send(p, text);
		}
	}
	
	
}
