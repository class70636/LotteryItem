package me.old.li.files;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.old.li.Main;

public class Items {
	
	private static FileConfiguration customConfig = null;
	private static File customConfigFile = null;

	public static void reloadConfig() {
		if (customConfigFile == null) {
			customConfigFile = new File(Main.getInstance().getDataFolder() + "/files/", "items.yml");
		}
		if (!customConfigFile.exists()) {
			saveDefaultConfig();
		}
		customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

	}

	public static FileConfiguration getConfig() {
		if (customConfig == null) {
			reloadConfig();
		}
		return customConfig;
	}

	public static void saveDefaultConfig() {
		if (customConfigFile == null) {
			customConfigFile = new File(Main.getInstance().getDataFolder(), "items.yml");
		}
		if (!customConfigFile.exists()) {
			try {
				customConfigFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
