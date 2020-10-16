package me.old.li.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

public interface BaseCommand {
	
	public void execute(CommandSender sender, String[] args);
	
	public void showHelp(CommandSender sender);
	
	public String description();
	
	default public List<String> getCompleteList(String[] args) {
		return null;
	}

}
