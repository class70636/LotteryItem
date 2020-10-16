package me.old.li.commands.baseCommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.old.li.Config;
import me.old.li.InputType;
import me.old.li.LotteryItem;
import me.old.li.Utilss.Utils;
import me.old.li.commands.BaseCommand;
import me.old.li.files.Saves;

public class LotteryItemGive implements BaseCommand {

	@Override
	public void execute(CommandSender sender, String[] args) {
		// typed /li get
		if (args.length < 3) {
			showHelp(sender);
			return;
		}

		String given_name = args[1];
		String given_id = args[2];

		Player givenPlayer = Bukkit.getPlayerExact(given_name);
		if (givenPlayer == null) {
			Utils.sendPluginMessage(sender, "&c指定玩家不存在");
			return;
		}

		LotteryItem li = (LotteryItem) Saves.getConfig().get(given_id);
		if (li == null) {
			Utils.sendPluginMessage(sender, Config.MESSAGE_LOTTERYITEM_NOT_EXISTS);
			return;
		}

		int amount = 1;

		if (args.length >= 4) {
			if (!args[3].matches(InputType.ANY_NUMBER.getReg())) {
				Utils.sendPluginMessage(sender, Config.MESSAGE_INPUT_WRONG);
				showHelp(sender);
				return;
			}
			int tempAmount = Integer.valueOf(args[3]);
			if (tempAmount > 1)
				amount = tempAmount;
		}

		ItemStack item = li.buildItem();
		item.setAmount(amount);

		// give
		givenPlayer.getInventory().addItem(item);

	}

	@Override
	public List<String> getCompleteList(String[] args) {
		if (args.length == 2) {
			List<String> list = new ArrayList<>();
			for (String key : Saves.getConfig().getKeys(false)) {
				if (key.startsWith(args[1]))
					list.add(key);
			}
			return list;
		}
		return null;
	}

	@Override
	public void showHelp(CommandSender sender) {
		Utils.sendPluginMessage(sender, "&cUsage: /li give <PLAYER_ID> <ID> [amount]");
	}

	@Override
	public String description() {
		return " &3/li give <PLAYER_ID> <ID> [amount] &7-給予指定玩家抽獎物";
	}

}
