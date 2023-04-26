package de.wattestaebchen.dyingrabbit99.commands2;

@CommandAnnotation(label="location", alias = "loc", subCommands = "add|remove|get|list", subCommandsRequired = true)
public class LocationCmd extends Cmd {
	
	@Override
	protected boolean execute() {
		return false;
	}
	
	
	@SubCommandAnnotation(label = "add")
	public void add(String name) {
		System.out.println("loc add " + name);
	}
	
	@SubCommandAnnotation(label = "remove")
	public void remove(String name) {
		System.out.println("loc remove " + name);
	}
	
	@SubCommandAnnotation(label = "get")
	public void get(String name) {
		System.out.println("loc get " + name);
	}
	
	@SubCommandAnnotation(label = "list")
	public void list() {
		System.out.println("loc list");
	}
	
}
