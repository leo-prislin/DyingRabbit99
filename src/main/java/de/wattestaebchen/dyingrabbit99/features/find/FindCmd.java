package de.wattestaebchen.dyingrabbit99.features.find;

import de.wattestaebchen.dyingrabbit99.Cmd;
import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class FindCmd extends Cmd {
	
	@CommandExecutor(cmdParams = {"sender"})
	public boolean execute(CommandSender sender, String playerName) {
		Player p = Bukkit.getPlayer(playerName);
		if(p == null) {
			Chat.send(sender, new Text("Der Spieler " + playerName + " wurde nicht gefunden.", Text.Type.ERROR));
		}
		else {
			Location loc = p.getLocation();
			Chat.send(sender, new Text(
					"Der Spieler " + playerName + " befindet sich gerade an den Koordinaten:" +
							"\nworld: " + loc.getWorld().getEnvironment() +
							", x: " + loc.getBlockX() +
							", y: " + loc.getBlockY() +
							", z: " + loc.getBlockZ(),
					Text.Type.DEFAULT
			));
		}
		return true;
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		return switch (args.length) {
			case 0 -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
			case 1 -> Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.startsWith(args[0])).collect(Collectors.toList());
			default -> List.of();
		};
	}
}
