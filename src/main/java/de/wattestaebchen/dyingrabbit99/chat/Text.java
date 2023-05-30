package de.wattestaebchen.dyingrabbit99.chat;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import de.wattestaebchen.dyingrabbit99.files.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Collection;
import java.util.function.Function;

public class Text {
	
	private Text next = null;
	private final String content;
	private final Type type;
	private TextDecoration[] decorations;
	private final ClickEvent clickEvent;
	
	public static Text newLine() {
		return new Text().nl();
	}
	
	public Text(TextDecoration... decorations) {
		this("", Type.DEFAULT, decorations);
	}
	public Text(String content, Type type, TextDecoration... decorations) {
		this.content = content;
		this.type = type;
		this.decorations = decorations;
		this.clickEvent = null;
	}
	public Text(String content, ClickEvent clickEvent, TextDecoration... decorations) {
		this.content = content;
		this.type = Type.CLICKABLE;
		this.decorations = decorations;
		this.clickEvent = clickEvent;
	}
	
	public Text setDecorations(TextDecoration... decorations) {
		this.decorations = decorations;
		return this;
	}
	
	// Appending
	public Text nl() {
		return append(new Text("\n", Type.DEFAULT));
	}
	public Text indent(int amount) {
		for(int i = 0; i < amount; i++) {
			appendDefault("  ");
		}
		return this;
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
	
	Component build() {
		var builder = Component.text()
				.content(content)
				.color(type.getColor())
				.decorate(decorations);
		if(clickEvent != null) {
			builder.clickEvent(clickEvent);
		}
		
		TextComponent childComponents = children.stream().reduce(Component.empty(), (acc, child) -> acc.append(child.build()), TextComponent::append);
		return Component.join(JoinConfiguration.noSeparators(), builder, childComponents);
	}
	
	public enum Type {
		
		DEFAULT,
		SUCCESS, ERROR, INFO,
		CLICKABLE,
		OVERWORLD, NETHER;
		
		public NamedTextColor getColor() {
			return switch (this) {
				case DEFAULT -> DyingRabbit99.getColor(Config.getDefaultMessageColor());
				case SUCCESS -> DyingRabbit99.getColor(Config.getSuccessMessageColor());
				case ERROR -> DyingRabbit99.getColor(Config.getErrorMessageColor());
				case INFO -> DyingRabbit99.getColor(Config.getInfoMessageColor());
				case CLICKABLE -> DyingRabbit99.getColor(Config.getClickableMessageColor());
				case OVERWORLD -> DyingRabbit99.getColor(Config.getOverworldMessageColor());
				case NETHER -> DyingRabbit99.getColor(Config.getNetherMessageColor());
			};
		}
		
	}
	
}
