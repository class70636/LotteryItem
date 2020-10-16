package me.old.li.commands.baseCommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import me.old.li.Config;
import me.old.li.LotteryItem;
import me.old.li.Main;
import me.old.li.Utilss.Utils;
import me.old.li.commands.BaseCommand;
import me.old.li.files.Saves;

public class LotteryItemRemove implements BaseCommand {

	private Main instance;

	public LotteryItemRemove(Main instance) {
		this.instance = instance;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
//		Player p = (Player) sender;
//		if (!p.hasPermission("lotteryitem.commands.remove")) {
//			p.sendMessage(Config.MESSAGE_NO_PERMISSION);
//			return;
//		}
		if (args.length == 1) {
			showHelp(sender);
			return;
		}
		boolean inConfig = false;
		LotteryItem li = instance.getServerLotteryItems().getLotteryItem(args[1]);
		if (li == null) {
			li = (LotteryItem) Saves.getConfig().get(args[1]);
			if (li == null) {
				Utils.sendPluginMessage(sender, Config.MESSAGE_LOTTERYITEM_NOT_EXISTS);
				return;
			}
			inConfig = true;
		}
		Utils.sendPluginMessage(sender, Config.MESSAGE_LOTTERYITEM_REMOVED.replace("{id}", args[1]));
		if (inConfig) {
			Saves.getConfig().set(args[1], null);
			Saves.saveConfig();
		} else
			instance.getServerLotteryItems().getLotteryItems().remove(li);
	}

	@Override
	public void showHelp(CommandSender sender) {
		Utils.sendPluginMessage(sender, "&cUsage: /li remove <ID>");
	}

	@Override
	public List<String> getCompleteList(String[] str) {
		List<String> idList = new ArrayList<>();
		idList.addAll(Saves.getConfig().getKeys(false));
		for (LotteryItem li : instance.getServerLotteryItems().getLotteryItems())
			idList.add(li.getItemId());

		if (str.length == 1) {
			List<String> list = new ArrayList<>();
			for (String s : idList)
				if (s.startsWith(str[0]))
					list.add(s);

			return list;
		}
		return null;
	}

	@Override
	public String description() {
		return " &3/li remove <ID> &7-刪除抽獎物";
	}

}
