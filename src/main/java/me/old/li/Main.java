package me.old.li;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.old.li.Utilss.Hooker;
import me.old.li.Utilss.LICds;
import me.old.li.Utilss.Utils;
import me.old.li.commands.Command;
import me.old.li.files.Items;
import me.old.li.files.Saves;
import me.old.li.files.Strings;
import me.old.li.optionObjects.Consoprize;
import me.old.li.optionObjects.Gift;
import me.old.li.optionObjects.Key;
import me.old.li.optionObjects.Money;
import me.old.li.ui.InventoryManager;

public class Main extends JavaPlugin implements Listener {

	private static Main instance;
	public static String nmsver;
	public static Map<Player, LICds> liCds = new HashMap<>();

	public static Main getInstance() {
		return instance;
	}

	private final InventoryManager invManager = new InventoryManager(this);

	public InventoryManager getInvManager() {
		return invManager;
	}

	private final Command commnd = new Command(this);

	private final ServerLotteryItems sli = new ServerLotteryItems();

	public ServerLotteryItems getServerLotteryItems() {
		return sli;
	}

	@SuppressWarnings("unchecked")
	public void onEnable() {
		instance = this;

		nmsver = Bukkit.getServer().getClass().getPackage().getName();
		nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);

		// Hook depen plugins
		if (!Hooker.setupEconomy()) {
			getLogger().severe("\033[31m您的伺服器未安裝經濟插件，插件未開啟。\033[m");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		Hooker.setupPermissions();
		// Check nbt api
		if (getServer().getPluginManager().getPlugin("NBTAPI") == null) {
			getLogger().severe("\033[31m您的伺服器未安裝NBTAPI插件，插件未開啟。\033[m");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		// 讀取config
		File config = new File(getDataFolder(), "config.yml");
		if (!config.exists())
			saveDefaultConfig();
		reloadConfig();

		// 創建資料夾
		File folder = new File(getDataFolder() + "/files/");
		if (!folder.exists())
			folder.mkdir();

		Strings.reloadConfig();
		Utils.loadConfig(Config.class);

		// 註冊gui api
		invManager.enable();

		// 註冊指令
		commnd.enable();

		// 註冊抽獎事件
		Bukkit.getPluginManager().registerEvents(new ServerEvent(), this);

		Class<?>[] registerClasses = { Consoprize.class, Gift.class, Key.class, Money.class, LotteryItem.class };
		for (Class<?> clazz : registerClasses)
			ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) clazz);
		Saves.reloadConfig();

		Items.reloadConfig();

	}

	public void onDisable() {

	}

}
