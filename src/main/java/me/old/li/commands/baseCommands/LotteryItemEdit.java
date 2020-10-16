package me.old.li.commands.baseCommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.old.li.Config;
import me.old.li.LotteryItem;
import me.old.li.Main;
import me.old.li.Utilss.Utils;
import me.old.li.commands.BaseCommand;
import me.old.li.files.Saves;
import me.old.li.ui.EditLIPage;

public class LotteryItemEdit implements BaseCommand {

	private Main instance;

	public LotteryItemEdit(Main instance) {
		this.instance = instance;
	}

	// args = {edit, <id>}
	@Override
	public void execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		if (!p.hasPermission("lotteryitem.commands.edit")) {
			p.sendMessage(Config.MESSAGE_NO_PERMISSION);
			return;
		}
		if (args.length == 1) {
			showHelp(sender);
			return;
		}
		boolean inConfig = false;
		LotteryItem li = instance.getServerLotteryItems().getLotteryItem(args[1]);
		if (li == null) {
			li = (LotteryItem) Saves.getConfig().get(args[1]);
			inConfig = true;
		}

		if (li == null) {
			Utils.sendPluginMessage(sender, Config.MESSAGE_LOTTERYITEM_NOT_EXISTS);
			return;
		}

		EditLIPage editPage = new EditLIPage(Config.GUI_EDIT_TITLE, 6, inConfig ? li.clone() : li);
		editPage.open((Player) sender);
	}

	@Override
	public List<String> getCompleteList(String[] args) {
		List<String> idList = new ArrayList<>();
		idList.addAll(Saves.getConfig().getKeys(false));
		for (LotteryItem li : instance.getServerLotteryItems().getLotteryItems())
			idList.add(li.getItemId());

		if (args.length == 1) {
			List<String> list = new ArrayList<>();
			for (String s : idList)
				if (s.startsWith(args[0]))
					list.add(s);

			return list;
		}
		return null;
	}

	@Override
	public void showHelp(CommandSender sender) {
		Utils.sendPluginMessage(sender, "&cUsage: /li edit <ID>");
	}

	@Override
	public String description() {
		return " &3/li edit <ID> &7-編輯抽獎物";
	}

}
