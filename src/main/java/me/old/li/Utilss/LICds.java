package me.old.li.Utilss;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.old.li.Main;

public class LICds {

	private Map<String, Integer> liList;
	private Map<String, BukkitTask> runnables;

	public LICds() {
		liList = new HashMap<>();
		runnables = new HashMap<>();
	}

	public boolean hasLotteryItem(String key) {
		return liList.containsKey(key);
	}

	public void addLotteryItem(String id, int cds) {
		liList.put(id, cds);
		// Run task
		runnables.put(id, new BukkitRunnable() {

			@Override
			public void run() {
				int value = liList.get(id);
				if (value <= 0) {
					liList.remove(id);
					this.cancel();
					runnables.remove(id);
					return;
				}
				liList.replace(id, value - 1);
			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 0L, 20L));
	}

	public int getCountDown(String key) {
		return liList.get(key);
	}

	public void removeLotteryItem(String key) {
		if (!liList.containsKey(key))
			return;
		liList.remove(key);
		runnables.get(key).cancel();
		runnables.remove(key);
	}

}
