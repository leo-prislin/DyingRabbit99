package de.wattestaebchen.dyingrabbit99.commands2;

import org.bukkit.command.CommandSender;

@CommandAnnotation(labels = {"location", "loc"})
public class LocationCmd extends Cmd {	
	
	@SubCommandAnnotation(label = "add")
	@RequiresInfo(info = {"sender"})
	public boolean add(CommandSender sender, String name) {
		System.out.println("loc add " + name + sender);
		return true;
	}
	@SubCommandAnnotation(label = "add")
	public boolean add(String name, Integer x, Integer y, Integer z) {
		System.out.println("loc add " + name + ", " + x);
		return true;
	}
	
	@SubCommandAnnotation(label = "remove")
	public boolean remove(String name) {
		System.out.println("loc remove " + name);
		return true;
	}
	
	@SubCommandAnnotation(label = "get")
	public boolean get(String name) {
		System.out.println("loc get " + name);
		return true;
	}
	
	@SubCommandAnnotation(label = "list")
	public boolean list() {
		System.out.println("loc list");
		return true;
	}
	
}
