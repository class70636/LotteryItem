package me.old.li.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

public interface BaseCommand {
	
	public void execute(CommandSender sender, String[] args);
	
	public void showHelp(CommandSender sender);
	
	default public List<String> getCompleteList(String str) {
		return null;
	}

}
