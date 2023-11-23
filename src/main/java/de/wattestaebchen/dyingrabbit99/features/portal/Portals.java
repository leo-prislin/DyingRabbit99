package de.wattestaebchen.dyingrabbit99.features.portal;

import de.wattestaebchen.dyingrabbit99.features.config.CustomConfig;
import de.wattestaebchen.dyingrabbit99.features.config.OutdatedConfigException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.ArrayList;

public final class Portals extends CustomConfig {
	
	private static Portals instance;
	
	private static ArrayList<Portal> portals;
	
	private Portals() throws OutdatedConfigException {
		super("portals", "portals.yml");
	}
	
	public static void init() throws OutdatedConfigException {
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
	
	
	@Override
	protected String update(String version) {
		if(version.equals("INDEV-1.1.2") || version.equals("INDEV-1.1.3")) {
			// Nothing to do here
			return "INDEV-1.2.0";
		}
		return null;
	}
}
