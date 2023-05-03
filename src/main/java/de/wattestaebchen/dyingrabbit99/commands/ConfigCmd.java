package de.wattestaebchen.dyingrabbit99.commands;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import de.wattestaebchen.dyingrabbit99.files.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.awt.*;

@Cmd.CommandAnnotation(labels = "config")
public class ConfigCmd extends Cmd {
	
	@SubCommandAnnotation(label = "reset")
	public boolean reset() {
		return false;
	}
	
	@SubCommandAnnotation(label = "save")
	public boolean save() {
		return false;
	}
	
	@SubCommandAnnotation(label = "load")
	public boolean load() {
		return false;
	}
	
	
	@SubCommandAnnotation(label = "set")
	public boolean set() {
		return false;
	}
	
	@SubCommandAnnotation(label = "set printDeathCordsPublic")
	@RequiresInfo(info = {"sender"})
	public boolean setPrintDeathCordsPublic(CommandSender sender, boolean value) {
		Object oldValue = Config.getPrintDeathCordsPublic();
		Config.setPrintDeathCordsPublic(value);
		String s = value ?
				"Ab sofort werden beim Tod eines Spielers alle über die Koordinaten des Todesorts benachrichtigt." :
				"Ab sofort wird beim Tod eines Spielers nur noch dieser über die Koordinaten des Todesorts benachrichtigt.";
		DyingRabbit99.sendMessage(
				sender,
				Component.text().content(s).build(),
				DyingRabbit99.MessageType.DEFAULT
		);
		return true;
	}
	
}
