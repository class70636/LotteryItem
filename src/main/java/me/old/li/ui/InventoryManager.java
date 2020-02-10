package me.old.li.ui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.PluginManager;

import me.old.li.Main;

public class InventoryManager {

	private final Main plugin;
	private final PluginManager manager;

	private final Map<Player, Page> playerSession;

	public InventoryManager(Main plugin) {
		this.plugin = plugin;
		this.manager = Bukkit.getPluginManager();
		this.playerSession = new HashMap<>();
	}

	public void enable() {
		manager.registerEvents(new invListener(), plugin);
	}

	public Page getPlayerSession(Player p) {
		return playerSession.get(p);
	}

	public void setPlayerSession(Player p, Page page) {
		if (page == null)
			this.playerSession.remove(p);
		else
			this.playerSession.put(p, page);
	}

	class invListener implements Listener {
		@EventHandler
		void onClick(InventoryClickEvent e) {
			Player p = (Player) e.getWhoClicked();
			if (!playerSession.containsKey(p))
				return;

			int rawSlot = e.getRawSlot();
			
			Page page = playerSession.get(p);
			if (page.buttons.containsKey(rawSlot))
				page.buttons.get(rawSlot).execute(p, e);

			if (rawSlot >= p.getOpenInventory().getTopInventory().getSize() && e.isShiftClick()) {
				e.setCancelled(true);
			}
		}

		@EventHandler
		void onClose(InventoryCloseEvent e) {
			Player p = (Player) e.getPlayer();

			if (!playerSession.containsKey(p))
				return;

			Page page = playerSession.get(p);
			if (page.inputing)
				return;

			page.onClose(e);
			
		}
	}

}
