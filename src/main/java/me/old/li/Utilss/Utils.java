package me.old.li.Utilss;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;
import me.old.li.Config;
import me.old.li.LotteryItem;
import me.old.li.Main;
import me.old.li.files.Items;
import me.old.li.files.Saves;
import me.old.li.files.Strings;
import me.old.li.optionObjects.Gift;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Utils {

	public static void sendPluginMessage(CommandSender sender, String message) {
		sender.sendMessage(
				Config.SETTINGS_PLUGIN_PREFIX + " §r" + ChatColor.translateAlternateColorCodes('&', message));
	}

	public static void sendActionBar(Player p, String message) {
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
	}

	public static boolean isNullableItem(ItemStack is) {
		return (is == null || is.getType() == Material.AIR);
	}

	public static void loadConfig(Class<?> clazz) {

		for (String key : Main.getInstance().getConfig().getKeys(true)) {
			String temp = key.toUpperCase().replace(".", "_");
			try {
				Field f = clazz.getDeclaredField(temp);
				try {
					Object o = transColor(Main.getInstance().getConfig().get(key));
					f.set(new Config(), o);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					continue;
				}
			} catch (NoSuchFieldException | SecurityException e) {
				// e.printStackTrace();
				continue;
			}
		}

		for (String key : Strings.getConfig().getKeys(true)) {
			String temp = key.toUpperCase().replace(".", "_");
			try {
				Field f = clazz.getDeclaredField(temp);
				try {
					Object o = transColor(Strings.getConfig().get(key));
					f.set(new Config(), o);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					continue;
				}
			} catch (NoSuchFieldException | SecurityException e) {
				// e.printStackTrace();
				continue;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static Object transColor(Object o) {
		Object temp = o;
		if (o.getClass() == String.class) {
			temp = ChatColor.translateAlternateColorCodes('&', (String) o);
		} else if (o.getClass() == ArrayList.class) {
			List<String> list = Arrays.asList(((List<String>) o).toArray(new String[0]));
			for (int i = 0; i < list.size(); i++)
				list.set(i, ChatColor.translateAlternateColorCodes('&', list.get(i)));
			temp = list;
		}
		return temp;
	}

	public static String getItemName(ItemStack is) {
		String name = is.getType().name();
		if (is.hasItemMeta() && is.getItemMeta().hasDisplayName())
			return is.getItemMeta().getDisplayName();
		for (String key : Items.getConfig().getKeys(false)) {
			if (key.toUpperCase().equals(name))
				return Items.getConfig().getString(key);
		}
		return name;
	}

	public static String getBooleanSymbol(boolean b) {
		return b ? "§a§l✔" : "§c✘";
	}

	public static String getReasons(LotteryItem li) {
		StringBuilder sb = new StringBuilder();
		if (!li.hasLiName())
			sb.append(Config.REASON_NO_NAME + "\n");
		if (!li.hasLiLore())
			sb.append(Config.REASON_NO_LORE + "\n");
		if (li.getGifts().size() == 0)
			sb.append(Config.REASON_NO_GIFTS);
		return sb.toString();
	}

	public static void replacePlayerLotteryItem(LotteryItem li) {
		for (Player p : Bukkit.getOnlinePlayers())
			replaceSinglePlayer(p, li);
	}

	public static void replaceSinglePlayer(Player p, LotteryItem li) {
		String liId = li.getItemId();
		// Clear cds
		if (Main.liCds.containsKey(p) && Main.liCds.get(p).hasLotteryItem(liId))
			Main.liCds.get(p).removeLotteryItem(liId);

		for (int i = 0; i < p.getInventory().getSize(); i++) {
			ItemStack is = p.getInventory().getItem(i);
			if (Utils.isNullableItem(is))
				continue;
			NBTItem nbti = new NBTItem(is);
			if (!nbti.hasKey("liKey"))
				continue;
			String itemId = nbti.getString("liKey");
			if (!liId.equals(itemId))
				continue;

			// replace item
			ItemStack replacedItem = li.buildItem();
			replacedItem.setAmount(is.getAmount());
			p.getInventory().setItem(i, replacedItem);
		}
	}

	public static void replaceAllPlayerLottery() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			for (int i = 0; i < p.getInventory().getSize(); i++) {
				ItemStack is = p.getInventory().getItem(i);
				if (Utils.isNullableItem(is))
					continue;
				if (!LIApi.isLotteryItem(is))
					continue;
				String liKey = Utils.getLiKey(is);
				if (!Saves.getConfig().contains(liKey))
					continue;

				String key = Utils.getLiKey(is);
				LotteryItem li = (LotteryItem) Saves.getConfig().get(key);

				ItemStack li_item = li.buildItem();
				li_item.setAmount(is.getAmount());

				p.getInventory().setItem(i, li_item);
			}
		}
	}

	public static String getLiKey(ItemStack is) {
		NBTItem nbti = new NBTItem(is);
		if (!nbti.hasKey("liKey"))
			return null;
		return nbti.getString("liKey");
	}

	public static void sortGifts(List<Gift> gifts, int order) {
		Collections.sort(gifts, new Comparator<Gift>() {
			@Override
			public int compare(Gift g1, Gift g2) {
				int x = (int) (((g2.getChance() - g1.getChance()) * 100000) / 100);
				int y = (int) (((g1.getChance() - g2.getChance()) * 100000) / 100);
				int z = g1.getItemName().compareTo(g2.getItemName());
				double d1 = Double.valueOf(g1.getAmount().split("-")[0]);
				double d2 = Double.valueOf(g2.getAmount().split("-")[0]);
				int w = (int) (((d1 - d2) * 100000) / 100);

				if (w == 0) {
					int l1 = g1.getAmount().split("-").length;
					int l2 = g2.getAmount().split("-").length;
					if (l1 != l2)
						w = l1 - l2;
					else {
						d1 = l1 > 1 ? Double.valueOf(g1.getAmount().split("-")[1]) : d1;
						d2 = l2 > 1 ? Double.valueOf(g2.getAmount().split("-")[1]) : d2;
						w = (int) (((d1 - d2) * 100000) / 100);
					}
				}

				switch (order) {
				case 1:
					return x;
				case 2:
					return y;
				case 3:
					return z;
				case 4:
					if (x == 0) {
						if (z == 0)
							return w;
						return z;
					}
					return x;
				case 5:
					if (y == 0) {
						if (z == 0)
							return w;
						return z;
					}
					return y;
				default:
					return z;
				}
			}
		});
	}

	public static String transItemToJson(ItemStack item) {
		NBTItem nbti = new NBTItem(item);
		return nbti.toString();
	}

}
