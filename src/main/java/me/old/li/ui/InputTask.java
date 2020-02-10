package me.old.li.ui;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import me.old.li.Config;
import me.old.li.Main;
import me.old.li.Utilss.Utils;

public class InputTask extends BukkitRunnable {

	private Page page;
	private Listener listener;
	private int count;

	public InputTask(Page page, Listener inputListener, int count) {
		this.page = page;
		this.count = count;
		page.inputing = true;
		page.p.closeInventory();
		Bukkit.getPluginManager().registerEvents(this.listener = inputListener, Main.getInstance());
	}

	@Override
	public void run() {
		if (--count == 0)
			shutDown();
		else {
			String msg = Config.MESSAGE_INPUT_COUNTDOWN.replace("{seconds}", String.valueOf(count));
			Utils.sendActionBar(page.p, msg);
		}
	}

	public void shutDown() {
		closeInputStatus();
		this.cancel();
	}
	
	public void shutDownWithoutOpenPage() {
		page.inputing = false;
		this.cancel();
		HandlerList.unregisterAll(listener);
	}

	private void closeInputStatus() {
		page.inputing = false;
		page.open();
		HandlerList.unregisterAll(listener);
	}

}
