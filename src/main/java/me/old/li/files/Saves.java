package me.old.li.files;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.old.li.Main;

public class Saves {

	private static FileConfiguration customConfig = null;
	private static File customConfigFile = null;

	public static void reloadConfig() {
		if (customConfigFile == null) {
			customConfigFile = new File(Main.getInstance().getDataFolder() + "/files/", "saves.yml");
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

	public static void saveConfig() {
		if (customConfig == null || customConfigFile == null) {
			return;
		}
		try {
			getConfig().save(customConfigFile);
		} catch (IOException ex) {
			Main.getInstance().getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
		}
	}

	public static void saveDefaultConfig() {
		if (customConfigFile == null) {
			customConfigFile = new File(Main.getInstance().getDataFolder(), "saves.yml");
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
