package de.wattestaebchen.dyingrabbit99.features.menu;

import de.wattestaebchen.dyingrabbit99.Cmd;
import org.bukkit.entity.Player;

public class MenuCmd extends Cmd {
	
	@CommandExecutor(playerOnly = true)
	public boolean execute(Player p) {
		
		Menu menu = Menu.getMainMenu();
		menu.open(p);
		return true;
		
	}
	
}
