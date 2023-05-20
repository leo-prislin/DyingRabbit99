package de.wattestaebchen.dyingrabbit99.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.Collection;
import java.util.function.Function;

public class Text {
	
	private Text next = null;
	private final String content;
	private final Chat.Type type;
	
	private final ClickEvent clickEvent;
	
	public Text(String content, Chat.Type type) {
		this.content = content;
		this.type = type;
		this.clickEvent = null;
	}
	public Text(String content, ClickEvent clickEvent) {
		this.content = content;
		this.type = Chat.Type.CLICKABLE;
		this.clickEvent = clickEvent;
	}
	
	// Appending
	public Text nl() {
		return append(new Text("\n", Chat.Type.DEFAULT));
	}
	public Text space() {
		return append(new Text(" ", Chat.Type.DEFAULT));
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
		return append(new Text(defaultText, Chat.Type.DEFAULT));
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
	
}
