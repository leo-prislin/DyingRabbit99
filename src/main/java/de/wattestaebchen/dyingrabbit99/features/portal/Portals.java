package de.wattestaebchen.dyingrabbit99.features.portal;

import de.wattestaebchen.dyingrabbit99.features.config.CustomConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.ArrayList;

public final class Portals extends CustomConfig {
	
	private static Portals instance;
	
	private static ArrayList<Portal> portals;
	
	private Portals() {
		super("portals", "portals.yml");
	}
	
	public static void init() {
		ConfigurationSerialization.registerClass(RealPortal.class);
		ConfigurationSerialization.registerClass(ImaginaryPortal.class);
		instance = new Portals();
		portals = get().getObject("portals", ArrayList.class, new ArrayList<Portal>());
	}
	
	private static YamlConfiguration get() {
		return instance.getConfig();
	}
	public static void save() {
		get().set("portals", portals);
		instance.saveConfig();
	}
	
	public static ArrayList<Portal> getPortals() {
		return Portals.portals;
	}
	
}
