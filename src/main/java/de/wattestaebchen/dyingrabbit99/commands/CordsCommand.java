package de.wattestaebchen.dyingrabbit99.commands;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import de.wattestaebchen.dyingrabbit99.files.Coordinates;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CordsCommand extends DR99Command {
	
	public CordsCommand() {
		
	}
	
	
	@Override
	public String getUsage() {
		return "/cords (add <name> [<x> <y> <z>]) / (remove <name>) / (get <name>) / list";
	}
	
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
											Coordinates.addCords(args[1], p.getLocation().getWorld().getName(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ());
											if(overwritten) DyingRabbit99.sendMessage(sender, "Eintrag erfolgreich überschrieben.", DyingRabbit99.MessageType.SUCCESS);
											else DyingRabbit99.sendMessage(sender, "Eintrag erfolgreich erstellt.", DyingRabbit99.MessageType.SUCCESS);
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
																	String world = (sender instanceof Player p) ? p.getLocation().getWorld().getName() : "";
																	Coordinates.addCords(args[1], world, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
																	if(overwritten) DyingRabbit99.sendMessage(sender, "Eintrag erfolgreich überschrieben.", DyingRabbit99.MessageType.SUCCESS);
																	else DyingRabbit99.sendMessage(sender, "Eintrag erfolgreich erstellt.", DyingRabbit99.MessageType.SUCCESS);
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
										DyingRabbit99.sendMessage(sender, "Der Eintrag wurde erfolgreich gelöscht.", DyingRabbit99.MessageType.SUCCESS);
									}
									else {
										DyingRabbit99.sendMessage(sender, "Es existiert kein Eintrag mit diesem Namen.", DyingRabbit99.MessageType.ERROR);
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
							StringBuilder sb = new StringBuilder("Liste aller gespeicherten Orte:");
							for(String key : keys) {
								sb.append("\n").append(key);
							}
							DyingRabbit99.sendMessage(sender, sb.toString(), DyingRabbit99.MessageType.DEFAULT);
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
									String[] cords = Coordinates.getCords(args[1]);
									if(cords == null) {
										DyingRabbit99.sendMessage(sender, "Es existiert kein Eintrag mit diesem Namen.", DyingRabbit99.MessageType.ERROR);
										return true;
									}
									
									int x = Integer.parseInt(cords[1]);
									int y = Integer.parseInt(cords[2]);
									int z = Integer.parseInt(cords[3]);
									
									if(cords[0].equals("")) {
										DyingRabbit99.sendMessage(sender, ("Der Punkt " + args[1] + " befindet sich bei den Koordinaten:\nx: " +cords[1]+ ", y: " +cords[2]+ ", z: " +cords[3]), DyingRabbit99.MessageType.DEFAULT);
										return true;
									}
									
									Location loc = new Location(DyingRabbit99.get().getServer().getWorld(cords[0]), x, y, z);
									
									String msg = "Der Punkt " + args[1] + " befindet sich bei den Koordinaten:\nWelt: " + cords[0] + ", x: " +cords[1]+ ", y: " +cords[2]+ ", z: " +cords[3];
									if(sender instanceof Player p && p.getLocation().getWorld().equals(loc.getWorld())) {
										int directDistance = (int) p.getLocation().distance(loc);
										msg += "\nDu bist " + directDistance + (directDistance==1 ? " Block" : " Blöcke") + " davon entfernt.";
									}
									DyingRabbit99.sendMessage(sender, msg, DyingRabbit99.MessageType.DEFAULT);
									
									return true;
								}
								@Override public Argument getNext() { return null; }
							};
						}
					}					
				};
			}
			@Override public Argument getNext() {
				return null;
			}
		};
	}
	
}
