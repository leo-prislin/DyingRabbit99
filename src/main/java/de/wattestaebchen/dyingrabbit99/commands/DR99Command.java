package de.wattestaebchen.dyingrabbit99.commands;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class DR99Command implements CommandExecutor {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(getArg0().apply(sender, command, label, args, 0)) {
			return true;
		}
		else {
			DyingRabbit99.sendMessage(sender, Component.text().content("Ungültige Verwendung des Commands").appendNewline().append(usage()).build(), DyingRabbit99.MessageType.ERROR);
			return true;
		}
	}

	public abstract String getUsage();
	public abstract TextComponent usage();
	protected abstract Argument getArg0();
	
	
	
	
	protected abstract static class Argument {
		public abstract Argument getNext();
		public abstract boolean apply(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, int start);
		
		/** Must always return true or the command won't work! */
		public boolean onCall(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) { return true; }
	}
	
	protected abstract static class Text extends Argument {
		public abstract String getText();
		@Override
		public boolean apply(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, int start) {
			Argument next = getNext();
			if(start < args.length && args[start].equalsIgnoreCase(getText())) {
				if(next == null) return (start+1 == args.length) ? onCall(sender, command, label, args) : false;
				else return next.apply(sender, command, label, args, start+1);
			}
			else return false;
		}
	}
	protected abstract static class Option extends Argument {
		public abstract Argument[] getOptions();
		@Override
		public boolean apply(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, int start) {
			Argument[] options = getOptions();
			if(options == null || options.length == 0 || start >= args.length) { return false; }
			
			for (Argument o : options) {
				if(o.apply(sender, command, label, args, start)) return true;
			}
			return false;
		}
		
		@Override
		public Argument getNext() { return null; }
	}
	protected abstract static class Variable extends Argument {
		public enum Type { STRING, INT }
		public abstract Type getType();
		@Override
		public boolean apply(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, int start) {
			Argument next = getNext();
			if(start >= args.length) return false;
			//if((next == null && args.length > start+1) || (next != null && args.length-1 < start)) return false;
			boolean b = switch(getType()) {
				case STRING:
					yield true;
					
				case INT:
					try { Integer.parseInt(args[start]); }
					catch(NumberFormatException ignored) { yield false; }
					yield true;
			};
			if(!b) return false;
			if(next == null) {
				return (args.length == start+1) ? onCall(sender, command, label, args) : false;
			}
			return next.apply(sender, command, label, args, start+1);
		}
	}
	protected abstract static class Optional extends Argument {
		public abstract Argument getInnerArg();
		@Override
		public boolean apply(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, int start) {
			if(start == args.length && !mustBeInvoked(sender, command, label, args)) {
				return onCall(sender, command, label, args);
			}
			else if(start > args.length || !canBeInvoked(sender, command, label, args)) return false;
			return getInnerArg().apply(sender, command, label, args, start);
		}
		@Override
		public Argument getNext() { return null; }
		public boolean canBeInvoked(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) { return true; }
		public boolean mustBeInvoked(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) { return false; }
	}	
	
}
