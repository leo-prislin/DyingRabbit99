package de.wattestaebchen.dyingrabbit99.chat;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import de.wattestaebchen.dyingrabbit99.files.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collection;
import java.util.function.Function;

public class Text {
	
	private Text next = null;
	private final String content;
	private final Type type;
	
	private final ClickEvent clickEvent;
	
	public Text() {
		this("", Type.DEFAULT);
	}
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
	public Text tab() {
		return append(new Text("	", Type.DEFAULT));
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
	public <T> Text appendCollection(Collection<T> collection, boolean seperateByNewLine, Function<T, Text> appendElement) {
		for(T element : collection) {
			if(seperateByNewLine) nl().append(appendElement.apply(element));
			else append(appendElement.apply(element));
		}
		return this;
	}
	public Text appendDefault(String defaultText) {
		return append(new Text(defaultText, Type.DEFAULT));
	}
	
	
	TextComponent build() {
		TextComponent.Builder builder = Component.text().content(content).color(type.getColor());
		
		if(clickEvent != null) {
			builder.clickEvent(clickEvent);
		}
		
		if(next != null) {
			builder.append(next.build());
		}
		
		return builder.build();
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
}
