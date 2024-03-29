package me.old.li.commands.baseCommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import me.old.li.Config;
import me.old.li.Main;
import me.old.li.Utilss.Utils;
import me.old.li.commands.BaseCommand;
import me.old.li.files.Items;
import me.old.li.files.Saves;
import me.old.li.files.Strings;

public class LotteryItemReload implements BaseCommand {

	@Override
	public void execute(CommandSender sender, String[] args) {
//		Player p = (Player) sender;
//		if (!p.hasPermission("lotteryitem.commands.reload")) {
//			p.sendMessage(Config.MESSAGE_NO_PERMISSION);
//			return;
//		}
		// reload all
		if (args.length == 1) {
			Main.getInstance().reloadConfig();
			Utils.loadConfig(Config.class);
			Saves.reloadConfig();
			Items.reloadConfig();
			Utils.sendPluginMessage(sender, Config.MESSAGE_RELOAD_SUCCESSFULLY);
			Utils.replaceAllPlayerLottery();
			return;
		}
		if (args[1].equalsIgnoreCase("config")) {
			Main.getInstance().reloadConfig();
			Utils.loadConfig(Config.class);
			Utils.sendPluginMessage(sender, Config.MESSAGE_RELOAD_SUCCESSFULLY);
			Utils.replaceAllPlayerLottery();
			return;
		}
		if (args[1].equalsIgnoreCase("saves")) {
			Saves.reloadConfig();
			Utils.sendPluginMessage(sender, Config.MESSAGE_RELOAD_SUCCESSFULLY);
			Utils.replaceAllPlayerLottery();
			return;
		}
		if (args[1].equalsIgnoreCase("items")) {
			Items.reloadConfig();
			Utils.sendPluginMessage(sender, Config.MESSAGE_RELOAD_SUCCESSFULLY);
			Utils.replaceAllPlayerLottery();
			return;
		}
		if (args[1].equalsIgnoreCase("strings")) {
			Strings.reloadConfig();
			Utils.loadConfig(Config.class);
			Utils.sendPluginMessage(sender, Config.MESSAGE_RELOAD_SUCCESSFULLY);
			Utils.replaceAllPlayerLottery();
			return;
		}
		showHelp(sender);
	}

	@Override
	public void showHelp(CommandSender sender) {
		Utils.sendPluginMessage(sender, "&cUsage: /li reload [config|saves|items|strings]");
	}

	@Override
	public List<String> getCompleteList(String[] str) {
		if (str.length == 1) {
			String[] args = { "config", "saves", "items", "strings" };
			List<String> list = new ArrayList<>();
			for (String s : args)
				if (s.startsWith(str[0].toLowerCase()))
					list.add(s);
			return list;
		}
		return null;
	}

	@Override
	public String description() {
		return " &3/li reload [config|saves|items|strings] &7-重新讀取檔案";
	}

}
