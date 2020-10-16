package me.old.li.commands.baseCommands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.old.li.Config;
import me.old.li.LotteryItem;
import me.old.li.Main;
import me.old.li.ServerLotteryItems;
import me.old.li.Utilss.LIApi;
import me.old.li.Utilss.Utils;
import me.old.li.commands.BaseCommand;
import me.old.li.ui.EditLIPage;

public class LotteryItemCreate implements BaseCommand {

	private Main instance;

	public LotteryItemCreate(Main instance) {
		this.instance = instance;
	}

	// args = {create, <ID>}
	@Override
	public void execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		if (!p.hasPermission("lotteryitem.commands.create")) {
			p.sendMessage(Config.MESSAGE_NO_PERMISSION);
			return;
		}
		if (args.length == 1) {
			showHelp(sender);
			return;
		}
		if (LIApi.isExists(args[1])) {
			Utils.sendPluginMessage(sender, Config.MESSAGE_LOTTERYITEM_EXISTS);
			return;
		}
		ServerLotteryItems sli = instance.getServerLotteryItems();
		List<LotteryItem> liList = sli.getLotteryItems();
		liList.add(new LotteryItem(args[1]));

		EditLIPage editPage = new EditLIPage(Config.GUI_CREATE_TITLE, 6, LIApi.getServerLottery(args[1]));
//		editPage.setLotteryItem(sli.getLotteryItem(args[1]));
		editPage.open((Player) sender);
	}

	@Override
	public void showHelp(CommandSender sender) {
		Utils.sendPluginMessage(sender, "&cUsage: /li create <ID>");
	}

	@Override
	public String description() {
		return " &3/li create <ID> &7-創建抽獎物";
	}

}
