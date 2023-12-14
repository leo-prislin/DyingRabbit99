package de.wattestaebchen.dyingrabbit99.features.locations;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import de.wattestaebchen.dyingrabbit99.chat.Chat;
import de.wattestaebchen.dyingrabbit99.chat.Text;
import de.wattestaebchen.dyingrabbit99.features.config.CustomConfig;
import de.wattestaebchen.dyingrabbit99.features.config.OutdatedConfigException;
import de.wattestaebchen.dyingrabbit99.features.menu.Menu;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class LocationsConfig extends CustomConfig {

	private static LocationsConfig instance;
	
	public static void init() throws OutdatedConfigException {
		instance = new LocationsConfig();
		if(!get().isConfigurationSection("locations")) {
			get().createSection("locations");
		}
		save();
	}
	
	public static FileConfiguration get() {
		return instance.getConfig();
	}
	public static void save() {
		instance.saveConfig();
	}
	
	public static Menu getMenu() {
		List<String> keys = listLocations();
		
		return new Menu(3, "Locations", true)
				.setChildMenu(9+4, Material.WRITTEN_BOOK, "Liste", new Menu((keys.size()-1)/9+1, "DR99 - Locations - Liste", true)
						.setEntries(0, keys, key -> switch(LocationsConfig.getLocation(key).getWorld().getEnvironment()) {
							case NORMAL -> Material.GRASS_BLOCK;
							case NETHER -> Material.NETHERRACK;
							case THE_END -> Material.END_STONE;
							case CUSTOM -> Material.SHROOMLIGHT;
						}, key -> key, (key, event) -> ((Player) event.getWhoClicked()).performCommand("location get " + key))
				);
	}
	
	public static void setLocation(String name, Location location) {
		get().set("locations." + name, location);
		save();
	}
	public static void removeLocation(String name) {
		get().set("locations." + name, null);
		save();
	}
	public static boolean isSet(String name) {
		return get().isSet("locations." + name);
	}
	public static Location getLocation(String name) {
		return get().getLocation("locations." + name);
	}
	public static boolean renameLocation(String name, String newName) {
		Location loc = get().getLocation("locations." + name);
		if(loc != null) {
			get().set("locations." + name, null);
			get().set("locations." + newName, loc);
			return true;
		}
		else return false;
	}
	public static List<String> listLocations() {
		Collection<String> keys;
		try {
			keys = Objects.requireNonNull(get().getConfigurationSection("locations")).getKeys(false);
		}
		catch(NullPointerException e) {
			Chat.sendToConsole(new Text("Die locations-Config ist fehlerhaft. Bitte überprüfen!", Text.Type.ERROR));
			return List.of();
		}
		return keys.stream()
				.sorted((key0, key1) -> {
					int world = getLocation(key0).getWorld().getName().compareToIgnoreCase(getLocation(key1).getWorld().getName());
					return world == 0 ?
							key0.compareToIgnoreCase(key1) :
							world;
				}).toList();
	}
	
	
	
	
	private LocationsConfig() throws OutdatedConfigException {
		super("locations", "locations.yml");
	}
	
	@Override
	protected String update(String version) {
		if(version.equals("INDEV-1.1.2") || version.equals("INDEV-1.1.3")) {
			// Nothing to do here
			return "INDEV-1.2.0";
		}
		else if(version.equals("INDEV-1.2.0")) {
			getConfig().createSection("locations");
			for(String key : getConfig().getKeys(false)) {
				Location loc = getConfig().getLocation(key);
				if(loc != null) {
					if(loc.getWorld() == null) {
						List<World> ws = DyingRabbit99.get().getServer().getWorlds();
						if(!ws.isEmpty()) {
							loc.setWorld(ws.get(0));
							Chat.sendToConsole(new Text("Die Welt der location " + key + " wurde geändert. " +
									"Bitte, überprüfe, ob sie nach wie vor stimmt.", Text.Type.INFO));
						} else {
							throw new RuntimeException("Es existiert keine Server-Welt. WTF???");
						}
					}
					getConfig().set(key, null);
					getConfig().set("locations." + key, loc);
				}
			}
			return "INDEV-1.2.1";
		}
		else return null;
	}
}
