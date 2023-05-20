package de.wattestaebchen.dyingrabbit99.commands;

import de.wattestaebchen.dyingrabbit99.Chat;
import de.wattestaebchen.dyingrabbit99.files.Config;
import org.bukkit.command.CommandSender;

public class ConfigCmd extends Cmd {
	
	@SubCommandExecutor(label = "save", cmdParams = {"sender"})
	public boolean save(CommandSender sender) {
		Config.save();
		Chat.send(
				sender,
				new Chat.Text("Config erfolgreich in config.yml gespeichert!", Chat.Type.SUCCESS)
		);
		return true;
	}
	
	@SubCommandExecutor(label = "reload", cmdParams = {"sender"})
	public boolean reload(CommandSender sender) {
		Config.reload();
		Chat.send(sender, new Chat.Text(
				"Config erfolgreich aus config.yml geladen! Alle ungespeicherten Änderungen wurden überschrieben.", 
				Chat.Type.SUCCESS
		));
		return true;
	}
	
	@SubCommandExecutor(label = "reset", cmdParams = {"sender"})
	public boolean reset(CommandSender sender) {
		if(Config.reset()) {
			Chat.send(sender, new Chat.Text(
					"Config erfolgreich zurückgesetzt!",
					Chat.Type.SUCCESS
			));
		}
		else {
			Chat.send(sender, new Chat.Text(
					"Beim löschen der alten config.yml ist ein Fehler aufgetreten", Chat.Type.ERROR
			));
		}
		return true;
	}
	
	
	
	@SubCommandExecutor(label = "get printDeathCordsPublic", cmdParams = {"sender"})
	public boolean getPrintDeathCordsPublic(CommandSender sender) {
		Chat.send(sender, new Chat.Text(
				Config.getPrintDeathCordsPublic() ?
						"Beim Tod eines Spielers werden alle über die Koordinaten des Todesorts informiert." :
						"Beim Tod eines Spielers wird nur diese über die Koordinaten des Todesorts informiert.",
				Chat.Type.DEFAULT
		));
		return true;
	}
	@SubCommandExecutor(label = "set printDeathCordsPublic", cmdParams = {"sender"})
	public boolean setPrintDeathCordsPublic(CommandSender sender, Boolean value) {
		Config.setPrintDeathCordsPublic(value);
		Chat.send(sender, new Chat.Text(
				value ? "Ab sofort werden beim Tod eines Spielers alle über die Koordinaten des Todesorts informiert." :
						"Ab sofort wird beim Tod eines Spielers nur noch dieser über die Koordinaten des Todesorts informiert.",
				Chat.Type.DEFAULT
		));
		return true;
	}
	
}
