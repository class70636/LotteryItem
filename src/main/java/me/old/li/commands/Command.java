package me.old.li.commands;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.old.li.Config;
import me.old.li.Main;
import me.old.li.commands.baseCommands.LotteryItemCreate;
import me.old.li.commands.baseCommands.LotteryItemEdit;
import me.old.li.commands.baseCommands.LotteryItemGet;
import me.old.li.commands.baseCommands.LotteryItemGive;
import me.old.li.commands.baseCommands.LotteryItemManager;
import me.old.li.commands.baseCommands.LotteryItemRefresh;
import me.old.li.commands.baseCommands.LotteryItemReload;
import me.old.li.commands.baseCommands.LotteryItemRemove;
import net.md_5.bungee.api.ChatColor;

public class Command implements CommandExecutor, TabCompleter {

	private Main instance;
	private Map<String, BaseCommand> commands;
	private String str, title;

	public Command(Main instance) {
		this.instance = instance;
		this.commands = new LinkedHashMap<>();

		commands.put("create", new LotteryItemCreate(instance));
		commands.put("edit", new LotteryItemEdit(instance));
		commands.put("get", new LotteryItemGet());
		commands.put("give", new LotteryItemGive());
		commands.put("remove", new LotteryItemRemove(instance));
		commands.put("manager", new LotteryItemManager(instance));
		commands.put("refresh", new LotteryItemRefresh());
		commands.put("reload", new LotteryItemReload());
		try {
			str = new String(
					Base64.getDecoder().decode("wqc4ICAgICBbRGV2ZWxvcGVkIGJ5IGNsYXNzNzA2MzUsIHhnZCB8IHZlcnNpb24gJXNd"),
					"UTF-8");
			title = new String(Base64.getDecoder().decode("wqc3PT09WyDCpzZMb3R0ZXJ5SXRlbcKnNyBdPT09"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void enable() {
		instance.getCommand("lotteryitem").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String lable, String[] args) {
//		if (!(sender instanceof Player))
//			return true;
		if (args.length == 0) {
			showHelp(sender);
			return true;
		}

		String index = args[0].equalsIgnoreCase("m") ? "manager" : args[0].toLowerCase();
		if (commands.get(index) != null) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (!p.hasPermission("lotteryitem.commands." + index)) {
					p.sendMessage(Config.MESSAGE_NO_PERMISSION);
					return true;
				}
			}
			commands.get(index).execute(sender, args);
			return true;
		}
		showHelp(sender);
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String lable,
			String[] args) {
		if (!(sender instanceof Player))
			return null;
		Player p = (Player) sender;
		if (!p.hasPermission("lotteryitem.commands.base"))
			return null;

		List<String> temp = new ArrayList<>();
		temp.addAll(commands.keySet());
		// type /li {args}
		if (args.length == 1) {
			if (args[0].length() == 0)
				return temp;
			List<String> list = new ArrayList<>();
			for (String str : temp) {
				if (str.startsWith(args[0].toLowerCase()))
					list.add(str);
			}
			return list;
		}
		// type /li <selectors> {args}
		else if (args.length >= 2) {
			if (!commands.containsKey(args[0]))
				return null;
			String[] tempStr = new String[args.length - 1];
			for (int i = 0; i < tempStr.length; i++) {
				tempStr[i] = args[i + 1];
			}
			return commands.get(args[0]).getCompleteList(tempStr);
		}
		return null;
	}

	private void showHelp(CommandSender sender) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!p.hasPermission("lotteryitem.commands.base")) {
				p.sendMessage(Config.MESSAGE_NO_PERMISSION);
				return;
			}
		}
		sender.sendMessage(title);
		for (BaseCommand bc : commands.values()) {
			if ((!(sender instanceof Player)) && (bc instanceof PlayerCommand))
				continue;
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', bc.description()));
		}
		sender.sendMessage(String.format(str, instance.getDescription().getVersion()));
	}

}
