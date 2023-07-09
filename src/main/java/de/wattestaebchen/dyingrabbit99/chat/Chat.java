package de.wattestaebchen.dyingrabbit99.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Chat {
	
	private Chat() {}
	
	
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
