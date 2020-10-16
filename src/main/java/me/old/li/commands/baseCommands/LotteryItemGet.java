package me.old.li.commands.baseCommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.old.li.Config;
import me.old.li.InputType;
import me.old.li.LotteryItem;
import me.old.li.Utilss.Utils;
import me.old.li.commands.BaseCommand;
import me.old.li.files.Saves;

public class LotteryItemGet implements BaseCommand {

	public LotteryItemGet() {
	}

	// {get,<id>}
	@Override
	public void execute(CommandSender sender, String[] args) {
//		Player p = (Player) sender;
//		if (!p.hasPermission("lotteryitem.commands.get")) {
//			p.sendMessage(Config.MESSAGE_NO_PERMISSION);
//			return;
//		}
		if (args.length == 1) {
			showHelp(sender);
			return;
		}
		LotteryItem li = (LotteryItem) Saves.getConfig().get(args[1]);
		if (li == null) {
			Utils.sendPluginMessage(sender, Config.MESSAGE_LOTTERYITEM_NOT_EXISTS);
			return;
		}

		int amount = 1;
		if (args.length > 2 && args[2].matches(InputType.INTEGER_NOT_INCLUDE_ZERO.getReg()))
			amount = Integer.valueOf(args[2]);

		ItemStack item = li.buildItem();
		item.setAmount(amount);

		((Player) sender).getInventory().addItem(item);
	}

	@Override
	public List<String> getCompleteList(String[] args) {
		if (args.length == 1) {
			List<String> list = new ArrayList<>();
			for (String key : Saves.getConfig().getKeys(false)) {
				if (key.startsWith(args[0]))
					list.add(key);
			}
			return list;
		}
		return null;
	}

	@Override
	public void showHelp(CommandSender sender) {
		Utils.sendPluginMessage(sender, "&cUsage: /li get <ID> [amount]");
	}

	@Override
	public String description() {
		return " &3/li get <ID> [amount] &7-取得抽獎物";
	}

}
