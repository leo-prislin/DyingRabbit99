package de.wattestaebchen.dyingrabbit99;

import de.wattestaebchen.dyingrabbit99.files.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.function.Function;

public class Chat {
	
	
	public static class Text {
		
		private Text next = null;
		private final String content;
		private final Type type;
		
		private final ClickEvent clickEvent;
		
		public Text(String content, Type type) {
			this.content = content;
			this.type = type;
			this.clickEvent = null;
		}
		public Text(String content, ClickEvent clickEvent) {
			this.content = content;
			this.type = Type.CLICKABLE;
			this.clickEvent = clickEvent;
		}
		
		// Appending
		public Text nl() {
			return append(new Text("\n", Type.DEFAULT));
		}
		public Text space() {
			return append(new Text(" ", Type.DEFAULT));
		}
		public Text append(Text text) {
			if(next == null) {
				next = text;
			}
			else {
				next.append(text);
			}
			return this;
		}
		public <T> Text appendCollection(Collection<T> collection, Function<T, Text> appendElement) {
			for(T element : collection) {
				append(appendElement.apply(element));
			}
			return this;
		}
		public Text appendDefault(String defaultText) {
			return append(new Text(defaultText, Type.DEFAULT));
		}
		
		
		private TextComponent build() {
			TextComponent.Builder builder = Component.text().content(content).color(type.getColor());
			
			if(clickEvent != null) {
				builder.clickEvent(clickEvent);
			}
			
			if(next != null) {
				builder.append(next.build());
			}
			
			return builder.build();
		}
		
	}
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
