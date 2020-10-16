package me.old.li.commands.baseCommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.old.li.Config;
import me.old.li.Main;
import me.old.li.commands.BaseCommand;
import me.old.li.ui.ManagerPage;

public class LotteryItemManager implements BaseCommand {

	private Main instance;

	public LotteryItemManager(Main instance) {
		this.instance = instance;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		if (!p.hasPermission("lotteryitem.commands.manager")) {
			p.sendMessage(Config.MESSAGE_NO_PERMISSION);
			return;
		}
		ManagerPage mp = new ManagerPage(Config.GUI_MANAGER_TITLE, 6, instance);
		mp.open((Player) sender);
	}

	@Override
	public void showHelp(CommandSender sender) {
	}

	@Override
	public String description() {
		return " &3/li manager(m) &7-抽獎物管理介面";
	}

}
