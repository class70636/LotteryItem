package me.old.li.ui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.old.li.Config;
import me.old.li.InputType;
import me.old.li.LotteryItem;
import me.old.li.Main;
import me.old.li.ServerLotteryItems;
import me.old.li.Utilss.Utils;

public abstract class InputHandler {

	private Player p;
	private Page page;
	private InputType inputType;
	private int runTime;

	public InputHandler(Player p, Page page, InputType inputType) {
		this.p = p;
		this.page = page;
		this.inputType = inputType;
		this.runTime = Config.SETTINGS_INPUT_TIME;
	}

	public InputHandler enable() {
		page.it = new InputTask(page, new InputListener(), runTime);
		page.it.runTaskTimer(Main.getInstance(), 0L, 20L);
		Utils.sendPluginMessage(p, Config.MESSAGE_INPUT_NOTICE.replace("{seconds}", String.valueOf(runTime)));
		return this;
	}

	abstract boolean setValue(String msg);

	class InputListener implements Listener {
		@EventHandler
		void inputing(AsyncPlayerChatEvent e) {
			Player player = e.getPlayer();
			if (!player.equals(p))
				return;

			e.setCancelled(true);
			String message = e.getMessage();

			if (message.equalsIgnoreCase("cancel")) {
				Utils.sendPluginMessage(player, Config.MESSAGE_INPUT_CANCEL);
				Bukkit.getScheduler().runTask(Main.getInstance(), () -> page.it.shutDown());
				return;
			}

			if (!message.matches(inputType.getReg())) {
				Utils.sendPluginMessage(player, Config.MESSAGE_INPUT_WRONG);
				return;
			}

			if (!setValue(message))
				return;

			if (page instanceof ManagerPage) {
				Bukkit.getScheduler().runTask(Main.getInstance(), () -> page.it.shutDownWithoutOpenPage());

				ServerLotteryItems sli = Main.getInstance().getServerLotteryItems();
				List<LotteryItem> liList = sli.getLotteryItems();
				liList.add(new LotteryItem(message));

				LotteryItem li = sli.getLotteryItem(message);

				EditLIPage editPage = new EditLIPage(Config.GUI_CREATE_TITLE, 6, li);
//				editPage.setLotteryItem(sli.getLotteryItem(message));
				Bukkit.getScheduler().runTask(Main.getInstance(), () -> page.openNextPage(editPage, player));
				return;
			}

			Bukkit.getScheduler().runTask(Main.getInstance(), () -> page.it.shutDown());
		}
	}

}
