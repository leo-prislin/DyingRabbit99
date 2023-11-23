package de.wattestaebchen.dyingrabbit99.features.config;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;

public class OutdatedVersionException extends RuntimeException {
	
	public final String configName;
	public final String version;
	
	
	public OutdatedVersionException(String configName, String version) {
		super();
		this.configName = configName;
		this.version = version;
		DyingRabbit99.errorOnEnable = true;
	}
	
	@Override
	public String getMessage() {
		return "In der " + (configName==null ? "" : configName+"-") + "Config sind veraltete Daten gespeichert, " +
				"zu denen die aktuelle Version von DyingRabbit99 nicht rückwärtskompatibel ist. (v" + version + ")";
	}
	
	
}
