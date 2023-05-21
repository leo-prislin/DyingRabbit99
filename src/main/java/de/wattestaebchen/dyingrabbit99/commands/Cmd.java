package de.wattestaebchen.dyingrabbit99.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Cmd implements CommandExecutor, TabCompleter {
	
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		
		// Execute Command (no SubCommands)
		for(Method method : getClass().getMethods()) {
			if(!method.isAnnotationPresent(CommandExecutor.class)) continue;
			
			// Check if entered args match the required parameters
			Object[] castArgs = castArgs(method, args, sender, command, label, args);
			if(castArgs == null) continue;
			
			try {
				return (boolean) method.invoke(this, castArgs);
			} catch(IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		
		if(args.length == 0) return false;
		
		// Execute SubCommands
		for(Method method : getClass().getMethods()) {
			SubCommandExecutor subCommandExecutor = method.getAnnotation(SubCommandExecutor.class);
			if(subCommandExecutor == null) continue;
			
			// Check if entered args match the subCommand label
			String argString = String.join(" ", args);
			if(!argString.startsWith(subCommandExecutor.label())) continue;
			
			// Remove the label from args to get an array of method parameters
			int labelLength = subCommandExecutor.label().split(" ").length;
			String[] relevantArgs = new String[args.length - labelLength];
			System.arraycopy(args, labelLength, relevantArgs, 0, relevantArgs.length);
			
			// Check if entered args match the required parameters
			Object[] castArgs = castArgs(method, relevantArgs, sender, command, label, args);
			if(castArgs == null) continue;
			
			try {
				Arrays.stream(castArgs).forEach(System.out::println);
				return (boolean) method.invoke(this, castArgs);
			} catch(IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		
		return false;
		
	}
	
	
	
	/**
	 * Supported Types: String, int, boolean
	 * @return Object array of cast relevantArgs or null if relevantArgs could not be cast
	 */
	private Object[] castArgs(Method method, String[] relevantArgs, CommandSender sender, Command command, String label, String[] args) {
		
		CommandExecutor commandExecutor = method.getAnnotation(CommandExecutor.class);
		SubCommandExecutor subCommandExecutor = method.getAnnotation(SubCommandExecutor.class);
		
		String[] cmdParams = (commandExecutor != null) ?
			commandExecutor.cmdParams() :
			subCommandExecutor.cmdParams();
		Object[] castArgs = new Object[cmdParams.length + relevantArgs.length];
		
		// Check args length
		if(method.getParameterCount() != castArgs.length) return null;
		
		// Add cmdParams
		for(int i = 0; i < cmdParams.length; i++) {
			castArgs[i] = switch(cmdParams[i]) {
				case "sender" -> sender;
				case "command" -> command;
				case "label" -> label;
				case "relevantArgs" -> args;
				default -> null;
			};
		}
		
		// Add relevantArgs
		Class<?>[] types = method.getParameterTypes();
		for(int i = cmdParams.length; i < castArgs.length; i++) {
			// Type String
			if(types[i].equals(String.class)) {
				castArgs[i] = relevantArgs[i-cmdParams.length];
			}
			// Type Integer
			if(types[i].equals(Integer.class)) {
				try { castArgs[i] = Integer.parseInt(relevantArgs[i- cmdParams.length]); }
				catch(NumberFormatException ignored) { return null; }
			}
			// Type Boolean
			if(types[i].equals(Boolean.class)) {
				boolean returnNull = false;
				castArgs[i] = switch(relevantArgs[i-cmdParams.length]) {
					case "yes", "y", "ja", "j", "true", "t" -> true;
					case "no", "n", "nein", "false", "f" -> false;
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
		
		if(args.length == 0) return List.of();
		
		ArrayList<String> completions = new ArrayList<>();
		for(Method method : Arrays.stream(getClass().getMethods()).filter(m -> m.isAnnotationPresent(SubCommandExecutor.class)).toList()) {
			
			String[] parameters = method.getAnnotation(SubCommandExecutor.class).label().split(" ");
			if(parameters.length < args.length) continue;
			
			String[] relevantParameters = new String[args.length];
			System.arraycopy(parameters, 0, relevantParameters, 0, relevantParameters.length);
			
			if(String.join(" ", relevantParameters).startsWith(String.join(" ", args))) {
				completions.add(relevantParameters[relevantParameters.length-1]);
			}
			
		}
		return completions;
		
	}
	
	
	/** Method must return a boolean! */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface CommandExecutor {
		/**
		 * cmdParams() Defines the additional parameters from onCommand that should be passed to this method.
		 * For every entry in cmdParams(), there must be a corresponding method-parameter.
		 * These parameters have to be the first method-parameters and need to be in the same order as in cmdParams().
		 * Possible values are: sender, command, label, args.
		 */
		String[] cmdParams() default {};
	}
	/** Method must return a boolean! */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface SubCommandExecutor {
		String label();
		/**
		 * cmdParams() defines the additional parameters from onCommand that should be passed to this method.
		 * For every entry in cmdParams(), there must be a corresponding method-parameter.
		 * These parameters have to be the first method-parameters and need to be in the same order as in cmdParams().
		 * Possible values are: sender, command, label, args.
		 */
		String[] cmdParams() default {};
	}
	
}
