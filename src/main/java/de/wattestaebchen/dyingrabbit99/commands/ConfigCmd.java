package de.wattestaebchen.dyingrabbit99.commands;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import de.wattestaebchen.dyingrabbit99.files.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class ConfigCmd extends Cmd {
	
	@SubCommandExecutor(label = "save", cmdParams = {"sender"})
	public boolean save(CommandSender sender) {
		Config.save();
		DyingRabbit99.sendMessage(
				sender,
				Component.text().content("Config erfolgreich in config.yml gespeichert!").build(),
				DyingRabbit99.MessageType.SUCCESS
		);
		return true;
	}
	
	@SubCommandExecutor(label = "reload", cmdParams = {"sender"})
	public boolean reload(CommandSender sender) {
		Config.reload();
		DyingRabbit99.sendMessage(
				sender,
				Component.text().content("Config erfolgreich aus config.yml geladen! Alle ungespeicherten Änderungen wurden überschrieben.").build(),
				DyingRabbit99.MessageType.SUCCESS
		);
		return true;
	}
	
	@SubCommandExecutor(label = "reset", cmdParams = {"sender"})
	public boolean reset(CommandSender sender) {
		if(Config.reset()) {
			DyingRabbit99.sendMessage(
					sender,
					Component.text().content("Config erfolgreich zurückgesetzt!").build(),
					DyingRabbit99.MessageType.SUCCESS
			);
		}
		else {
			DyingRabbit99.sendMessage(
					sender,
					Component.text().content("Beim löschen der alten config.yml ist ein Fehler aufgetreten").build(),
					DyingRabbit99.MessageType.ERROR
			);
		}
		return true;
	}
	
	
	
	@SubCommandExecutor(label = "get printDeathCordsPublic", cmdParams = {"sender"})
	public boolean getPrintDeathCordsPublic(CommandSender sender) {
		String s = Config.getPrintDeathCordsPublic() ?
				"Beim Tod eines Spielers werden alle über die Koordinaten des Todesorts informiert." :
				"Beim Tod eines Spielers wird nur diese über die Koordinaten des Todesorts informiert.";
		DyingRabbit99.sendMessage(
				sender,
				Component.text().content(s).build(),
				DyingRabbit99.MessageType.DEFAULT
		);
		return true;
	}
	@SubCommandExecutor(label = "set printDeathCordsPublic", cmdParams = {"sender"})
	public boolean setPrintDeathCordsPublic(CommandSender sender, Boolean value) {
		Config.setPrintDeathCordsPublic(value);
		String s = value ?
				"Ab sofort werden beim Tod eines Spielers alle über die Koordinaten des Todesorts informiert." :
				"Ab sofort wird beim Tod eines Spielers nur noch dieser über die Koordinaten des Todesorts informiert.";
		DyingRabbit99.sendMessage(
				sender,
				Component.text().content(s).build(),
				DyingRabbit99.MessageType.DEFAULT
		);
		return true;
	}
	
}
