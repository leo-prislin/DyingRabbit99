package de.wattestaebchen.dyingrabbit99.features.portal;

import de.wattestaebchen.dyingrabbit99.features.config.CustomConfig;
import de.wattestaebchen.dyingrabbit99.features.config.OutdatedConfigException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.ArrayList;

public final class PortalsConfig extends CustomConfig {
	
	private static PortalsConfig instance;
	
	private static ArrayList<Portal> portals;
	
	public static void init() throws OutdatedConfigException {
		ConfigurationSerialization.registerClass(RealPortal.class);
		ConfigurationSerialization.registerClass(ImaginaryPortal.class);
		instance = new PortalsConfig();
		portals = get().getObject("portals", ArrayList.class, new ArrayList<Portal>());
	}
	
	private static YamlConfiguration get() {
		return instance.getConfig();
	}
	public static void save() {
		get().set("portals", portals);
		instance.saveConfig();
	}
	
	
	static ArrayList<Portal> getPortals() {
		return PortalsConfig.portals;
	}
	
	
	
	
	
	private PortalsConfig() throws OutdatedConfigException {
		super("portals", "portals.yml");
	}
	
	@Override
	protected String update(String version) {
		if(version.equals("INDEV-1.1.2") || version.equals("INDEV-1.1.3")) {
			// Nothing to do here
			return "INDEV-1.2.0";
		}
		else if(version.equals("INDEV-1.2.0")) {
			return "INDEV-1.2.1";
		}
		else return null;
	}
	
}
