package de.wattestaebchen.dyingrabbit99.commands;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import de.wattestaebchen.dyingrabbit99.files.Coordinates;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class CordsCommand extends DR99Command {
	
	public CordsCommand() {
		
	}
	
	
	@Override
	public String getUsage() {
		return "/cords (add <name> [<x> <y> <z>]) / (remove <name>) / (get <name>) / list";
	}
	
	public TextComponent usage() {
		return Component.text().content("/cords ").clickEvent(ClickEvent.suggestCommand("/cords"))
				.append(Component.text().content(" (add <name> [<x> <y> <z>]) /").clickEvent(ClickEvent.suggestCommand("/cords add ")))
				.append(Component.text().content(" (remove <name> /").clickEvent(ClickEvent.suggestCommand("/cords remove ")))
				.append(Component.text().content(" (get <name>) /").clickEvent(ClickEvent.suggestCommand("/cords get ")))
				.append(Component.text().content(" list").clickEvent(ClickEvent.suggestCommand("/cords list ")))
				.build();
	}
	
	@Override
	protected Argument getArg0() {
		// add/remove/list/get
		return new Option() {
			@Override public Argument[] getOptions() {
				return new Argument[] {
					new Text() {
						// add
						@Override public String getText() { return "add"; }
						@Override public Argument getNext() {
							// <name>
							return new Variable() {
								@Override public Type getType() { return Type.STRING; }								
								@Override public Argument getNext() {
									// [<x> <y> <z>]
									return new Optional() {
										@Override
										public boolean mustBeInvoked(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
											return !(sender instanceof Player);
										}
										@Override public boolean onCall(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
											boolean overwritten = Coordinates.isSet(args[1]);
											
											Player p = (Player) sender;
											Coordinates.addCords(args[1], p.getLocation());
											
											if(overwritten) DyingRabbit99.sendMessage(sender, Component.text().content("Eintrag erfolgreich überschrieben.").build(), DyingRabbit99.MessageType.SUCCESS);
											else DyingRabbit99.sendMessage(sender, Component.text().content("Eintrag erfolgreich erstellt.").build(), DyingRabbit99.MessageType.SUCCESS);
											
											return true;
										}
										@Override public Argument getInnerArg() {
											// <x>
											return new Variable() {
												@Override public Type getType() { return Type.INT; }
												@Override public Argument getNext() {
													// <y>
													return new Variable() {
														@Override public Type getType() { return Type.INT; }
														@Override public Argument getNext() {
															// <z>
															return new Variable() {
																@Override public Type getType() { return Type.INT; }
																@Override public Argument getNext() {
																	return null;
																}
																
																@Override public boolean onCall(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
																	boolean overwritten = Coordinates.isSet(args[1]);
																	
																	Location location;
																	if(sender instanceof Player p) {
																		location = p.getLocation();
																	}
																	else {
																		location = new Location(null, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
																	}
																	Coordinates.addCords(args[1], location);
																	
																	if(overwritten) DyingRabbit99.sendMessage(sender, Component.text().content("Eintrag erfolgreich überschrieben.").build(), DyingRabbit99.MessageType.SUCCESS);
																	else DyingRabbit99.sendMessage(sender, Component.text().content("Eintrag erfolgreich erstellt.").build(), DyingRabbit99.MessageType.SUCCESS);
																	
																	return true;
																}
															};
														}
													};
												}
											};
										}
									};
								}
							};
						}
					},
					new Text() {
						// remove
						@Override public String getText() { return "remove"; }
						@Override public Argument getNext() {
							// <name>
							return new Variable() {
								@Override public Type getType() { return Type.STRING; }
								@Override public boolean onCall(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
									
									if(Coordinates.get().isSet(args[1])) {
										Coordinates.removeCords(args[1]);
										DyingRabbit99.sendMessage(sender, Component.text().content("Der Eintrag wurde erfolgreich gelöscht.").build(), DyingRabbit99.MessageType.SUCCESS);
									}
									else {
										DyingRabbit99.sendMessage(sender, Component.text().content("Es existiert kein Eintrag mit diesem Namen.").build(), DyingRabbit99.MessageType.ERROR);
									}
									return true;
								}
								@Override public Argument getNext() { return null; }
							};
						}
					},
					new Text() {
						// list
						@Override public String getText() { return "list"; }
						
						@Override public boolean onCall(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
							Set<String> keys = Coordinates.listCords();
							
							TextComponent.Builder tc = Component.text().content("Liste aller gespeicherten Orte:");
							for(String key : keys) {
								tc
									.appendNewline()
									.append(Component.text().content(key).color(DyingRabbit99.MessageType.CLICKABLE.getColor()).clickEvent(ClickEvent.runCommand("cords get " + key)));
							}
							DyingRabbit99.sendMessage(sender, tc.build(), DyingRabbit99.MessageType.DEFAULT);
							
							return true;
						}
						
						@Override public Argument getNext() { return null; }
					},
					new Text() {
						// get
						@Override public String getText() { return "get"; }
						@Override public Argument getNext() {
							// <name>
							return new Variable() {
								@Override public Type getType() { return Type.STRING; }
								@Override public boolean onCall(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
									Location location = Coordinates.getCords(args[1]);
									if(location == null) {
										DyingRabbit99.sendMessage(sender, Component.text().content("Es existiert kein Eintrag mit diesem Namen.").build(), DyingRabbit99.MessageType.ERROR);
										return true;
									}
									
									if(location.getWorld() == null) {
										DyingRabbit99.sendMessage(sender, Component.text().content("Der Punkt " + args[1] + " befindet sich bei den Koordinaten:\nx: " +location.getBlockX()+ ", y: " +location.getBlockY()+ ", z: " + location.getBlockZ()).build(), DyingRabbit99.MessageType.DEFAULT);
										return true;
									}
									
									String msg = "Der Punkt " + args[1] + " befindet sich bei den Koordinaten:\nWelt: " + location.getWorld().getEnvironment() + ", x: " +location.getBlockX()+ ", y: " +location.getBlockY()+ ", z: " +location.getBlockZ();
									if(sender instanceof Player p && p.getLocation().getWorld().equals(location.getWorld())) {
										int directDistance = (int) p.getLocation().distance(location);
										msg += "\nDu bist " + directDistance + (directDistance==1 ? " Block" : " Blöcke") + " davon entfernt.";
									}
									DyingRabbit99.sendMessage(sender, Component.text().content(msg).build(), DyingRabbit99.MessageType.DEFAULT);
									
									return true;
								}
								@Override public Argument getNext() { return null; }
							};
						}
					}					
				};
			}
		};
	}
	
}
