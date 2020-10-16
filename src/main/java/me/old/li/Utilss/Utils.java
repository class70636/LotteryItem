package me.old.li.Utilss;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.old.li.Config;
import me.old.li.LotteryItem;
import me.old.li.Main;
import me.old.li.files.Items;
import me.old.li.files.Saves;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Utils {

	public static void sendPluginMessage(CommandSender sender, String message) {
		sender.sendMessage(Config.SETTINGS_PLUGIN_PREFIX + " §r" + ChatColor.translateAlternateColorCodes('&', message));
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

//	public static String transItemToJson(ItemStack itemStack) {
//		Class<?> craftItemStackClazz = ReflectionUtil.getOBCClass("inventory.CraftItemStack");
//		Method asNMSCopyMethod = ReflectionUtil.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);
//
//		Class<?> nmsItemStackClazz = ReflectionUtil.getNMSClass("ItemStack");
//		Class<?> nbtTagCompoundClazz = ReflectionUtil.getNMSClass("NBTTagCompound");
//		Method saveNmsItemStackMethod = ReflectionUtil.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);
//
//		Object nmsNbtTagCompoundObj;
//		Object nmsItemStackObj;
//		Object itemAsJsonObject;
//
//		try {
//			nmsNbtTagCompoundObj = nbtTagCompoundClazz.newInstance();
//			nmsItemStackObj = asNMSCopyMethod.invoke(null, itemStack);
//			itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
//		} catch (Throwable t) {
//			Bukkit.getLogger().log(Level.SEVERE, "failed to serialize itemstack to nms item", t);
//			return null;
//		}
//
//		// Return a string representation of the serialized object
//		return itemAsJsonObject.toString();
//	}

	public static String transItemToJson(ItemStack item) {
		NBTItem nbti = new NBTItem(item);
		return nbti.toString();
	}

}
