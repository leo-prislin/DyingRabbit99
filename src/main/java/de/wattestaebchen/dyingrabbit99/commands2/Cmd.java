package de.wattestaebchen.dyingrabbit99.commands2;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public abstract class Cmd implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		
		CommandAnnotation commandAnnotation = getClass().getAnnotation(CommandAnnotation.class);
		if(commandAnnotation == null) {
			return false;
		}
		
		String[] subCommands = commandAnnotation.subCommands();
		if(args.length == 0) {
			if(subCommands.length == 0 || !commandAnnotation.subCommandsRequired()) {
				return execute();
			}
			else {
				return false;
			}			
		}
		
		for(Method m : getClass().getMethods()) {
			SubCommandAnnotation subCommandAnnotation = m.getAnnotation(SubCommandAnnotation.class);
			if(subCommandAnnotation != null && subCommandAnnotation.label().equals(args[0])) {
				return callSubCommand(m, subCommandAnnotation, sender, command, label, args, 0);
			}
		}
		
		return false;
		
	}
	
	private boolean callSubCommand(Method method, SubCommandAnnotation annotation, CommandSender sender, Command command, String commandLabel, String[] args, int argOffset) {
		
		String[] subCommands = annotation.subCommands();
		if(method.getParameterCount() == args.length-argOffset-1) {
			if(subCommands.length == 0 || !annotation.subCommandsRequired()) {
				try {
					Object[] relevantArgs = new Object[method.getParameterCount()];
					System.arraycopy(args, argOffset+1, relevantArgs, 0, relevantArgs.length);
					method.invoke(this, relevantArgs);
					return true;
					// TODO Return method return value
				} catch(IllegalAccessException | InvocationTargetException e) {
					return false;
				}
				catch(IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
			else {
				return false;
			}
		}
		
		if(method.getParameterCount() > args.length-argOffset-1) {
			return false;
		}
		
		
		for(Method m : getClass().getMethods()) {
			SubCommandAnnotation subCommandAnnotation = m.getAnnotation(SubCommandAnnotation.class);
			if(subCommandAnnotation != null && subCommandAnnotation.label().equals(annotation.label() + " " + args[argOffset+1])) {
				return callSubCommand(m, subCommandAnnotation, sender, command, commandLabel, args, argOffset+1);
			}
		}
		
		return false;
		
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		return List.of(); // TODO onTabComplete
	}
	
	/**
	 * This method will be invoked onCommand if no arguments were passed and the command supports a no-argument-execution. If at least one argument was passed, this method won´t be invoked.
	 * @return This method should return if the command was successfully executed.
	 */
	protected abstract boolean execute();
}
