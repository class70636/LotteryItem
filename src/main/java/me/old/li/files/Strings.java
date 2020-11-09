package me.old.li.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.old.li.Main;

public class Strings {

	private static FileConfiguration customConfig = null;
	private static File customConfigFile = null;

	public static void reloadConfig() {
		if (customConfigFile == null) {
			customConfigFile = new File(Main.getInstance().getDataFolder() + File.separator + "files" + File.separator,
					"strings.yml");
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
			customConfigFile = new File(Main.getInstance().getDataFolder() + File.separator + "files" + File.separator,
					"strings.yml");
		}
		InputStream is = null;
		OutputStream os = null;

		if (!customConfigFile.exists()) {
			// read source file into InputStream
			is = Main.getInstance().getResource("strings.yml");

			// write
			try {
//				customConfigFile.createNewFile();
				os = new FileOutputStream(customConfigFile);

				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = is.read(bytes)) != -1) {
					os.write(bytes, 0, read);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

}
