package de.wattestaebchen.dyingrabbit99.features.config;

import de.wattestaebchen.dyingrabbit99.Cmd;
import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import org.bukkit.command.CommandSender;

public class ConfigCmd extends Cmd {
	
	@CommandExecutor(label = "save")
	public boolean save(CommandSender sender) {
		Config.save();
		Chat.send(
				sender,
				new Text("Config erfolgreich in config.yml gespeichert!", Text.Type.SUCCESS)
		);
		return true;
	}
	
	@CommandExecutor(label = "reload")
	public boolean reload(CommandSender sender) {
		Config.reload();
		Chat.send(sender, new Text(
				"Config erfolgreich aus config.yml geladen! Alle ungespeicherten Änderungen wurden überschrieben.", 
				Text.Type.SUCCESS
		));
		return true;
	}
	
	@CommandExecutor(label = "reset")
	public boolean reset(CommandSender sender) {
		if(Config.reset()) {
			Chat.send(sender, new Text(
					"Config erfolgreich zurückgesetzt!",
					Text.Type.SUCCESS
			));
		}
		else {
			Chat.send(sender, new Text(
					"Beim löschen der alten config.yml ist ein Fehler aufgetreten", Text.Type.ERROR
			));
		}
		return true;
	}
	
	
	
	@CommandExecutor(label = "get printDeathCordsPublic")
	public boolean getPrintDeathCordsPublic(CommandSender sender) {
		Chat.send(sender, new Text(
				Config.getPrintDeathCordsPublic() ?
						"Beim Tod eines Spielers werden alle über die Koordinaten des Todesorts informiert." :
						"Beim Tod eines Spielers wird nur diese über die Koordinaten des Todesorts informiert.",
				Text.Type.DEFAULT
		));
		return true;
	}
	@CommandExecutor(label = "set printDeathCordsPublic")
	public boolean setPrintDeathCordsPublic(CommandSender sender, Boolean value) {
		Config.setPrintDeathCordsPublic(value);
		Chat.send(sender, new Text(
				value ? "Ab sofort werden beim Tod eines Spielers alle über die Koordinaten des Todesorts informiert." :
						"Ab sofort wird beim Tod eines Spielers nur noch dieser über die Koordinaten des Todesorts informiert.",
				Text.Type.DEFAULT
		));
		return true;
	}
	
}
