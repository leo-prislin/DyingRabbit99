package de.wattestaebchen.dyingrabbit99;

import com.destroystokyo.paper.profile.PlayerProfile;
import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import de.wattestaebchen.dyingrabbit99.features.config.Config;
import de.wattestaebchen.dyingrabbit99.features.config.ConfigCmd;
import de.wattestaebchen.dyingrabbit99.features.find.FindCmd;
import de.wattestaebchen.dyingrabbit99.features.locations.LocationCmd;
import de.wattestaebchen.dyingrabbit99.features.locations.LocationsConfig;
import de.wattestaebchen.dyingrabbit99.features.menu.MenuCmd;
import de.wattestaebchen.dyingrabbit99.features.messages.Messages;
import de.wattestaebchen.dyingrabbit99.features.portal.PortalCmd;
import de.wattestaebchen.dyingrabbit99.features.portal.PortalsConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.NoSuchElementException;
import java.util.Objects;

public class DyingRabbit99 extends JavaPlugin {
	
	public static final String VERSION = "INDEV-1.2.2";
	
	private static DyingRabbit99 instance;
	public static DyingRabbit99 get() { return instance; }
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		instance = this;
		
		// Registering Events
		registerEvent(new Messages());
		
		// Registering Commands
		registerCommand("find", new FindCmd());
		registerCommand("location", new LocationCmd());
		registerCommand("config", new ConfigCmd());
		registerCommand("portal", new PortalCmd());
		registerCommand("menu", new MenuCmd());
		
		// Setup Configs
		Config.init();
		LocationsConfig.init();
		PortalsConfig.init();
		
		Chat.sendToConsole(new Text("DyingRabbit99 [" + VERSION + "] erfolgreich geladen", Text.Type.SUCCESS));
		
	}
	
	public static boolean errorOnEnable = false;
	
	@Override
	public void onDisable() {
		
		if(errorOnEnable) {
			Chat.sendToConsole(new Text("Beim Start von DyingRabbit99 [" + VERSION + "] ist ein Fehler aufgetreten und das Plugin wurde wieder heruntergefahren", Text.Type.ERROR));
			return;
		}
		
		Config.save();
		LocationsConfig.save();
		PortalsConfig.save();
		
		Chat.sendToConsole(new Text("DyingRabbit99 [" + VERSION + "] wurde ordnungsgemäß heruntergefahren", Text.Type.INFO));
		
	}
	
	
	public static void registerEvent(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, get());
	}
	public static void registerCommand(String name, Cmd command) {
		Objects.requireNonNull(get().getCommand(name)).setExecutor(command);
	}
	
	
	
	public static NamedTextColor getColor(String color) throws NoSuchElementException {
		return NamedTextColor.NAMES.valueOrThrow(color);
	}
	public static Material colorToMaterial(String color) throws NoSuchElementException {
		if(color.equalsIgnoreCase("black")) { return Material.BLACK_STAINED_GLASS; }
		else if(color.equalsIgnoreCase("white")) { return Material.WHITE_STAINED_GLASS; }
		else if(color.equalsIgnoreCase("dark_aqua")) { return Material.CYAN_STAINED_GLASS; }
		else if(color.equalsIgnoreCase("dark_blue")) { return Material.BLUE_STAINED_GLASS; }
		else if(color.equalsIgnoreCase("dark_gray")) { return Material.GRAY_STAINED_GLASS; }
		else if(color.equalsIgnoreCase("dark_green")) { return Material.GREEN_STAINED_GLASS; }
		else if(color.equalsIgnoreCase("dark_purple")) { return Material.PURPLE_STAINED_GLASS; }
		else if(color.equalsIgnoreCase("dark_red")) { return Material.RED_STAINED_GLASS_PANE; }
		else if(color.equalsIgnoreCase("gold")) { return Material.ORANGE_STAINED_GLASS; }
		else if(color.equalsIgnoreCase("aqua")) { return Material.CYAN_STAINED_GLASS; }
		else if(color.equalsIgnoreCase("blue")) { return Material.LIGHT_BLUE_STAINED_GLASS; }
		else if(color.equalsIgnoreCase("gray")) { return Material.LIGHT_GRAY_STAINED_GLASS; }
		else if(color.equalsIgnoreCase("green")) { return Material.LIME_STAINED_GLASS; }
		else if(color.equalsIgnoreCase("light_purple")) { return Material.MAGENTA_STAINED_GLASS; }
		else if(color.equalsIgnoreCase("red")) { return Material.PINK_STAINED_GLASS; }
		else if(color.equalsIgnoreCase("yellow")) { return Material.YELLOW_STAINED_GLASS; }
		else throw new NoSuchElementException("This color doesn't exist");		
	}
	
	public static Location normalizeLocation(Location location) {
		return location.getBlock().getLocation();
	}
	
	public static Text.Type enviromentToTextType(World.Environment e) {
		return switch(e) {
			case NORMAL -> Text.Type.OVERWORLD;
			case NETHER -> Text.Type.NETHER;
			case THE_END -> Text.Type.THE_END;
			case CUSTOM -> Text.Type.SPECIAL;
		};
	}
	
	public static ItemStack getPlayerHead(Player p) {
		PlayerProfile profile = p.getPlayerProfile();
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		head.editMeta(meta -> {
			((SkullMeta) meta).setPlayerProfile(profile);
			meta.displayName(p.displayName());
		});
		return head;
	}
	
}
