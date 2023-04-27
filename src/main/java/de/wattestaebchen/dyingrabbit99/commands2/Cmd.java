package de.wattestaebchen.dyingrabbit99.commands2;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class Cmd implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		
		CommandAnnotation commandAnnotation = getClass().getAnnotation(CommandAnnotation.class);
		if(commandAnnotation == null) {
			throw new RuntimeException("CommandAnnotation missing on Class: " + getClass().getName());
		}
		
		// Execute command directly
		for(Method method : getClass().getMethods()) {
			// Check if method is an execute-method
			if(method.isAnnotationPresent(CommandExecuteAnnotation.class)) {
				// Check if args math with method parameters
				Object[] castArgs = castArgs(method, args, sender, command, label, args);
				if(castArgs != null) {
					try {
						return (boolean) method.invoke(this, castArgs);
					} catch(IllegalAccessException | InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		
		if(args.length == 0) {
			return false;
		}
		
		// Execute subCommands
		// Collect all methods of the passed subCommand
		ArrayList<Method> subCommandMethods = new ArrayList<>();
		for(Method m : getClass().getMethods()) {
			// If method is correctly annotated and label matches the passed subCommand-argument
			SubCommandAnnotation subCommandAnnotation = m.getAnnotation(SubCommandAnnotation.class);
			if(subCommandAnnotation != null && subCommandAnnotation.label().equals(args[0])) {
				subCommandMethods.add(m);
			}
		}
		// If no methods were found, return false
		if(subCommandMethods.size() == 0) {
			return false;
		}
		// SubCommand-methods were found
		else {
			return callSubCommand(subCommandMethods, sender, command, label, args, 1);
		}
		
	}
	
	private boolean callSubCommand(@NotNull ArrayList<Method> methods, @NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, @NotNull String[] args, int argOffset) {
		
		String[] relevantArgs = new String[args.length-argOffset];
		System.arraycopy(args, argOffset, relevantArgs, 0, relevantArgs.length);
		
		for(Method method : methods) {
			
			Object[] castArgs = castArgs(method, relevantArgs, sender, command, commandLabel, args);
			SubCommandAnnotation annotation = method.getAnnotation(SubCommandAnnotation.class);
			
			if((!annotation.subCommandsRequired() || annotation.subCommands().length == 0) && castArgs != null) {
				try {
					return (boolean) (Boolean) method.invoke(this, castArgs);
				} catch(IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
			
			if(args.length == argOffset) {
				return false;
			}
			
			ArrayList<Method> subCommandMethods = new ArrayList<>();
			for(Method m : getClass().getMethods()) {
				SubCommandAnnotation subCommandAnnotation = m.getAnnotation(SubCommandAnnotation.class);
				if(subCommandAnnotation != null && subCommandAnnotation.label().equals(annotation.label() + " " + args[argOffset])) {
					subCommandMethods.add(m);
				}
			}
			if(subCommandMethods.size() != 0 && callSubCommand(subCommandMethods, sender, command, commandLabel, args, argOffset)) {
				return true;
			}
			
		}
		
		return false;
		
	}
	
	/**
	 * Supported Types: String, int, boolean
	 * @return Object array of cast relevantArgs or null if relevantArgs could not be cast
	 */
	private Object[] castArgs(Method method, String[] relevantArgs, CommandSender sender, Command command, String label, String[] args) {
		
		RequiresInfo requiresInfo = method.getAnnotation(RequiresInfo.class);
		String[] requiredInfo = (requiresInfo == null) ? new String[0] : requiresInfo.info();
		Object[] castArgs = new Object[relevantArgs.length + requiredInfo.length];
		
		// Check args length
		if(method.getParameterCount() != castArgs.length) {
			return null;
		}
		
		// Add requiredInfo
		for(int i = 0; i < requiredInfo.length; i++) {
			castArgs[i] = switch(requiredInfo[i]) {
				case "sender" -> sender;
				case "command" -> command;
				case "label" -> label;
				case "relevantArgs" -> args;
				default -> null;
			};
		}
		
		// Add relevantArgs
		Class<?>[] types = method.getParameterTypes();
		for(int i = 0; i < relevantArgs.length; i++) {
			// Type String
			if(types[i].equals(String.class)) {
				castArgs[requiredInfo.length+i] = relevantArgs[i];
			}
			// Type Integer
			if(types[i].equals(Integer.class)) {
				try { castArgs[requiredInfo.length+i] = Integer.parseInt(relevantArgs[i]); }
				catch(NumberFormatException ignored) { return null; }
			}
			// Type Boolean
			if(types[i].equals(Boolean.class)) {
				boolean returnNull = false;
				castArgs[requiredInfo.length+i] = switch(relevantArgs[i]) {
					case "yes", "t", "true", "j", "ja", "y" -> true;
					case "no", "f", "false", "nein", "n" -> false;
					default -> {
						returnNull = true;
						yield false;
					}
				};
				if(returnNull) return null;
			}
		}
		return castArgs;
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		return List.of(); // TODO onTabComplete
	}
	
}
