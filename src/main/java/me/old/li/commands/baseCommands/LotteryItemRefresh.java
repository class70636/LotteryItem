package me.old.li.commands.baseCommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.old.li.Config;
import me.old.li.Utilss.Utils;
import me.old.li.commands.BaseCommand;

public class LotteryItemRefresh implements BaseCommand {

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		if (!p.hasPermission("lotteryitem.commands.refresh")) {
			p.sendMessage(Config.MESSAGE_NO_PERMISSION);
			return;
		}
		Utils.replaceAllPlayerLottery();
		Utils.sendPluginMessage(p, Config.MESSAGE_REFRESH_SUCCESSFULLY);
	}

	@Override
	public void showHelp(CommandSender sender) {

	}

}
