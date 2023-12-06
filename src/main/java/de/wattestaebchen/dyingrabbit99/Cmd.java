package de.wattestaebchen.dyingrabbit99;

import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
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
	
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command ignored0, @NotNull String ignored1, @NotNull String[] args) {
		
		M: for(Method method : getClass().getMethods()) {
			
			CommandExecutor commandExecutor = method.getAnnotation(CommandExecutor.class);
			if(commandExecutor == null) continue M;
			
			String label = commandExecutor.label();
			System.out.println("Label : \"" + label + "\"");
			Arrays.stream(args).forEach(System.out::println);
			String[] labelSplits = label.isEmpty() ? new String[0] : label.split(" ");
			System.out.println(labelSplits.length + "  " + args.length);
			if(labelSplits.length > args.length) continue M;
			for(int i = 0; i < labelSplits.length; i++) {
				if(!labelSplits[i].equals(args[i])) {
					continue M;
				}
			}
			
			String[] params = new String[args.length - labelSplits.length];
			System.arraycopy(args, labelSplits.length, params, 0, params.length);
			
			Object[] castParams = castParams(method, sender, params);
			if(castParams == null) continue M;
			
			if(commandExecutor.playerOnly()) {
				if(sender instanceof Player p) {
					castParams[0] = p;
				} else {
					Chat.send(sender, new Text("Dieser Befehl ist nur für Spieler verfügbar.", Text.Type.ERROR));
					return true;
				}
			}
			
			try {
				return (boolean) method.invoke(this, castParams);
			} catch(IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			
		}
		
		return false;
		
	}
	
	/**
	 * Supported Types: String, Integer, Boolean
	 * @return Object array of cast parameters or null if parameters could not be cast
	 */
	private Object[] castParams(Method method, CommandSender sender, String[] params) {
		Class<?>[] types = method.getParameterTypes();
		if(types.length != params.length+1 || types[0] != CommandSender.class) return null;
		
		List<Object> list = new ArrayList<>();
		list.add(sender);
		
		for(int i = 0; i < params.length; i++) {
			if(types[i+1] == String.class) {
				list.add(params[i]);
			} else if(types[i+1] == Integer.class) {
				try { list.add(Integer.parseInt(params[i])); }
				catch(NumberFormatException e) { return null; }
			} else if(types[i+1] == Boolean.class) {
				switch(params[i]) {
					case "yes", "y", "ja", "j", "true", "t" -> list.add(true);
					case "no", "n", "nein", "false", "f" -> list.add(false);
					default -> { return null; }
				}
			} else {
				throw new RuntimeException("Commands can only accept String, Integer or Boolean parameters. There´s an error in the Command-Class!");
			}
		}
		
		return list.toArray();
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		
		if(args.length == 0) return List.of();
		
		ArrayList<String> completions = new ArrayList<>();
		for(Method method : Arrays.stream(getClass().getMethods()).filter(m -> m.isAnnotationPresent(CommandExecutor.class)).toList()) {
			
			String[] parameters = method.getAnnotation(CommandExecutor.class).label().split(" ");
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
	protected @interface CommandExecutor {
		String label() default "";
		boolean playerOnly() default false;
	}
	
}
