package me.old.li.Utilss;

import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;
import me.old.li.LotteryItem;
import me.old.li.Main;
import me.old.li.files.Saves;

public class LIApi {

	public static boolean isExists(String id) {
//		return Main.getInstance().getServerLotteryItems().getLotteryItem(id) != null || Saves.getConfig().contains(id);
		if (Main.getInstance().getServerLotteryItems().getLotteryItem(id) != null)
			return true;
		return isConfigExists(id);
	}

	public static boolean isConfigExists(String id) {
		return Saves.getConfig().contains(id);
	}

	public static boolean isExists(LotteryItem li) {
		String id = li.getItemId();
		return isExists(id);
	}

	public static LotteryItem getServerLottery(String id) {
		for (LotteryItem l : Main.getInstance().getServerLotteryItems().getLotteryItems())
			if (l.getItemId().equals(id))
				return l;
		return null;
	}

	public static boolean isLotteryItem(ItemStack is) {
		NBTItem nbti = new NBTItem(is);
		return nbti.hasKey("liKey");
	}

}
