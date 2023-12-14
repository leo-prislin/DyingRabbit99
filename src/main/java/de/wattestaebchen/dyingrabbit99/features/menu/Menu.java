package de.wattestaebchen.dyingrabbit99.features.menu;

import de.wattestaebchen.dyingrabbit99.DyingRabbit99;
import de.wattestaebchen.dyingrabbit99.features.config.Config;
import de.wattestaebchen.dyingrabbit99.features.find.FindCmd;
import de.wattestaebchen.dyingrabbit99.features.locations.LocationsConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Menu implements InventoryHolder {
	
	private final boolean hasBar;
	public boolean hasBar() {
		return this.hasBar;
	}
	private final Inventory inventory;
	@Override
	public @NotNull Inventory getInventory() {
		return this.inventory;
	}
	
	public Menu(int rows, String title, boolean addBar) {
		this.hasBar = addBar;
		this.inventory = DyingRabbit99.get().getServer().createInventory(this, addBar ? (rows*9+9) : (rows*9), Component.text(title));
		for(int i = 0; i < rows*9; i++) {
			ItemStack itemStack = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
			itemStack.editMeta(meta -> meta.displayName(Component.text("")));
			inventory.setItem(i, itemStack);
		}
		if(addBar) {
			for(int i = rows*9; i < rows*9+9; i++) {
				ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
				itemStack.editMeta(meta -> meta.displayName(Component.text("")));
				inventory.setItem(i, itemStack);
			}
		}
	}
	
	public Menu setEntry(int slot, Material icon, String name, Consumer<InventoryClickEvent> onClick) {
		
		ItemStack itemStack = new ItemStack(icon);
		if(name != null) itemStack.editMeta(meta -> meta.displayName(Component.text(name)));
		getInventory().setItem(slot, itemStack);
		DyingRabbit99.registerEvent(new Listener() {
			@EventHandler
			public void onInventoryClick(InventoryClickEvent event) {
				if(event.getClickedInventory() == inventory) {
					event.setCancelled(true);
					if(event.getSlot() == slot) onClick.accept(event);
				}
			}
		});
		
		return this;
	}
	public Menu setEntry(int slot, ItemStack itemStack, String name, Consumer<InventoryClickEvent> onClick) {
		
		if(name != null) itemStack.editMeta(meta -> meta.displayName(Component.text(name)));
		getInventory().setItem(slot, itemStack);
		DyingRabbit99.registerEvent(new Listener() {
			@EventHandler
			public void onInventoryClick(InventoryClickEvent event) {
				if(event.getClickedInventory() == inventory) {
					event.setCancelled(true);
					if(event.getSlot() == slot) onClick.accept(event);
				}
			}
		});
		
		return this;
	}
	public Menu setEntry(int slot, Material icon, String name, Menu forward) {
		return setEntry(slot, icon, name, event -> {
			forward.open(event.getWhoClicked());
		});
	}
	public Menu setChildMenu(int slot, Material icon, String name, Menu child) {
		if(!child.hasBar()) throw new IllegalArgumentException("Only menus with bar can be used as child menu!");
		child.setEntry(child.getInventory().getSize()-9, Material.STRUCTURE_VOID, "<--", this);
		return setEntry(slot, icon, name, child);
	}
	public Menu setCommandExecution(int slot, Material icon, String name, String command, boolean closeInvOnCLick) {
		return setEntry(slot, icon, name, event -> {
			if(closeInvOnCLick) event.getWhoClicked().closeInventory();
			((Player) event.getWhoClicked()).performCommand(command);
		});
	}
	public <T> Menu setEntries(int slotFrom, Collection<T> collection, Function<T, Material> getMaterial, Function<T, String> getName, BiConsumer<T, InventoryClickEvent> onClick) {
		for(T element : collection) {
			setEntry(slotFrom++, getMaterial.apply(element), getName.apply(element), event -> onClick.accept(element, event));
		}
		return this;
	}
	public <T> Menu setEntries(int slotFrom, Collection<T> collection, Function<T, ItemStack> getItemStack, BiConsumer<T, InventoryClickEvent> onClick) {
		for(T element : collection) {
			setEntry(slotFrom++, getItemStack.apply(element), null, event -> onClick.accept(element, event));
		}
		return this;
	}
	
	public InventoryView open(HumanEntity entity) {
		entity.closeInventory();
		return entity.openInventory(getInventory());
	}
	
	
	public static Menu getMainMenu() {
		return new Menu(3, "DyingRabbit99 - Menü", false)
				.setChildMenu(9+2, Material.COMMAND_BLOCK, "Config", Config.getMenu())
				.setChildMenu(9+4, Material.PLAYER_HEAD, "Spieler", FindCmd.getMenu()) // TODO
				.setChildMenu(9+6, Material.RESPAWN_ANCHOR, "Locations", LocationsConfig.getMenu());
	}
	
}
